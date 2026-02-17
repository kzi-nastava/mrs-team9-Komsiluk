package com.komsiluk.taxi.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.auth.AuthManager;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.databinding.FragmentChatBinding;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;
import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;

@AndroidEntryPoint
public class ChatActivity extends BaseNavDrawerActivity {

    public static final String EXTRA_RECEIVER_ID = "extra_receiver_id";
    public static final String EXTRA_RECEIVER_NAME = "extra_receiver_name";
    public static final String EXTRA_PROFILE_PATH = "extra_profile_path";

    private FragmentChatBinding chatBinding;
    private ChatViewModel viewModel;
    private ChatAdapter adapter;
    private Long receiverId;
    private String otherProfilePath;

    @Inject AuthManager authManager;
    @Inject SessionManager sessionManager;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Povezivanje sa unutrašnjim layoutom
        View contentView = findViewById(R.id.chatContentRoot);
        chatBinding = FragmentChatBinding.bind(contentView);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        // Redosled je bitan: prvo setupReceiver da izvučemo putanju slike
        setupReceiver();
        setupRecyclerView();
        setupKeyboardHandling();
        observeViewModel();

        chatBinding.btnSend.setOnClickListener(v -> sendMessage());

        viewModel.connect();

        Long myId = sessionManager.getUserId();
        String myRole = String.valueOf(authManager.getRole());

        if ("ADMIN".equals(myRole)) {
            viewModel.loadHistory(receiverId);
            viewModel.markAsRead(receiverId);
        } else {
            viewModel.loadHistory(myId);
            viewModel.markAsRead(7L);
        }
    }

    private void setupKeyboardHandling() {
        ViewCompat.setOnApplyWindowInsetsListener(chatBinding.chatContentRoot, (v, insets) -> {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            int bottomMenuPx = (int) (40 * getResources().getDisplayMetrics().density);

            if (imeInsets.bottom > 0) {
                // TASTATURA OTVORENA: Skloni padding sa inputa, podigni ceo ekran
                chatBinding.layoutInput.setPadding(
                        chatBinding.layoutInput.getPaddingLeft(),
                        chatBinding.layoutInput.getPaddingTop(),
                        chatBinding.layoutInput.getPaddingRight(),
                        0
                );
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, imeInsets.bottom);
            } else {
                // TASTATURA ZATVORENA: Vrati padding da input "leži" na meniju
                chatBinding.layoutInput.setPadding(
                        chatBinding.layoutInput.getPaddingLeft(),
                        chatBinding.layoutInput.getPaddingTop(),
                        chatBinding.layoutInput.getPaddingRight(),
                        bottomMenuPx
                );
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            }
            return insets;
        });

        chatBinding.rvChatMessages.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> {
            if (b < ob) scrollToBottom();
        });
    }

    private void setupReceiver() {
        long idFromIntent = getIntent().getLongExtra(EXTRA_RECEIVER_ID, -1);
        // IZVLAČIMO PUTANJU SLIKE IZ INTENTA
        otherProfilePath = getIntent().getStringExtra(EXTRA_PROFILE_PATH);

        if (idFromIntent != -1) {
            receiverId = idFromIntent;
            String name = getIntent().getStringExtra(EXTRA_RECEIVER_NAME);
            chatBinding.tvChatTitle.setText(name != null ? name : "Support");
        } else {
            receiverId = 7L;
            chatBinding.tvChatTitle.setText("Support Chat");
        }
    }

    private void setupRecyclerView() {
        adapter = new ChatAdapter(sessionManager.getUserId());
        // PROSLEĐUJEMO PUTANJU ADAPTERU DA BI MOGAO DA UČITA SLIKU
        adapter.setOtherProfilePath(otherProfilePath);

        chatBinding.rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        chatBinding.rvChatMessages.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.messages.observe(this, msgs -> {
            if (msgs != null) {
                adapter.setMessages(msgs);
                scrollToBottom();
            }
        });

        viewModel.newMessage.observe(this, msg -> {
            if (msg.getSenderId().equals(receiverId) || msg.getReceiverId().equals(receiverId)) {
                adapter.addMessage(msg);
                scrollToBottom();

                if (msg.getSenderId().equals(receiverId)) {
                    viewModel.markAsRead(sessionManager.getUserId());
                }
            }
        });
    }

    private void sendMessage() {
        String text = chatBinding.etMessageInput.getText().toString().trim();
        if (!text.isEmpty()) {
            viewModel.sendMessage(text, receiverId);
            chatBinding.etMessageInput.setText("");
        }
    }

    private void scrollToBottom() {
        chatBinding.rvChatMessages.postDelayed(() -> {
            if (adapter.getItemCount() > 0) {
                chatBinding.rvChatMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        }, 100);
    }
}