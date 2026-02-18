package com.komsiluk.taxi.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.data.remote.chat.ChatInbox;
import com.komsiluk.taxi.data.remote.chat.ChatService;
import com.komsiluk.taxi.databinding.FragmentAdminInboxBinding;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class AdminInboxActivity extends BaseNavDrawerActivity implements InboxAdapter.OnItemClickListener {

    private FragmentAdminInboxBinding inboxBinding;
    private InboxAdapter adapter;

    @Inject ChatService chatService;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_admin_inbox;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View contentView = findViewById(R.id.inboxContentRoot);
        inboxBinding = FragmentAdminInboxBinding.bind(contentView);

        setupRecyclerView();
        loadInbox();
    }

    private void setupRecyclerView() {
        adapter = new InboxAdapter(this);
        inboxBinding.rvInbox.setLayoutManager(new LinearLayoutManager(this));
        inboxBinding.rvInbox.setAdapter(adapter);
    }

    private void loadInbox() {
        inboxBinding.progressBarInbox.setVisibility(View.VISIBLE);
        chatService.getAdminInbox().enqueue(new Callback<List<ChatInbox>>() {
            @Override
            public void onResponse(Call<List<ChatInbox>> call, Response<List<ChatInbox>> response) {
                inboxBinding.progressBarInbox.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setItems(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ChatInbox>> call, Throwable t) {
                inboxBinding.progressBarInbox.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemClick(ChatInbox contact) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_RECEIVER_ID, contact.getUserId());
        intent.putExtra(ChatActivity.EXTRA_RECEIVER_NAME, contact.getFullName());
        intent.putExtra("extra_profile_path", contact.getProfilePicture());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInbox();
    }
}