package com.example.appdelishorder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    private OnNotificationClickListener clickListener;
    private OnNotificationActionListener actionListener;

    // Interface cho sự kiện click vào thông báo
    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    // Interface cho sự kiện xóa thông báo
    public interface OnNotificationActionListener {
        void onDeleteNotification(int position);
    }

    public adapterNotification(Context context, List<Notification> notifications,
                               OnNotificationClickListener clickListener,
                               OnNotificationActionListener actionListener) {
        this.context = context;
        this.notifications = notifications;
        this.clickListener = clickListener;
        this.actionListener = actionListener;
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

        // Hiển thị tiêu đề, nội dung và thời gian thông báo
        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());
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

        // Xử lý nút "Đánh dấu đã đọc"
        holder.btnMarkAsRead.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onNotificationClick(notification); // Gọi callback để xử lý đánh dấu đã đọc
            }
        });

        // Xử lý nút "Xóa"
        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteNotification(position); // Gọi callback để xử lý xóa
            }
        });

        // Đặt icon dựa trên loại thông báo
        if ("order_new".equals(notification.getType())) {
            holder.tvTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_fiber_new_24, 0, 0, 0);
        } else if ("order_status".equals(notification.getType())) {
            holder.tvTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_update_24, 0, 0, 0);
        } else {
            holder.tvTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_notifications_active_24, 0, 0, 0);
        }

        // Xử lý sự kiện click vào thông báo
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onNotificationClick(notification);
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
        ImageButton btnMarkAsRead, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvTime = itemView.findViewById(R.id.tvNotificationTime);
            btnMarkAsRead = itemView.findViewById(R.id.btnMarkAsRead);
            btnDelete = itemView.findViewById(R.id.btnDeleteNotification);
        }
    }
}