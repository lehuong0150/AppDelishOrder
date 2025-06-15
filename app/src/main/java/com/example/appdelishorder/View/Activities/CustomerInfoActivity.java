package com.example.appdelishorder.View.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerInfoActivity extends AppCompatActivity implements customerContract.View {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;

    private EditText etName, etEmail, etPhone, etBirthdate, etAddress;
    private CircleImageView imgAvatar;
    private ImageButton btnImage;
    private Button btnCreate;
    private androidx.appcompat.widget.AppCompatSpinner spinnerGender;
    private customerPresenter presenter;
    private Uri selectedImageUri;
    private String avatarPath,password;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_info);

        // Yêu cầu quyền đọc bộ nhớ nếu chưa có
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        // Ánh xạ view
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etBirthdate = findViewById(R.id.etBirthdate);
        etAddress = findViewById(R.id.etAddress);
        imgAvatar = findViewById(R.id.ed_profile_image);
        btnImage = findViewById(R.id.btnCamera);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnCreate = findViewById(R.id.btnCreate);

        presenter = new customerPresenter(this);

        // Nhận email từ intent và set vào EditText (không cho sửa)
        String email = getIntent().getStringExtra("email");
         password = getIntent().getStringExtra("password");
        etEmail.setText(email);
        etEmail.setEnabled(false);

        // Set gender spinner
        String[] genderArray = getResources().getStringArray(R.array.gender_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        // Chọn ảnh đại diện
        btnImage.setOnClickListener(v -> openGallery());

        // Chọn ngày sinh bằng DatePicker
        etBirthdate.setOnClickListener(v -> showDatePicker());

        btnCreate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String birthdate = etBirthdate.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String gender = spinnerGender.getSelectedItem().toString();

            if (name.isEmpty() || phone.isEmpty() || birthdate.isEmpty() || address.isEmpty() || gender.isEmpty()) {
                showUpdateError("Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            // Định dạng lại ngày sinh sang yyyy-MM-dd
            String formattedBirthdate = "";
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                formattedBirthdate = outputFormat.format(inputFormat.parse(birthdate));
            } catch (ParseException e) {
                showUpdateError("Ngày sinh không hợp lệ (định dạng dd/MM/yyyy)");
                return;
            }

            Customer customer = new Customer();
            customer.setName(name);
            customer.setAccountEmail(email);
            customer.setPhone(phone);
            customer.setBirthdate(formattedBirthdate);
            customer.setAddress(address);
            customer.setGender(gender);

            if (selectedImageUri != null) {
                customer.setAvatar(selectedImageUri.toString());
            } else {
                customer.setAvatar("");
            }

            presenter.createCustomer(customer);
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    etBirthdate.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                Glide.with(this)
                        .load(selectedImageUri)
                        .into(imgAvatar);
                Toast.makeText(this, "Đã chọn ảnh mới thành công", Toast.LENGTH_SHORT).show();
            }
        }
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
        if (result == null) {
            result = contentUri.toString();
        }
        return result;
    }

    // Các hàm giao diện contract
    @Override
    public void showLoading() {}

    @Override
    public void hideLoading() {}

    @Override
    public void displayCustomerInfo(Customer customer) {}

    @Override
    public void showUpdateSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("email", etEmail.getText().toString());
        intent.putExtra("password", password);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void showUpdateError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        Log.d("ErrorCus", "showError: " + error);
    }

    @Override
    public void showError(String message) {}
}