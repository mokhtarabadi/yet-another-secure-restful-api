package com.mokhtarabadi.yasra.server.controller;

import com.google.gson.Gson;
import com.mokhtarabadi.yasra.models.UserModel;
import com.mokhtarabadi.yasra.models.base.RESTFulModel;
import com.mokhtarabadi.yasra.server.repository.UserRepository;

import java.util.List;

public class UserController {

    private UserRepository repository;
    private Gson gson;

    public RESTFulModel createUser(Object data) {
        return RESTFulModel.builder()
                .success(true)
                //.data(repository.createUser(user))
                .build();
    }

    public RESTFulModel getUserById(int id) {
        UserModel user = repository.getUser(id);
        RESTFulModel.RESTFulModelBuilder builder = RESTFulModel.builder();
        if (user == null) {
            builder.success(false)
                    .errorMessage("user not found");
        } else {
            builder.success(true)
                    .data(user);
        }
        return builder.build();
    }

    public RESTFulModel getAllUsers() {
        return RESTFulModel.builder()
                .success(true)
                .data(repository.getAll())
                .build();
    }

}
