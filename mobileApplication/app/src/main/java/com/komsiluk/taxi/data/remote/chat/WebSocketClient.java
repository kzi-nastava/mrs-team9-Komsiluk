package com.komsiluk.taxi.data.remote.chat;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.komsiluk.taxi.BuildConfig;
import com.komsiluk.taxi.data.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompHeader;

@Singleton
public class WebSocketClient {

    private static final String TAG = "WebSocketClient";

    private static final String WS_URL = "ws://" + BuildConfig.IP_ADDR + ":8081/ws/websocket";

    private StompClient mStompClient;
    private CompositeDisposable compositeDisposable;
    private final Gson gson;
    private final SessionManager sessionManager;

    private final BehaviorSubject<ChatMessage> messageSubject = BehaviorSubject.create();

    @Inject
    public WebSocketClient(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.gson = new GsonBuilder().create();
        this.compositeDisposable = new CompositeDisposable();
    }

    @SuppressLint("CheckResult")
    public void connect() {
        if (mStompClient != null) {
            Log.d(TAG, "Čišćenje stare konekcije pre novog povezivanja...");
            disconnect();
        }

        String rawToken = sessionManager.getToken();
        if (rawToken == null) {
            Log.e(TAG, "Nema tokena, nemoguće povezivanje.");
            return;
        }

        String cleanToken = rawToken.replace("Bearer ", "").trim();
        String connectionUrl = WS_URL + "?token=" + cleanToken;

        Log.d(TAG, "Povezivanje na: " + connectionUrl);

        if (compositeDisposable == null || compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, connectionUrl);

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", "Bearer " + cleanToken));

        mStompClient.connect(headers);

        Disposable dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "Stomp connection OPENED");
                            subscribeToMessages();
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection ERROR", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            Log.d(TAG, "Stomp connection CLOSED");
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.e(TAG, "Stomp failed server heartbeat");
                            break;
                    }
                });

        compositeDisposable.add(dispLifecycle);
    }

    @SuppressLint("CheckResult")
    private void subscribeToMessages() {
        Disposable dispTopic = mStompClient.topic("/user/queue/messages")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "STIGLA PORUKA: " + topicMessage.getPayload());
                    try {
                        ChatMessage message = gson.fromJson(topicMessage.getPayload(), ChatMessage.class);
                        messageSubject.onNext(message);
                    } catch (Exception e) {
                        Log.e(TAG, "Greška pri parsiranju poruke", e);
                    }
                }, throwable -> {
                    Log.e(TAG, "Greška na subscribe-u", throwable);
                });

        compositeDisposable.add(dispTopic);
    }

    @SuppressLint("CheckResult")
    public void sendMessage(String content, Long senderId, Long receiverId) {
        if (mStompClient == null || !mStompClient.isConnected()) {
            Log.e(TAG, "Pokušaj slanja bez konekcije!");
            return;
        }

        ChatMessage msg = new ChatMessage();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setContent(content);
        msg.setType("FROM_USER");

        String jsonPayload = gson.toJson(msg);

        compositeDisposable.add(mStompClient.send("/app/chat/send", jsonPayload)
                .compose(applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "Poruka poslata: " + content);
                }, throwable -> {
                    Log.e(TAG, "Greška pri slanju", throwable);
                }));
    }

    public void disconnect() {
        Log.d(TAG, "Gasim WebSocket resurse...");
        if (mStompClient != null) {
            mStompClient.disconnect();
            mStompClient = null;
        }
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    public io.reactivex.Observable<ChatMessage> getMessageObservable() {
        return messageSubject;
    }

    private io.reactivex.CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}