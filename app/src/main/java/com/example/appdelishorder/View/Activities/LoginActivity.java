package com.example.appdelishorder.View.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.appdelishorder.Presenter.accountPresenter;
import com.example.appdelishorder.R;
import com.example.appdelishorder.Retrofit.APIClient;
import com.example.appdelishorder.Retrofit.ApiService;
import com.example.appdelishorder.Utils.SessionManager;

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
        sessionManager.saveEmail(emailInput.getText().toString().trim());
        sessionManager.saveToken(token);
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void showLoginError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
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

}