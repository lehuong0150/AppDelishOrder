package com.example.appdelishorder.View.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.appdelishorder.Contract.customerContract;
import com.example.appdelishorder.Model.Customer;
import com.example.appdelishorder.Presenter.customerPresenter;
import com.example.appdelishorder.R;
import com.example.appdelishorder.Utils.SessionManager;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements customerContract.View {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;

    private EditText etName, etEmail, etPhone, etBirthdate, etAddress;
    private CircleImageView imgAvatar;
    private ImageButton btnImage, btnBack, btnSetting;
    private Spinner spinnerGender;
    private Button btnUpdate;
    private customerPresenter presenter;
    private Customer currentCustomer; // Biến toàn cục để lưu thông tin khách hàng hiện tại

    private String avatarPath; // Đường dẫn avatar hiện tại
    private Uri selectedImageUri; // URI của ảnh mới được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Nếu chưa có quyền, yêu cầu quyền
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        // Initialize views
        imgAvatar = findViewById(R.id.ed_profile_image);
        btnImage = findViewById(R.id.btnCamera);
        btnBack = findViewById(R.id.back_editProfile);
        btnSetting = findViewById(R.id.btnSettings);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etBirthdate = findViewById(R.id.etBirthdate);
        etAddress = findViewById(R.id.etAddress);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Initialize presenter
        presenter = new customerPresenter(this);

        SessionManager sessionManager = new SessionManager(this);
        String emailAccount = sessionManager.getEmail();
        // Load customer data
        presenter.loadCustomerInfo(emailAccount);

        // Handle back button click
        btnBack.setOnClickListener(v -> {
            finish();
        });
        // Handle settings button click
        btnSetting.setOnClickListener(v -> {
            // Open settings activity
            Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        // Handle image button click
        btnImage.setOnClickListener(v -> {
            openGallery();
        });

        // Handle update button click
        btnUpdate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String birthdate = etBirthdate.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String gender = spinnerGender.getSelectedItem().toString();

            // Kiểm tra các trường bắt buộc
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy ID từ khách hàng hiện tại
            int customerId = currentCustomer.getId();

            // Tạo đối tượng Customer mới với thông tin đã cập nhật
            Customer updatedCustomer = new Customer();
            updatedCustomer.setId(customerId);
            updatedCustomer.setName(name);
            updatedCustomer.setAccountEmail(email);
            updatedCustomer.setPhone(phone);
            updatedCustomer.setBirthdate(birthdate);
            updatedCustomer.setAddress(address);
            updatedCustomer.setGender(gender);

            // Xử lý avatar
            if (selectedImageUri != null) {
                // Nếu người dùng đã chọn ảnh mới, sử dụng đường dẫn của ảnh mới
                updatedCustomer.setAvatar(getRealPathFromURI(selectedImageUri));
            } else {
                // Nếu không có ảnh mới, giữ nguyên avatar hiện tại
                updatedCustomer.setAvatar(avatarPath);
            }

            // Gọi presenter để cập nhật thông tin
            presenter.updateCustomerInfo(customerId, updatedCustomer);
        });
    }

    // Lấy đường dẫn thực từ URI
    private String getRealPathFromURI(Uri contentUri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }

        // Nếu không lấy được đường dẫn thực, trả về URI dưới dạng chuỗi
        if (result == null) {
            result = contentUri.toString();
        }

        return result;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Hiển thị ảnh đã chọn trên giao diện
                Glide.with(this)
                        .load(selectedImageUri)
                        .into(imgAvatar);

                Toast.makeText(this, "Đã chọn ảnh mới thành công", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void showLoading() {
        // Hiển thị loading nếu cần
    }

    @Override
    public void hideLoading() {
        // Ẩn loading nếu cần
    }

    @Override
    public void displayCustomerInfo(Customer customer) {
        if (customer != null) {
            currentCustomer = customer;
            etName.setText(customer.getName());
            etEmail.setText(customer.getAccountEmail());
            etPhone.setText(customer.getPhone());
            etBirthdate.setText(customer.getBirthdate());
            etAddress.setText(customer.getAddress());

            // Lưu đường dẫn avatar hiện tại
            avatarPath = customer.getAvatar();

            // Load avatar image using Glide
            if (avatarPath != null && !avatarPath.isEmpty()) {
                if (avatarPath.startsWith("http")) {
                    Glide.with(this)
                            .load(avatarPath)
                            .placeholder(R.drawable.avt)
                            .error(R.drawable.avt)
                            .into(imgAvatar);
                } else if (avatarPath.startsWith("content://")) {
                    Glide.with(this)
                            .load(Uri.parse(avatarPath))
                            .placeholder(R.drawable.avt)
                            .error(R.drawable.avt)
                            .into(imgAvatar);
                } else {
                    File file = new File(avatarPath);
                    Log.d("ProfileActivity", "Avatar path: " + avatarPath + ", exists: " + file.exists());
                    Glide.with(this)
                            .load(file)
                            .placeholder(R.drawable.avt)
                            .error(R.drawable.avt)
                            .into(imgAvatar);
                }
            }

            // Set gender in spinner
            String[] genderArray = getResources().getStringArray(R.array.gender_array);

            // Gán adapter
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGender.setAdapter(adapter);

            // Chọn giới tính của customer (nếu có)
            for (int i = 0; i < genderArray.length; i++) {
                if (genderArray[i].equalsIgnoreCase(customer.getGender())) {
                    spinnerGender.setSelection(i);
                    break;
                }
            }
        }
    }

    @Override
    public void showUpdateSuccess(String message) {
        // Handle success
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        // Cập nhật lại avatarPath nếu đã thay đổi
        if (selectedImageUri != null) {
            avatarPath = getRealPathFromURI(selectedImageUri);
            // Reset selectedImageUri sau khi cập nhật thành công
            selectedImageUri = null;
        }
        // Optionally, you can finish the activity or navigate to another screen
        finish();
    }

    @Override
    public void showUpdateError(String error) {
        // Handle error
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String message) {
        // Handle error
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.d("ErrorCus", "showError: " + message);
    }
}
