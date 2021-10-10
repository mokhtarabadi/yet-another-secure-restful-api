package com.mokhtarabadi.yasra.android;

import android.app.Activity;
import android.os.Bundle;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.internal.http.CallServerInterceptor;
import okhttp3.internal.tls.OkHostnameVerifier;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;
import org.apache.http.conn.ssl.StrictHostnameVerifier;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;

import static com.mokhtarabadi.yasra.android.Constants.BASE_URL;

@Slf4j
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // very dangerous, because I used self-signed certificate for server-side, I trust all certificates here, in production must use valid certificate
        try {
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());

            class GzipRequestInterceptor implements Interceptor {
                @Override public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
                        return chain.proceed(originalRequest);
                    }

                    Request compressedRequest = originalRequest.newBuilder()
                            .header("Content-Encoding", "gzip")
                            .method(originalRequest.method(), gzip(originalRequest.body()))
                            .build();
                    return chain.proceed(compressedRequest);
                }

                private RequestBody gzip(final RequestBody body) {
                    return new RequestBody() {
                        @Override public MediaType contentType() {
                            return body.contentType();
                        }

                        @Override public long contentLength() {
                            return -1; // We don't know the compressed length in advance!
                        }

                        @Override public void writeTo(BufferedSink sink) throws IOException {
                            BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                            body.writeTo(gzipSink);
                            gzipSink.close();
                        }
                    };
                }
            }

            class EncryptRequestInterceptor implements Interceptor {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    return null;
                }

            }

            class DecryptResponseInterceptor implements Interceptor {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .header("Cache-Control", "max-age=60")
                            .build();
                }
            };

            class SignatureRequestInterceptor implements Interceptor {

                @Override
                public Response intercept(Chain chain) throws IOException {
                    return null;
                }
            }

            OkHttpClient client = new OkHttpClient.Builder()
                    .dns(hostname -> {
                        if (hostname.equals("example.com")) {
                            return Collections.singletonList(Inet4Address.getByName("192.168.1.7"));
                        }
                        return Dns.SYSTEM.lookup(hostname);
                    })
                    .hostnameVerifier((hostname, session) -> {
                        if (hostname.equals("example.com")) {
                            return true;
                        }
                        return OkHostnameVerifier.INSTANCE.verify(hostname, session);
                    })
                    /*.certificatePinner(new CertificatePinner.Builder()
                            .add("example.com", "sha256/H+YeNU/uf8UTlXkr0cSHNST695MHznggZthuuKdudBQ=")
                            .build())*/
                    .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                    .addInterceptor(new EncryptRequestInterceptor())
                    .addInterceptor(new SignatureRequestInterceptor())
                    .addInterceptor(new GzipRequestInterceptor())
                    .build();

            Request request = new Request.Builder()
                    .url(BASE_URL + "/user/all")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    log.info("body: {}", response.body().string());

                    response.close();
                }
            });

        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

}