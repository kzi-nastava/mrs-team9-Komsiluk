package com.komsiluk.taxi.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.komsiluk.taxi.BuildConfig;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.data.remote.chat.ChatInbox;
import java.util.ArrayList;
import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxViewHolder> {

    private List<ChatInbox> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ChatInbox contact);
    }

    public InboxAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<ChatInbox> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_inbox, parent, false);
        return new InboxViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxViewHolder holder, int position) {
        ChatInbox item = items.get(position);
        holder.name.setText(item.getFullName());
        holder.lastMsg.setText(item.getLastMessage());
        String rawTime = item.getLastMessageTime(); // npr. "2026-02-16T14:30:00"
        if (rawTime != null && rawTime.contains("T")) {
            try {
                String timeOnly = rawTime.split("T")[1].substring(0, 5);
                holder.time.setText(timeOnly);
            } catch (Exception e) {
                holder.time.setText(rawTime);
            }
        } else {
            holder.time.setText(rawTime);
        }

        String profilePath = item.getProfilePicture();

        if (profilePath != null && !profilePath.trim().isEmpty()) {
            profilePath = profilePath.trim();
            if (profilePath.startsWith("/")) {
                profilePath = profilePath.substring(1);
            }

            String imageUrl = "http://" + BuildConfig.IP_ADDR + ":8081/" + profilePath;

            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Keširanje da ne bi stalno vukao sa mreže
                    .placeholder(R.drawable.ic_profile)          // Dok se učitava
                    .error(R.drawable.ic_profile)                // Ako pukne učitavanje
                    .into(holder.avatar);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.ic_profile)
                    .circleCrop()
                    .into(holder.avatar);
        }

        if (item.getUnreadCount() > 0) {
            holder.badge.setVisibility(View.VISIBLE);
            holder.badge.setText(String.valueOf(item.getUnreadCount()));
        } else {
            holder.badge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class InboxViewHolder extends RecyclerView.ViewHolder {
        TextView name, lastMsg, time, badge;
        ImageView avatar;

        InboxViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.tvInboxName);
            lastMsg = v.findViewById(R.id.tvInboxLastMsg);
            time = v.findViewById(R.id.tvInboxTime);
            badge = v.findViewById(R.id.tvUnreadCount);
            avatar = v.findViewById(R.id.ivInboxAvatar);
        }
    }
}