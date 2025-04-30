// SignalRManager.java
package com.example.appdelishorder;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.appdelishorder.Model.Notification;
import com.example.appdelishorder.Utils.SessionManager;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignalRManager {
    private static final String TAG = "SignalRManager";
    private static SignalRManager instance;

    private HubConnection hubConnection;
    private Context context;
    private ExecutorService executorService;
    private List<OnNotificationListener> listeners = new ArrayList<>();
    private List<Notification> notifications = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private boolean soundEnabled = true; // Mặc định bật âm thanh

    // URL của SignalR hub
    private static final String HUB_URL = "https://192.168.1.2:7010/orderNotificationHub";

    // Interface cho các listener
    public interface OnNotificationListener {
        void onNewNotification(Notification notification);
        void onNotificationsUpdated(List<Notification> notifications);
    }

    private SignalRManager(Context context) {
        this.context = context.getApplicationContext();
        this.executorService = Executors.newSingleThreadExecutor();

        // Khởi tạo MediaPlayer để phát âm thanh thông báo
        //mediaPlayer = MediaPlayer.create(context, R.raw.notification_sound);

        // Khởi tạo kết nối Hub
        createHubConnection();
    }

    public static synchronized SignalRManager getInstance(Context context) {
        if (instance == null) {
            instance = new SignalRManager(context);
        }
        return instance;
    }

    private void createHubConnection() {
        hubConnection = HubConnectionBuilder.create(HUB_URL)
                .withHeader("User-Agent", "Android")
                .build();

        // Đăng ký các phương thức nhận thông báo từ server
        hubConnection.on("ReceiveOrderNotification", (orderId, details) -> {
            Log.d(TAG, "Received order notification: " + orderId + ", " + details);

            // Tạo thông báo mới
            Notification notification = new Notification();
            notification.setId(Integer.parseInt(orderId));
            notification.setTitle("Đơn hàng mới");
            notification.setMessage(details);
            notification.setTimestamp(new Date());
            notification.setRead(false);
            notification.setType("order_new");

            // Lưu thông báo vào danh sách
            notifications.add(0, notification); // Thêm vào đầu danh sách

            // Phát âm thanh nếu được bật
            if (soundEnabled) {
                playNotificationSound();
            }

            // Thông báo tới tất cả các listener
            notifyListeners(notification);
        }, String.class, String.class);

        // Nhận thông báo khi trạng thái đơn hàng thay đổi
        hubConnection.on("OrderStatusChanged", (orderId, status) -> {
            Log.d(TAG, "Order status changed: " + orderId + ", status: " + status);

            // Tạo thông báo mới
            Notification notification = new Notification();
            notification.setId(Integer.parseInt(orderId));
            notification.setTitle("Cập nhật trạng thái đơn hàng");
            notification.setMessage("Đơn hàng #" + orderId + " đã được cập nhật sang: " + status);
            notification.setTimestamp(new Date());
            notification.setRead(false);
            notification.setType("order_status");

            // Lưu thông báo vào danh sách
            notifications.add(0, notification);

            // Phát âm thanh nếu được bật
            if (soundEnabled) {
                playNotificationSound();
            }

            // Thông báo tới tất cả các listener
            notifyListeners(notification);
        }, String.class, String.class);

        // Xử lý sự kiện đóng kết nối
        hubConnection.onClosed(error -> {
            Log.e(TAG, "Connection closed: " + (error != null ? error.getMessage() : "Unknown error"));
            // Thử kết nối lại sau 5 giây
            executorService.execute(() -> {
                try {
                    Thread.sleep(5000);
                    startConnection();
                } catch (InterruptedException e) {
                    Log.e(TAG, "Interrupted while waiting to reconnect", e);
                }
            });
        });
    }

    // Bắt đầu kết nối
    public void startConnection() {
        if (hubConnection != null && hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            executorService.execute(() -> {
                try {
                    hubConnection.start().blockingAwait();
                    Log.d(TAG, "SignalR connection started successfully");
                } catch (Exception e) {
                    Log.e(TAG, "Error starting SignalR connection", e);
                }
            });
        }
    }

    // Dừng kết nối
    public void stopConnection() {
        if (hubConnection != null && hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            executorService.execute(() -> {
                try {
                    hubConnection.stop();
                    Log.d(TAG, "SignalR connection stopped");
                } catch (Exception e) {
                    Log.e(TAG, "Error stopping SignalR connection", e);
                }
            });
        }
    }

    // Phát âm thanh thông báo
    private void playNotificationSound() {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.prepare();
            }
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e(TAG, "Error playing notification sound", e);
        }
    }

    // Đăng ký listener
    public void addListener(OnNotificationListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            // Gửi danh sách thông báo hiện tại cho listener mới
            if (listener != null) {
                listener.onNotificationsUpdated(new ArrayList<>(notifications));
            }
        }
    }

    // Hủy đăng ký listener
    public void removeListener(OnNotificationListener listener) {
        listeners.remove(listener);
    }

    // Thông báo đến tất cả các listener
    private void notifyListeners(Notification notification) {
        for (OnNotificationListener listener : listeners) {
            if (listener != null) {
                listener.onNewNotification(notification);
                listener.onNotificationsUpdated(new ArrayList<>(notifications));
            }
        }
    }

    // Đánh dấu thông báo đã đọc
    public void markAsRead(int notificationId) {
        for (Notification notification : notifications) {
            if (notification.getId() == notificationId) {
                notification.setRead(true);
                // Thông báo cho server rằng thông báo đã được đọc
                markNotificationReadOnServer(notificationId);
                break;
            }
        }
        // Cập nhật UI
        for (OnNotificationListener listener : listeners) {
            if (listener != null) {
                listener.onNotificationsUpdated(new ArrayList<>(notifications));
            }
        }
    }

    // Đánh dấu tất cả là đã đọc
    public void markAllAsRead() {
        for (Notification notification : notifications) {
            notification.setRead(true);
            // Thông báo server
            markNotificationReadOnServer(notification.getId());
        }
        // Cập nhật UI
        for (OnNotificationListener listener : listeners) {
            if (listener != null) {
                listener.onNotificationsUpdated(new ArrayList<>(notifications));
            }
        }
    }

    // Gửi yêu cầu đánh dấu đã đọc đến server
    private void markNotificationReadOnServer(int notificationId) {
        if (hubConnection != null && hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            executorService.execute(() -> {
                try {
                    hubConnection.invoke("MarkOrderAsRead", String.valueOf(notificationId));
                    Log.d(TAG, "Marked notification as read on server: " + notificationId);
                } catch (Exception e) {
                    Log.e(TAG, "Error marking notification as read", e);
                }
            });
        }
    }

    // Bật/tắt âm thanh thông báo
    public void toggleSound(boolean enabled) {
        this.soundEnabled = enabled;
        // Lưu trạng thái vào SharedPreferences
        SessionManager sessionManager = new SessionManager(context);
        sessionManager.setSoundEnabled(enabled);
    }

    // Lấy trạng thái âm thanh
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    // Lấy danh sách thông báo
    public List<Notification> getNotifications() {
        return new ArrayList<>(notifications);
    }

    // Lấy số lượng thông báo chưa đọc
    public int getUnreadCount() {
        int count = 0;
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                count++;
            }
        }
        return count;
    }
}
