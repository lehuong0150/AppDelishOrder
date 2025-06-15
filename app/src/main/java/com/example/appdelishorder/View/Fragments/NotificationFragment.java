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
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationFragment";
    private static final String PREFS_NAME = "notifications";
    private static final String SOUND_PREF_KEY = "soundEnabled";
    private static final String BROADCAST_ACTION = "com.example.appdelishorder.NEW_NOTIFICATION";

    // UI Components
    private RecyclerView rvNotification;
    private adapterNotification notificationAdapter;
    private TextView tvNotificationCount;
    private Switch switchSound;

    // Data
    private List<Notification> notificationList;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private boolean isReceiverRegistered = false;

    private final BroadcastReceiver newNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent != null && BROADCAST_ACTION.equals(intent.getAction())) {
                    String notificationJson = intent.getStringExtra("notification");
                    if (notificationJson != null && !notificationJson.isEmpty()) {
                        Notification newNotification = gson.fromJson(notificationJson, Notification.class);
                        if (newNotification != null) {
                            updateNotificationList(newNotification);
                        }
                    }
                }
            } catch (JsonSyntaxException e) {
                Log.e(TAG, "Error parsing notification JSON: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Error in broadcast receiver: " + e.getMessage());
            }
        }
    };

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        Log.d(TAG, "Fragment created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated started");

        try {
            initializeViews(view);
            initializeSharedPreferences();
            setupBroadcastReceiver();
            loadAndSetupNotifications();
            setupSoundSwitch();

            Log.d(TAG, "Fragment initialization completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated: " + e.getMessage(), e);
            handleInitializationError();
        }
    }

    private void initializeViews(View view) {
        rvNotification = view.findViewById(R.id.rNotification);
        tvNotificationCount = view.findViewById(R.id.NotificationCount);
        switchSound = view.findViewById(R.id.swSound);

        // Validate views
        if (rvNotification == null) {
            throw new IllegalStateException("RecyclerView rNotification not found in layout");
        }
        if (tvNotificationCount == null) {
            throw new IllegalStateException("TextView NotificationCount not found in layout");
        }
        if (switchSound == null) {
            throw new IllegalStateException("Switch swSound not found in layout");
        }

        Log.d(TAG, "Views initialized successfully");
    }

    private void initializeSharedPreferences() {
        if (!isAdded() || getContext() == null) {
            throw new IllegalStateException("Fragment not properly attached to context");
        }

        sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (sharedPreferences == null) {
            throw new IllegalStateException("Failed to initialize SharedPreferences");
        }

        Log.d(TAG, "SharedPreferences initialized successfully");
    }

    private void setupBroadcastReceiver() {
        if (isAdded() && getContext() != null && !isReceiverRegistered) {
            try {
                IntentFilter filter = new IntentFilter(BROADCAST_ACTION);
                getContext().registerReceiver(newNotificationReceiver, filter);
                isReceiverRegistered = true;
                Log.d(TAG, "BroadcastReceiver registered successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error registering BroadcastReceiver: " + e.getMessage());
            }
        }
    }

    private void loadAndSetupNotifications() {
        // Load notifications
        notificationList = loadNotifications();
        if (notificationList == null) {
            notificationList = new ArrayList<>();
        }

        // Update count
        updateNotificationCount();

        // Setup adapter
        if (isAdded() && getContext() != null) {
            notificationAdapter = new adapterNotification(
                    getContext(),
                    notificationList,
                    this::onNotificationRead,
                    this::deleteNotification
            );

            rvNotification.setLayoutManager(new LinearLayoutManager(getContext()));
            rvNotification.setAdapter(notificationAdapter);

            Log.d(TAG, "RecyclerView setup completed with " + notificationList.size() + " notifications");
        }
    }

    private void setupSoundSwitch() {
        try {
            boolean isSoundEnabled = sharedPreferences.getBoolean(SOUND_PREF_KEY, true);
            switchSound.setChecked(isSoundEnabled);

            switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
                try {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(SOUND_PREF_KEY, isChecked);
                    boolean success = editor.commit();
                    Log.d(TAG, "Sound preference updated: " + isChecked + ", success: " + success);
                } catch (Exception e) {
                    Log.e(TAG, "Error updating sound preference: " + e.getMessage());
                }
            });

            Log.d(TAG, "Sound switch setup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up sound switch: " + e.getMessage());
        }
    }

    private List<Notification> loadNotifications() {
        List<Notification> notifications = new ArrayList<>();

        try {
            if (sharedPreferences == null) {
                Log.e(TAG, "SharedPreferences is null");
                return notifications;
            }

            Map<String, ?> allPrefs = sharedPreferences.getAll();
            if (allPrefs == null) {
                Log.w(TAG, "No preferences found");
                return notifications;
            }

            for (Map.Entry<String, ?> entry : allPrefs.entrySet()) {
                String key = entry.getKey();
                if (key != null && key.startsWith("notification_")) {
                    try {
                        String json = sharedPreferences.getString(key, null);
                        if (json != null && !json.isEmpty()) {
                            Notification notification = gson.fromJson(json, Notification.class);
                            if (notification != null && isValidNotification(notification)) {
                                notifications.add(notification);
                            } else {
                                Log.w(TAG, "Invalid notification found, removing: " + key);
                                // Remove invalid notification
                                sharedPreferences.edit().remove(key).apply();
                            }
                        }
                    } catch (JsonSyntaxException e) {
                        Log.e(TAG, "Error parsing notification " + key + ": " + e.getMessage());
                        // Remove corrupted notification
                        sharedPreferences.edit().remove(key).apply();
                    }
                }
            }

            // Sort by timestamp (newest first)
            Collections.sort(notifications, (n1, n2) -> {
                try {
                    return Long.compare(n2.getTimestamp(), n1.getTimestamp());
                } catch (Exception e) {
                    Log.w(TAG, "Error comparing timestamps: " + e.getMessage());
                    return 0;
                }
            });

            Log.d(TAG, "Successfully loaded " + notifications.size() + " notifications");

        } catch (Exception e) {
            Log.e(TAG, "Error loading notifications: " + e.getMessage());
        }

        return notifications;
    }

    private boolean isValidNotification(Notification notification) {
        return notification.getTitle() != null &&
                !notification.getTitle().isEmpty() &&
                notification.getTimestamp() > 0;
    }

    private void saveNotificationsToSharedPreferences() {
        try {
            if (sharedPreferences == null || notificationList == null) {
                Log.e(TAG, "Cannot save notifications: SharedPreferences or notificationList is null");
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Remove all existing notification entries
            Map<String, ?> allPrefs = sharedPreferences.getAll();
            for (String key : allPrefs.keySet()) {
                if (key.startsWith("notification_")) {
                    editor.remove(key);
                }
            }

            // Save current notifications
            for (Notification notification : notificationList) {
                if (notification != null && isValidNotification(notification)) {
                    try {
                        String json = gson.toJson(notification);
                        String key = "notification_" + notification.getTimestamp();
                        editor.putString(key, json);
                    } catch (Exception e) {
                        Log.e(TAG, "Error serializing notification: " + e.getMessage());
                    }
                }
            }

            boolean success = editor.commit();
            Log.d(TAG, "Notifications saved successfully: " + success);

        } catch (Exception e) {
            Log.e(TAG, "Error saving notifications: " + e.getMessage());
        }
    }

    public void updateNotificationList(Notification newNotification) {
        try {
            // Check if fragment is still active
            if (!isAdded() || getView() == null || getContext() == null) {
                Log.w(TAG, "Fragment not active, skipping notification update");
                return;
            }

            if (newNotification == null || !isValidNotification(newNotification)) {
                Log.w(TAG, "Invalid notification, skipping update");
                return;
            }

            if (notificationList == null) {
                notificationList = new ArrayList<>();
            }

            // Add new notification at the beginning
            notificationList.add(0, newNotification);

            // Update UI
            if (notificationAdapter != null) {
                notificationAdapter.notifyItemInserted(0);
                rvNotification.scrollToPosition(0);
            }

            // Save and update count
            saveNotificationsToSharedPreferences();
            updateNotificationCount();

            Log.d(TAG, "Notification list updated successfully. Title: " + newNotification.getTitle());

        } catch (Exception e) {
            Log.e(TAG, "Error updating notification list: " + e.getMessage());
        }
    }

    private void onNotificationRead(Notification notification) {
        try {
            notification.setRead(true);
            saveNotificationsToSharedPreferences();

            if (notificationAdapter != null) {
                notificationAdapter.notifyDataSetChanged();
            }

            updateNotificationCount();
            Log.d(TAG, "Notification marked as read: " + notification.getTitle());

        } catch (Exception e) {
            Log.e(TAG, "Error marking notification as read: " + e.getMessage());
        }
    }

    private void updateNotificationCount() {
        try {
            if (tvNotificationCount == null || notificationList == null) {
                return;
            }

            int unreadCount = 0;
            for (Notification notification : notificationList) {
                if (notification != null && !notification.isRead()) {
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

            // Save unread count
            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("unread_notification_count", unreadCount);
                editor.apply();
            }

            Log.d(TAG, "Notification count updated: " + unreadCount + " unread out of " + notificationList.size());

        } catch (Exception e) {
            Log.e(TAG, "Error updating notification count: " + e.getMessage());
        }
    }

    public void deleteNotification(int position) {
        try {
            if (notificationList == null || position < 0 || position >= notificationList.size()) {
                Log.w(TAG, "Invalid position for deletion: " + position);
                return;
            }

            Notification removed = notificationList.remove(position);

            if (notificationAdapter != null) {
                notificationAdapter.notifyItemRemoved(position);
                notificationAdapter.notifyItemRangeChanged(position, notificationList.size());
            }

            saveNotificationsToSharedPreferences();
            updateNotificationCount();

            Log.d(TAG, "Notification deleted successfully: " + (removed != null ? removed.getTitle() : "null"));

        } catch (Exception e) {
            Log.e(TAG, "Error deleting notification: " + e.getMessage());
        }
    }

    private void handleInitializationError() {
        try {
            if (tvNotificationCount != null) {
                tvNotificationCount.setText("Lỗi tải thông báo. Vui lòng thử lại.");
            }
            if (rvNotification != null) {
                rvNotification.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling initialization error: " + e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Fragment resumed");

        // Refresh notifications when fragment becomes visible
        if (notificationAdapter != null) {
            notificationAdapter.notifyDataSetChanged();
        }
        updateNotificationCount();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Fragment paused");
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView called");

        // Unregister broadcast receiver
        unregisterBroadcastReceiver();

        // Clean up references
        cleanupReferences();

        super.onDestroyView();
    }

    private void unregisterBroadcastReceiver() {
        if (isReceiverRegistered && getContext() != null) {
            try {
                getContext().unregisterReceiver(newNotificationReceiver);
                isReceiverRegistered = false;
                Log.d(TAG, "BroadcastReceiver unregistered successfully");
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "BroadcastReceiver was not registered: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Error unregistering BroadcastReceiver: " + e.getMessage());
            }
        }
    }

    private void cleanupReferences() {
        try {
            notificationAdapter = null;
            notificationList = null;
            sharedPreferences = null;
            rvNotification = null;
            tvNotificationCount = null;
            switchSound = null;

            Log.d(TAG, "References cleaned up successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error cleaning up references: " + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Fragment destroyed");
        super.onDestroy();
    }

    // Public method to get unread notification count
    public int getUnreadNotificationCount() {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt("unread_notification_count", 0);
        }
        return 0;
    }

    // Public method to clear all notifications (UI & SharedPreferences)
    public void clearAllNotifications() {
        try {
            if (notificationList != null) {
                notificationList.clear();

                if (notificationAdapter != null) {
                    notificationAdapter.notifyDataSetChanged();
                }

                saveNotificationsToSharedPreferences();
                updateNotificationCount();

                Log.d(TAG, "All notifications cleared");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error clearing notifications: " + e.getMessage());
        }
    }

    // Public method to clear all notification data in SharedPreferences (call after login)
    public void clearNotificationPreferences() {
        try {
            if (getContext() != null) {
                SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                prefs.edit().clear().apply();
                Log.d(TAG, "Notification SharedPreferences cleared");
            }
            // Also clear the list and update UI if fragment is active
            if (notificationList != null) {
                notificationList.clear();
                if (notificationAdapter != null) {
                    notificationAdapter.notifyDataSetChanged();
                }
                updateNotificationCount();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error clearing notification preferences: " + e.getMessage());
        }
    }
}