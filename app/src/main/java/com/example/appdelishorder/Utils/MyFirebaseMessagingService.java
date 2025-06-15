package com.example.appdelishorder.Utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.appdelishorder.Model.Notification;
import com.example.appdelishorder.Model.UpdateTokenRequest;
import com.example.appdelishorder.R;
import com.example.appdelishorder.Retrofit.APIClient;
import com.example.appdelishorder.Retrofit.ApiService;
import com.example.appdelishorder.View.Activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";
    private static final String PREFS_NAME = "notifications";
    private static final String SOUND_PREF_KEY = "soundEnabled";
    private static final String CHANNEL_ID = "order_notifications";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        // Lấy email người dùng từ SessionManager
        SessionManager sessionManager = new SessionManager(this);
        String email = sessionManager.getEmail();

        if (email != null) {
            Log.d(TAG, "Email: " + email);
            Log.d(TAG, "New Firebase Token: " + token);

            // Gửi token mới lên server
            sendTokenToServer(email, token);
        } else {
            Log.w(TAG, "Không tìm thấy email người dùng. Token không được gửi.");
        }
    }

    private void sendTokenToServer(String email, String token) {
        ApiService apiService = APIClient.getClient().create(ApiService.class);
        UpdateTokenRequest request = new UpdateTokenRequest(email, token);

        Call<Void> call = apiService.updateFirebaseToken(request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Token đã được cập nhật thành công trên server.");
                } else {
                    Log.e(TAG, "Lỗi khi cập nhật token trên server: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Lỗi khi gửi token lên server: " + t.getMessage());
            }
        });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Message received from: " + remoteMessage.getFrom());

        String title = "Thông báo";
        String body = "Bạn có thông báo mới";

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle() != null ? remoteMessage.getNotification().getTitle() : title;
            body = remoteMessage.getNotification().getBody() != null ? remoteMessage.getNotification().getBody() : body;
            Log.d(TAG, "Message notification title: " + title);
            Log.d(TAG, "Message notification body: " + body);
        }

        // Tạo đối tượng Notification
        Notification notification = new Notification(title, body, System.currentTimeMillis(), false, "order_status");

        // Lưu thông báo
        saveNotification(notification);

        // Gửi Broadcast để cập nhật NotificationFragment
        Intent intent = new Intent("com.example.appdelishorder.NEW_NOTIFICATION");
        intent.putExtra("notification", new Gson().toJson(notification));
        sendBroadcast(intent);

        // Hiển thị thông báo
        showNotification(title, body);
    }

    private void saveNotification(Notification notification) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(notification);

        editor.putString("notification_" + notification.getTimestamp(), json);
        editor.apply();

        Log.d(TAG, "Notification saved: " + notification.getTitle());
    }

    private void showNotification(String title, String body) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Lấy trạng thái âm thanh từ SharedPreferences (dùng chung PREFS_NAME)
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isSoundEnabled = sharedPreferences.getBoolean(SOUND_PREF_KEY, true);
        Log.d(TAG, "Sound enabled: " + isSoundEnabled);

        Uri soundUri = null;
        if (isSoundEnabled) {
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        // Tạo NotificationChannel nếu cần (Android O+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager systemNotificationManager = getSystemService(NotificationManager.class);

            NotificationChannel channel = systemNotificationManager.getNotificationChannel(CHANNEL_ID);
            if (channel == null) {
                channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Order Notifications",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Channel for order notifications");
                if (isSoundEnabled && soundUri != null) {
                    channel.setSound(soundUri, new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build());
                } else {
                    channel.setSound(null, null);
                }
                systemNotificationManager.createNotificationChannel(channel);
            } else {
                // Nếu trạng thái sound thay đổi, cập nhật lại channel
                if (isSoundEnabled && soundUri != null && channel.getSound() == null) {
                    channel.setSound(soundUri, new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build());
                    systemNotificationManager.createNotificationChannel(channel);
                } else if (!isSoundEnabled && channel.getSound() != null) {
                    channel.setSound(null, null);
                    systemNotificationManager.createNotificationChannel(channel);
                }
            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Chỉ thiết lập âm thanh cho builder ở phiên bản Android < 8.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && isSoundEnabled && soundUri != null) {
            builder.setSound(soundUri);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        } else {
            Log.w(TAG, "Notification permission not granted.");
        }
    }
}