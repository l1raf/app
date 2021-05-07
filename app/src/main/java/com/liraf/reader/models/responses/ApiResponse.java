package com.liraf.reader.models.responses;

import java.io.IOException;

import retrofit2.Response;

public class ApiResponse<T> {

    public ApiResponse<T> create(Throwable error) {
        return new ApiErrorResponse<>(error.getMessage().equals("") ? error.getMessage() : "Unknown error\nCheck network connection");
    }

    public ApiResponse<T> create(Response<T> response) {
        if (response.isSuccessful()) {
            T body = response.body();

            if (body == null || response.code() == 204)
                return new ApiEmptyResponse<>();
            else
                return new ApiSuccessResponse<>(body);
        } else {
            String errorMsg;

            try {
                errorMsg = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
                errorMsg = response.message();
            }
            return new ApiErrorResponse<>(errorMsg);
        }
    }

    public static class ApiSuccessResponse<T> extends ApiResponse<T> {

        private final T body;

        ApiSuccessResponse(T body) {
            this.body = body;
        }

        public T getBody() {
            return body;
        }

    }

    public static class ApiErrorResponse<T> extends ApiResponse<T> {

        private final String errorMessage;

        ApiErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

    }

    public static class ApiEmptyResponse<T> extends ApiResponse<T> {
    }
}
