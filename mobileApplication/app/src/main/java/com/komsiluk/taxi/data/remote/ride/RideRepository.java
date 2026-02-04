package com.komsiluk.taxi.data.remote.ride;

import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class RideRepository {

    private final RideService api;

    @Inject
    public RideRepository(RideService api) {
        this.api = api;
    }

    public Result<RideResponse> orderRideSync(RideCreateRequest req) {
        try {
            Response<RideResponse> r = api.orderRide(req).execute();

            if (r.isSuccessful() && r.body() != null) {
                return Result.success(r.body());
            }

            String msg = "Request failed";
            if (r.errorBody() != null) {
                String body = r.errorBody().string();
                msg = extractMessage(body, msg);
            }
            return Result.error(msg);

        } catch (Exception e) {
            return Result.error("Network error");
        }
    }

    private String extractMessage(String body, String fallback) {
        try {
            JSONObject obj = new JSONObject(body);
            if (obj.has("message")) return obj.getString("message");
        } catch (Exception ignored) {}
        return fallback;
    }

    public static class Result<T> {
        public final T data;
        public final String error;

        private Result(T data, String error) {
            this.data = data;
            this.error = error;
        }

        public static <T> Result<T> success(T data) { return new Result<>(data, null); }
        public static <T> Result<T> error(String msg) { return new Result<>(null, msg); }
        public boolean isSuccess() { return data != null; }
    }
}
