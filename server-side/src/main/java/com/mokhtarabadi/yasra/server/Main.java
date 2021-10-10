package com.mokhtarabadi.yasra.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mokhtarabadi.yasra.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import static com.mokhtarabadi.yasra.server.Constants.*;
import static spark.Spark.*;

@Slf4j
public class Main {

    public static void main(String[] args) {
        port(LISTEN_PORT);

        String keystorePath = System.getProperty("user.dir") + "/server-side/src/main/resources/server.keystore";
        log.info("path: {}", keystorePath);
        secure(keystorePath, "123456", null, null);

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().setLenient().create();

        // decrypt, check signature
        before((request, response) -> {

        });

        UserRepository userRepository = new UserRepository();
        post(PATH_CREATE_USER, (request, response) -> {
            return "OK";
        });

        get(PATH_GET_USER_BY_ID, (request, response) -> {
            return "OK";
        });

        get(PATH_GET_ALL_USERS, (request, response) -> {
            return "OK";
        });

        // encrypt
        after((request, response) -> {

        });

        after((request, response) -> {
            response.header("Content-Encoding", "gzip");
        });
    }
}
