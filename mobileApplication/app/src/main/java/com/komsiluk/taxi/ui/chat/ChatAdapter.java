package com.komsiluk.taxi.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.komsiluk.taxi.BuildConfig;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.data.remote.chat.ChatMessage;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ME = 1;
    private static final int TYPE_OTHER = 2;

    private List<ChatMessage> messages = new ArrayList<>();
    private final Long myId;
    private String otherProfilePath;

    public ChatAdapter(Long myId) {
        this.myId = myId;
    }

    public void setOtherProfilePath(String path) {
        this.otherProfilePath = path;
    }

    public void setMessages(List<ChatMessage> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage message) {
        this.messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSenderId().equals(myId)) {
            return TYPE_ME;
        } else {
            return TYPE_OTHER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ME) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_me, parent, false);
            return new MyMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_other, parent, false);
            return new OtherMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);
        if (holder instanceof MyMessageViewHolder) {
            ((MyMessageViewHolder) holder).bind(msg);
        } else {
            ((OtherMessageViewHolder) holder).bind(msg, otherProfilePath);
        }
    }

    @Override
    public int getItemCount() { return messages.size(); }

    private static String formatSentAt(String rawTime) {
        if (rawTime == null || !rawTime.contains("T")) {
            return rawTime != null ? rawTime : "";
        }
        try {
            String timePart = rawTime.split("T")[1];
            return timePart.substring(0, 5);
        } catch (Exception e) {
            return rawTime;
        }
    }

    // ViewHolder za MOJE poruke (Samo tekst i vreme)
    static class MyMessageViewHolder extends RecyclerView.ViewHolder {
        TextView content, time;
        MyMessageViewHolder(View v) {
            super(v);
            content = v.findViewById(R.id.tvMessageContent);
            time = v.findViewById(R.id.tvMessageTime);
        }
        void bind(ChatMessage m) {
            content.setText(m.getContent());
            time.setText(formatSentAt(m.getSentAt()));
        }
    }

    // ViewHolder za TUĐE poruke (Tekst, vreme i AVATAR)
    static class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        TextView content, time;
        ImageView avatar;

        OtherMessageViewHolder(View v) {
            super(v);
            content = v.findViewById(R.id.tvMessageContent);
            time = v.findViewById(R.id.tvMessageTime);
            avatar = v.findViewById(R.id.ivOtherAvatar);
        }

        void bind(ChatMessage m, String profilePath) {
            content.setText(m.getContent());
            time.setText(formatSentAt(m.getSentAt()));

            // Učitavanje slike sagovornika
            if (avatar != null) {
                if (profilePath != null && !profilePath.trim().isEmpty()) {
                    String cleanPath = profilePath.trim();
                    if (cleanPath.startsWith("/")) {
                        cleanPath = cleanPath.substring(1);
                    }

                    String imageUrl = "http://" + BuildConfig.IP_ADDR + ":8081/" + cleanPath;

                    Glide.with(itemView.getContext())
                            .load(imageUrl)
                            .circleCrop()
                            .placeholder(R.drawable.ic_profile)
                            .error(R.drawable.ic_profile)
                            .into(avatar);
                } else {
                    Glide.with(itemView.getContext())
                            .load(R.drawable.ic_profile)
                            .circleCrop()
                            .into(avatar);
                }
            }
        }
    }
}