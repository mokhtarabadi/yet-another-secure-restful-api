package com.mokhtarabadi.yasra.server;

public class Constants {

    public static final int LISTEN_PORT = 9090;

    public static final String SECURE_PASSWORD = "must_be_a_secure_password";
    public static final String SECURE_SALT = "must_be_a_random_generated_salt";
    public static final String SECURE_HASHING_KEY = "must_be_a_random_generated_long_enough_key";

    public static final String PATH_CREATE_USER = "/user/create";
    public static final String PATH_GET_USER_BY_ID = "/user/:id";
    public static final String PATH_GET_ALL_USERS = "/user/all";

}
