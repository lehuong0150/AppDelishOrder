package com.example.appdelishorder.View.Activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdelishorder.R;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton backButton;
    private RelativeLayout changePasswordLayout;
    private Switch notificationsSwitch;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Initialize views
        backButton = findViewById(R.id.btn_back);
        changePasswordLayout = findViewById(R.id.change_password_layout);
        notificationsSwitch = findViewById(R.id.switch_notifications);

        // Set listeners
        backButton.setOnClickListener(v -> onBackPressed());

        changePasswordLayout.setOnClickListener(v -> {
            // Navigate to change password screen
            Toast.makeText(this, "Change Password clicked", Toast.LENGTH_SHORT).show();
        });

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save notification preference
            SharedPreferences preferences = getSharedPreferences("app_settings", MODE_PRIVATE);
            preferences.edit().putBoolean("notifications_enabled", isChecked).apply();
            Toast.makeText(this, "Notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });
    }
}