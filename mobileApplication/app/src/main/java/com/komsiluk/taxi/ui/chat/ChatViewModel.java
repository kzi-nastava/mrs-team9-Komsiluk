package com.komsiluk.taxi.ui.chat;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.komsiluk.taxi.data.remote.chat.ChatMessage;
import com.komsiluk.taxi.data.remote.chat.ChatService;
import com.komsiluk.taxi.data.remote.chat.WebSocketClient;
import com.komsiluk.taxi.data.session.SessionManager;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class ChatViewModel extends ViewModel {

    private final ChatService chatService;
    private final WebSocketClient webSocketClient;
    private final SessionManager sessionManager;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<List<ChatMessage>> _messages = new MutableLiveData<>();
    public LiveData<List<ChatMessage>> messages = _messages;

    private final MutableLiveData<ChatMessage> _newMessage = new MutableLiveData<>();
    public LiveData<ChatMessage> newMessage = _newMessage;

    @Inject
    public ChatViewModel(ChatService chatService, WebSocketClient webSocketClient, SessionManager sessionManager) {
        this.chatService = chatService;
        this.webSocketClient = webSocketClient;
        this.sessionManager = sessionManager;

        observeIncomingMessages();
    }

    public void loadHistory(Long otherId) {
        Log.d("CHAT_DEBUG", "Učitavam istoriju za korisnika: " + otherId);
        chatService.getChatHistory(otherId).enqueue(new retrofit2.Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(retrofit2.Call<List<ChatMessage>> call, retrofit2.Response<List<ChatMessage>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("CHAT_DEBUG", "Istorija učitana. Broj poruka: " + response.body().size());
                    _messages.postValue(response.body());
                } else {
                    Log.e("CHAT_DEBUG", "Greška pri učitavanju istorije: " + response.code());
                }
            }
            @Override
            public void onFailure(retrofit2.Call<List<ChatMessage>> call, Throwable t) {
                Log.e("CHAT_DEBUG", "Neuspešan Retrofit poziv: " + t.getMessage());
            }
        });
    }

    public void connect() {
        webSocketClient.connect();
    }

    private void observeIncomingMessages() {
        disposables.add(webSocketClient.getMessageObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(msg -> {
                    Log.d("CHAT_DEBUG", "Nova poruka stigla u ViewModel: " + msg.getContent());
                    _newMessage.setValue(msg);
                }, throwable -> {
                    Log.e("CHAT_DEBUG", "Greška u posmatranju socket poruka", throwable);
                }));
    }

    public void sendMessage(String content, Long receiverId) {
        Long myId = sessionManager.getUserId();
        if (myId != null) {
            Log.d("CHAT_DEBUG", "Šaljem poruku: " + content + " primaocu: " + receiverId);
            webSocketClient.sendMessage(content, myId, receiverId);
        }
    }

    public void markAsRead(Long userId) {
        chatService.markAsRead(userId).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) Log.d("CHAT_DEBUG", "Poruke markirane kao pročitane.");
            }
            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {}
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}