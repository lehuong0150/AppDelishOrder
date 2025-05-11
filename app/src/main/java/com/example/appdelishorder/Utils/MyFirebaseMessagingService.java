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
import com.example.appdelishorder.View.Fragments.NotificationFragment;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final Logger log = LoggerFactory.getLogger(MyFirebaseMessagingService.class);

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        // Lấy email người dùng từ SessionManager
        SessionManager sessionManager = new SessionManager(this);
        String email = sessionManager.getEmail();

        if (email != null) {
            Log.d("FCM", "Email: " + email);
            Log.d("FCM", "New Firebase Token: " + token);

            // Gửi token mới lên server
            sendTokenToServer(email, token);
        } else {
            Log.w("FCM", "Không tìm thấy email người dùng. Token không được gửi.");
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
                    Log.d("FCM", "Token đã được cập nhật thành công trên server.");
                } else {
                    Log.e("FCM", "Lỗi khi cập nhật token trên server: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("FCM", "Lỗi khi gửi token lên server: " + t.getMessage());
            }
        });
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("FCMe", "Message received from: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d("FCMe", "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d("FCM", "Message notification title: " + remoteMessage.getNotification().getTitle());
            Log.d("FCM", "Message notification body: " + remoteMessage.getNotification().getBody());
        }
        // Lấy thông tin từ Notification payload
        String title = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getTitle() : "Thông báo";
        String body = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "Bạn có thông báo mới";

        // Tạo đối tượng Notification
        Notification notification = new Notification(title, body , System.currentTimeMillis(), false, "order_status");

        // Lưu thông báo
        saveNotification(notification);

        // Gửi Broadcast để cập nhật NotificationFragment
        Intent intent = new Intent("com.example.appdelishorder.NEW_NOTIFICATION");
        intent.putExtra("notification", new Gson().toJson(notification));
        sendBroadcast(intent);

        // Hiển thị thông báo
        showNotification(title, body );
    }

    private void saveNotification(Notification notification) {
        SharedPreferences sharedPreferences = getSharedPreferences("notifications", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(notification);

        editor.putString("notification_" + System.currentTimeMillis(), json);
        editor.apply();

        Log.d("FCM", "Notification saved: " + notification.getTitle());
    }

    private void showNotification(String title, String body) {
        String channelId = "order_notifications";
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Lấy trạng thái âm thanh từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        boolean isSoundEnabled = sharedPreferences.getBoolean("soundEnabled", true);
        Log.d("FCM", "Sound enabled: " + isSoundEnabled);

        // Kiểm tra âm lượng thông báo của thiết bị
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int notificationVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        if (notificationVolume == 0) {
            Log.w("FCM", "Notification volume is set to 0 on device.");
        }

        Uri soundUri = null;
        if (isSoundEnabled) {
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager systemNotificationManager = getSystemService(NotificationManager.class);

            // Tạo kênh thông báo mới với ID độc nhất nếu cần thay đổi cài đặt
            // String newChannelId = "order_notifications_" + System.currentTimeMillis();

            // Hoặc xóa kênh cũ nếu đã tồn tại trước khi tạo lại
            systemNotificationManager.deleteNotificationChannel(channelId);

            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Order Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for order notifications");

            if (isSoundEnabled) {
                channel.setSound(soundUri, new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build());
            } else {
                channel.setSound(null, null);
            }

            systemNotificationManager.createNotificationChannel(channel);

            // Kiểm tra xem kênh có bị vô hiệu hóa không
            NotificationChannel createdChannel = systemNotificationManager.getNotificationChannel(channelId);
            if (createdChannel != null && createdChannel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                Log.w("FCM", "Notification channel is disabled by user.");
            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Chỉ thiết lập âm thanh cho builder ở phiên bản Android < 8.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (isSoundEnabled && soundUri != null) {
                builder.setSound(soundUri);
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
        } else {
            Log.w("FCM", "Notification permission not granted.");
        }
    }

}

