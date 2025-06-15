package com.example.appdelishorder.View.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdelishorder.Contract.accountContract;
import com.example.appdelishorder.Presenter.accountPresenter;
import com.example.appdelishorder.R;
import com.example.appdelishorder.Utils.SessionManager;

public class SettingsActivity extends AppCompatActivity implements accountContract.View {

    private ImageButton backButton;
    private RelativeLayout changePasswordLayout;
    private accountPresenter presenter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        // Initialize the presenter
        presenter = new accountPresenter(this);
        // Initialize views
        backButton = findViewById(R.id.btn_back);
        changePasswordLayout = findViewById(R.id.change_password_layout);

        // Set listeners
        backButton.setOnClickListener(v -> onBackPressed());

        changePasswordLayout.setOnClickListener(v -> {
            // Hiển thị dialog đổi mật khẩu
            showChangePasswordDialog();
        });
    }
    private void showChangePasswordDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_change_password);

        EditText edtOld = dialog.findViewById(R.id.edtOldPassword);
        EditText edtNew = dialog.findViewById(R.id.edtNewPassword);
        EditText edtConfirm = dialog.findViewById(R.id.edtConfirmPassword);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirmChange);
        Log.d("SettingsActivity", "showChangePasswordDialog: Dialog created"+ edtOld+ edtNew + edtConfirm);
        btnConfirm.setOnClickListener(view -> {
            String oldPass = edtOld.getText().toString().trim();
            String newPass = edtNew.getText().toString().trim();
            String confirmPass = edtConfirm.getText().toString().trim();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                return;
            }
            // Lấy email từ SharedPreferences (hoặc từ nơi khác nếu cần)
            SessionManager sessionManager = new SessionManager(this); // hoặc getApplicationContext()
            String email = sessionManager.getEmail();
            // TODO: Gọi presenter đổi mật khẩu ở đây
            presenter.doChangePassword(email, oldPass, newPass);

            dialog.dismiss();
            Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
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

    }

    @Override
    public void showRegisterError(String error) {

    }

    @Override
    public void showChangePasswordSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showChangePasswordError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}