// Adapter/adapterNotification.java
package com.example.appdelishorder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdelishorder.Model.Notification;
import com.example.appdelishorder.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class adapterNotification extends RecyclerView.Adapter<adapterNotification.ViewHolder> {

    private Context context;
    private List<Notification> notifications;
    private OnNotificationClickListener listener;

    // Interface cho sự kiện click
    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public adapterNotification(Context context, List<Notification> notifications, OnNotificationClickListener listener) {
        this.context = context;
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewhold_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());

        // Format thời gian
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvTime.setText(sdf.format(notification.getTimestamp()));

        // Đặt màu nền dựa trên trạng thái đã đọc
        if (notification.isRead()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.create_new));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_1));
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.text_light));
        }

        // Đặt icon dựa trên loại thông báo
        if ("order_new".equals(notification.getType())) {
            holder.tvTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_fiber_new_24, 0, 0, 0);
        } else if ("order_status".equals(notification.getType())) {
            holder.tvTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_update_24, 0, 0, 0);
        } else {
            holder.tvTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_notifications_active_24, 0, 0, 0);
        }

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    // Cập nhật danh sách thông báo
    public void updateData(List<Notification> newNotifications) {
        this.notifications = newNotifications;
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvTime = itemView.findViewById(R.id.tvNotificationTime);
        }
    }
}
