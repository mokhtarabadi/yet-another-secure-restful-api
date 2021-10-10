package com.mokhtarabadi.yasra.models;

import com.google.gson.annotations.Expose;
import com.mokhtarabadi.yasra.models.base.RESTFulModel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
public class UserModel  {

    @Expose
    private int id;
    @Expose
    private String username;

    @Expose
    private String password;

    @Expose
    private String name;

}
