package com.example.appdelishorder.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    // SharedPreferences file name
    private static final String PREF_NAME = "DelishOrderPrefs";

    // SharedPreferences keys
    private static final String KEY_TOKEN = "user_token";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_SOUND_ENABLED = "notification_sound_enabled";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Lưu trạng thái đăng nhập
    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    // Lưu thông tin người dùng
    public void setUserDetails(String token, String email) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    // Các phương thức từ mã nguồn cũ, được cập nhật để phù hợp với mã mới
    public void saveEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    // Kiểm tra đăng nhập
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Lấy email người dùng
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    // Lấy token
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, "");
    }

    // Đăng xuất - cập nhật từ clearSession()
    public void logout() {
        editor.clear();
        editor.apply();
    }

    // Phương thức cũ giữ lại để tương thích ngược
    public void clearSession() {
        logout();
    }

    // Lưu trạng thái âm thanh thông báo
    public void setSoundEnabled(boolean enabled) {
        editor.putBoolean(KEY_SOUND_ENABLED, enabled);
        editor.apply();
    }

    // Lấy trạng thái âm thanh thông báo
    public boolean isSoundEnabled() {
        return sharedPreferences.getBoolean(KEY_SOUND_ENABLED, true); // Mặc định là bật
    }
}
