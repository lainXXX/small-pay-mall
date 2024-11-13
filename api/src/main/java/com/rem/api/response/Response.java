package com.rem.api.response;

import com.rem.types.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 7000723935764546321L;

    private String code;
    private String info;
    private T data;

    public static <T> Response<T> success(T data) {
        return Response.<T>builder()
                .code(Constants.ResponseCode.SUCCESS.getCode())
                .info(Constants.ResponseCode.SUCCESS.getInfo())
                .data(data)
                .build();
    }

    public static <T> Response<T> success() {
        return Response.<T>builder()
                .code(Constants.ResponseCode.SUCCESS.getCode())
                .info(Constants.ResponseCode.SUCCESS.getInfo())
                .build();
    }

    public static <T> Response<T> error() {
        return Response.<T>builder()
                .code(Constants.ResponseCode.UN_ERROR.getCode())
                .info(Constants.ResponseCode.UN_ERROR.getInfo())
                .build();
    }

    public static <T> Response<T> error(T data) {
        return Response.<T>builder()
                .code(Constants.ResponseCode.UN_ERROR.getCode())
                .info(Constants.ResponseCode.UN_ERROR.getInfo())
                .data(data)
                .build();
    }

    public static <T> Response<T> noLoginError() {
        return Response.<T>builder()
                .code(Constants.ResponseCode.NO_LOGIN.getCode())
                .info(Constants.ResponseCode.NO_LOGIN.getInfo())
                .build();
    }

}
