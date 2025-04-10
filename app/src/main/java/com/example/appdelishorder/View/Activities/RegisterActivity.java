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
import com.example.appdelishorder.Presenter.accountPresenter;
import com.example.appdelishorder.R;

public class RegisterActivity extends AppCompatActivity implements accountContract.View {
    private EditText emailInput, passwordInput, fullnameInput;
    private Button registerButton;
    private TextView loginButton;
    private ProgressBar progressBar;
    private accountPresenter presenter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        //find id
        emailInput = findViewById(R.id.et_email);
        passwordInput = findViewById(R.id.et_password);
        fullnameInput = findViewById(R.id.et_fullname);
        registerButton = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        loginButton = findViewById(R.id.tv_login);
        presenter = new accountPresenter(this);

        //action
        registerButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            String fullName = fullnameInput.getText().toString();

            presenter.doRegister(email, password, fullName);
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void showLoginSuccess(String message) {

    }

    @Override
    public void showLoginError(String error) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showRegisterSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // Chuyển sang LoginActivity và truyền email, password
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra("email", emailInput.getText().toString());
        intent.putExtra("password", passwordInput.getText().toString());
        startActivity(intent);
        finish();
    }

    @Override
    public void showRegisterError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}