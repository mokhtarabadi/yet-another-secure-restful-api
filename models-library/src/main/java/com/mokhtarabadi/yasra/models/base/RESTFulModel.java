package com.mokhtarabadi.yasra.models.base;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RESTFulModel {

    @Expose
    private boolean success;

    @Expose
    private String errorMessage;

    @Expose
    private Object data;
}
