package com.example.appdelishorder.View.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.appdelishorder.Adapter.adapterNotification;
import com.example.appdelishorder.Model.Notification;
import com.example.appdelishorder.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView rvNotification;
    private adapterNotification notificationAdapter;
    private List<Notification> notificationList;
    private TextView tvNotificationCount;
    private Switch switchSound;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "notifications"; // Thống nhất tên SharedPreferences
    private static final String SOUND_PREF_KEY = "soundEnabled";

    private final BroadcastReceiver newNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String notificationJson = intent.getStringExtra("notification");
            Notification newNotification = new Gson().fromJson(notificationJson, Notification.class);

            // Cập nhật danh sách thông báo
            updateNotificationList(newNotification);
        }
    };

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        rvNotification = view.findViewById(R.id.rvNotification);
        tvNotificationCount = view.findViewById(R.id.tvNotificationCount);
        switchSound = view.findViewById(R.id.switchSound);

        // Đăng ký BroadcastReceiver để nhận thông báo mới
        IntentFilter filter = new IntentFilter("com.example.appdelishorder.NEW_NOTIFICATION");
        requireContext().registerReceiver(newNotificationReceiver, filter);

        // Initialize SharedPreferences - Sử dụng chung một tên file
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load sound preference
        boolean isSoundEnabled = sharedPreferences.getBoolean(SOUND_PREF_KEY, true);
        switchSound.setChecked(isSoundEnabled);

        // Initialize notification list
        notificationList = loadNotifications();
        updateNotificationCount();

        // Set up RecyclerView
        notificationAdapter = new adapterNotification(requireContext(), notificationList, notification -> {
            // Đánh dấu thông báo là đã đọc
            notification.setRead(true);
            saveNotificationsToSharedPreferences(); // Lưu trạng thái đã đọc
            notificationAdapter.notifyDataSetChanged();
            updateNotificationCount();
        }, position -> {
            // Xóa thông báo
            deleteNotification(position);
        });

        rvNotification.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvNotification.setAdapter(notificationAdapter);

        // Handle sound switch
        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(SOUND_PREF_KEY, isChecked);
            editor.apply();
            Log.d("NotificationFragment", "Sound preference updated: " + isChecked);
        });
    }

    private List<Notification> loadNotifications() {
        List<Notification> notifications = new ArrayList<>();
        Gson gson = new Gson();

        // Lấy tất cả các key từ SharedPreferences
        for (String key : sharedPreferences.getAll().keySet()) {
            if (key.startsWith("notification_")) { // Chỉ lấy các key bắt đầu bằng "notification_"
                String json = sharedPreferences.getString(key, null);
                if (json != null) {
                    try {
                        Notification notification = gson.fromJson(json, Notification.class);
                        notifications.add(notification);
                    } catch (Exception e) {
                        Log.e("NotificationFragment", "Error parsing notification: " + e.getMessage());
                    }
                }
            }
        }

        // Sắp xếp thông báo theo thời gian (mới nhất trước)
        notifications.sort((n1, n2) -> Long.compare(n2.getTimestamp(), n1.getTimestamp()));

        Log.d("NotificationFragment", "Loaded " + notifications.size() + " notifications");
        return notifications;
    }

    private void saveNotificationsToSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        // Xóa tất cả các thông báo cũ trước khi lưu
        for (String key : sharedPreferences.getAll().keySet()) {
            if (key.startsWith("notification_")) {
                editor.remove(key);
            }
        }

        // Lưu danh sách thông báo hiện tại
        for (Notification notification : notificationList) {
            String json = gson.toJson(notification);
            editor.putString("notification_" + notification.getTimestamp(), json);
        }

        // Đảm bảo áp dụng các thay đổi
        boolean success = editor.commit(); // Sử dụng commit() thay vì apply() để đảm bảo ghi ngay lập tức
        Log.d("NotificationFragment", "Saved notifications: " + success);
    }

    public void updateNotificationList(Notification newNotification) {
        notificationList.add(0, newNotification); // Add the new notification to the top of the list
        if (notificationAdapter != null) {
            notificationAdapter.notifyDataSetChanged();
        }

        saveNotificationsToSharedPreferences(); // Lưu danh sách vào SharedPreferences
        updateNotificationCount();
        Log.d("NotificationFragment", "Notification list updated with new notification: " + newNotification.getTitle());
    }

    private void updateNotificationCount() {
        int unreadCount = 0;
        for (Notification notification : notificationList) {
            if (!notification.isRead()) {
                unreadCount++;
            }
        }

        if (notificationList.isEmpty()) {
            tvNotificationCount.setText("Không có thông báo nào.");
            rvNotification.setVisibility(View.GONE);
        } else {
            tvNotificationCount.setText("Thông báo (" + unreadCount + ")");
            rvNotification.setVisibility(View.VISIBLE);
        }

        // Lưu số lượng thông báo chưa đọc
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("unread_notification_count", unreadCount);
        editor.apply();
    }

    public void deleteNotification(int position) {
        if (position >= 0 && position < notificationList.size()) {
            Notification removed = notificationList.remove(position);
            notificationAdapter.notifyItemRemoved(position);
            saveNotificationsToSharedPreferences(); // Lưu danh sách sau khi xóa
            updateNotificationCount();
            Log.d("NotificationFragment", "Deleted notification: " + removed.getTitle());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hủy đăng ký BroadcastReceiver
        try {
            requireContext().unregisterReceiver(newNotificationReceiver);
        } catch (IllegalArgumentException e) {
            Log.w("NotificationFragment", "BroadcastReceiver was not registered.");
        }
    }
}
