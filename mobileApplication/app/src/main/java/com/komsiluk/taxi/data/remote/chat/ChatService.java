package com.komsiluk.taxi.data.remote.chat;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ChatService {

    @GET("chat/history/{userId}")
    Call<List<ChatMessage>> getChatHistory(@Path("userId") Long userId);

    @GET("chat/inbox")
    Call<List<ChatInbox>> getAdminInbox();

    @PUT("chat/read/{userId}")
    Call<Void> markAsRead(@Path("userId") Long userId);
}