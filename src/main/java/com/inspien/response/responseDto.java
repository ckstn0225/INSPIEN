package com.inspien.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class responseDto<T> {

    private boolean success;
    public T data;

    public static <T> responseDto<T> success(T data) {
        return new responseDto<>(true, data);
    }

    public static <T> responseDto<T> fail(String code, String message) {
        return new responseDto<>(false, null);
    }

}
