package com.example.appdelishorder.View.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appdelishorder.Contract.accountContract;
import com.example.appdelishorder.Model.Account;
import com.example.appdelishorder.Model.UpdateTokenRequest;
import com.example.appdelishorder.Presenter.accountPresenter;
import com.example.appdelishorder.R;
import com.example.appdelishorder.Retrofit.APIClient;
import com.example.appdelishorder.Retrofit.ApiService;
import com.example.appdelishorder.Utils.SessionManager;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements  accountContract.View{
    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerButton;
    private ProgressBar progressBar;
    private accountPresenter presenter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.et_email);
        passwordInput = findViewById(R.id.et_password);
        loginButton = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        registerButton = findViewById(R.id.tv_register);

        // Tạo presenter và truyền vào View (this) và apiService
        presenter = new accountPresenter(this);

        // Nhận dữ liệu email và password từ RegisterActivity
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");

        // Điền email và password vào các ô tương ứng nếu có dữ liệu
        if (email != null && !email.isEmpty()) {
            emailInput.setText(email);
        }
        if (password != null && !password.isEmpty()) {
            passwordInput.setText(password);
        }

        // Xử lý sự kiện khi nhấn nút đăng nhập
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    presenter.doLogin(email, password);
                } else {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xử lý sự kiện khi nhấn nút đăng ký
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void showLoginSuccess(String token) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, token, Toast.LENGTH_SHORT).show();

        // Save the email in SessionManager
        SessionManager sessionManager = new SessionManager(this);
        String email = emailInput.getText().toString().trim();

        // --- ĐOẠN KIỂM TRA VÀ XÓA THÔNG BÁO NẾU ĐĂNG NHẬP TÀI KHOẢN KHÁC ---
        // Lưu email hiện tại vào SharedPreferences
        android.content.SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String lastUser = prefs.getString("current_user", "");
        if (!lastUser.equals(email)) {
            // Nếu là tài khoản khác thì xóa thông báo
            android.content.SharedPreferences notificationPrefs = getSharedPreferences("notifications", MODE_PRIVATE);
            notificationPrefs.edit().clear().apply();
        }
        prefs.edit().putString("current_user", email).apply();
        // --- HẾT ĐOẠN THÊM ---

        sessionManager.saveEmail(email);
        sessionManager.saveToken(token);

        // Lấy Firebase Token và gửi lên server
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d("FCM", "Lấy token FCM không thành công", task.getException());
                        return;
                    }

                    // Lấy token FCM
                    String firebaseToken = task.getResult();
                    Log.d("FCM", "Firebase Token: " + firebaseToken);

                    // Gửi token FCM lên server
                    sendTokenToServer(email, firebaseToken);
                });

        // Chuyển đến HomeActivity
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void showLoginError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Lấy token FCM không thành công", task.getException());
                        return;
                    }

                    // Lấy token FCM mới
                    String token = task.getResult();
                    Log.d("FCM", "Token: " + token);

                    // Gửi token đến server
                    sendTokenToServer(emailInput.toString(), token);
                });
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showRegisterSuccess(String message) {

    }

    @Override
    public void showRegisterError(String error) {

    }

    @Override
    public void showChangePasswordSuccess(String message) {

    }

    @Override
    public void showChangePasswordError(String error) {

    }

    private void sendTokenToServer(String email, String firebaseToken) {
        ApiService apiService = APIClient.getClient().create(ApiService.class);
        UpdateTokenRequest request = new UpdateTokenRequest(email, firebaseToken);

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

}