package com.example.appdelishorder.View.Activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appdelishorder.R;
import com.example.appdelishorder.View.Fragments.NotificationFragment;
import com.example.appdelishorder.View.Fragments.HomeFragment;
import com.example.appdelishorder.View.Fragments.OrderFragment;
import com.example.appdelishorder.View.Fragments.ProfileFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView menuBottom;
    private BadgeDrawable notificationBadge;

    private static final String PREFS_NAME = "notifications"; // Thống nhất tên với NotificationFragment

    private enum CurrentPage {
        HOME, ORDER, NOTIFICATION, PROFILE
    }

    private CurrentPage currentPage = CurrentPage.HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Kiểm tra và yêu cầu quyền thông báo trên Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Khởi tạo giao diện
        replaceFragment(new HomeFragment());

        // Ánh xạ BottomNavigationView
        menuBottom = findViewById(R.id.bottomNavigationView);

        // Khởi tạo badge cho thông báo
        notificationBadge = menuBottom.getOrCreateBadge(R.id.menu_Notification);
        notificationBadge.setVisible(false); // Ẩn badge mặc định

        // Kiểm tra thông báo mới
        checkForNewNotifications();

        // Xử lý sự kiện chọn menu
        menuBottom.setOnItemSelectedListener(item -> {
            int i = item.getItemId();
            if (i == R.id.menu_Home) {
                replaceFragment(new HomeFragment());
                currentPage = CurrentPage.HOME;
            } else if (i == R.id.menu_Order) {
                replaceFragment(new OrderFragment());
                currentPage = CurrentPage.ORDER;
            } else if (i == R.id.menu_Notification) {
                replaceFragment(new NotificationFragment());
                currentPage = CurrentPage.NOTIFICATION;
                clearNotificationBadge(); // Xóa badge khi người dùng vào trang thông báo
            } else if (i == R.id.menu_Profile) {
                replaceFragment(new ProfileFragment());
                currentPage = CurrentPage.PROFILE;
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Kiểm tra thông báo mới khi quay lại HomeActivity
        checkForNewNotifications();
    }

    private void checkForNewNotifications() {
        // Lấy số lượng thông báo chưa đọc từ SharedPreferences - dùng cùng tên file với NotificationFragment
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int unreadCount = sharedPreferences.getInt("unread_notification_count", 0);

        Log.d("HomeActivity", "Kiểm tra thông báo: " + unreadCount + " thông báo chưa đọc");

        if (unreadCount > 0 && currentPage != CurrentPage.NOTIFICATION) {
            showNotificationBadge(unreadCount); // Hiển thị badge với số lượng thông báo chưa đọc
        } else {
            clearNotificationBadge(); // Ẩn badge nếu không có thông báo mới hoặc đang ở trang thông báo
        }
    }

    private void showNotificationBadge(int count) {
        if (notificationBadge != null) {
            notificationBadge.setVisible(true);
            notificationBadge.setNumber(count); // Hiển thị số lượng thông báo mới
            Log.d("HomeActivity", "Hiển thị badge với số lượng: " + count);
        }
    }

    private void clearNotificationBadge() {
        if (notificationBadge != null) {
            notificationBadge.setVisible(false); // Ẩn badge
            notificationBadge.clearNumber(); // Xóa số lượng thông báo
            Log.d("HomeActivity", "Đã xóa badge thông báo");
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, fragment);
        fragmentTransaction.commit();
    }

    // Thêm phương thức để cập nhật lại badge thông báo
    public void updateNotificationBadge() {
        checkForNewNotifications();
    }
}
