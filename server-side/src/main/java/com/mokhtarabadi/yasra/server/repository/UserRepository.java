package com.mokhtarabadi.yasra.server.repository;

import com.mokhtarabadi.yasra.models.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final static ArrayList<UserModel> USERS = new ArrayList<>();

    public UserRepository() {
        loadUsers();
    }

    private void loadUsers() {
        if (USERS.isEmpty()) {
            USERS.add(UserModel.builder()
                            .id(1)
                            .name("Mohammad Reza Mokhtarabadi")
                            .password("12345")
                            .username("mokhtarabadi")
                    .build());

            USERS.add(UserModel.builder()
                            .id(1)
                            .name("Hassan Rohani")
                            .password("kelid")
                            .username("hassan")
                    .build());
        }
    }

    public UserModel createUser(UserModel user) {
        user.setId(USERS.size() + 1);
        USERS.add(user);
        return user;
    }

    public UserModel getUser(int id) {
        for (UserModel user : USERS) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    public List<UserModel> getAll() {
        return USERS;
    }

}
