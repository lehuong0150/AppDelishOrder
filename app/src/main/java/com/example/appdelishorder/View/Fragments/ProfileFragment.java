package com.example.appdelishorder.View.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.Manifest;
import com.bumptech.glide.Glide;
import com.example.appdelishorder.Contract.customerContract;
import com.example.appdelishorder.Model.Customer;
import com.example.appdelishorder.Presenter.customerPresenter;
import com.example.appdelishorder.R;
import com.example.appdelishorder.Utils.SessionManager;
import com.example.appdelishorder.View.Activities.LoginActivity;
import com.example.appdelishorder.View.Activities.ProfileActivity;
import com.example.appdelishorder.View.Activities.SettingsActivity;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements customerContract.View {

    private LinearLayout personalInfoOption, settingsOption, logoutOption, shopInfoOption;
    private TextView txtName;
    private CircleImageView profileImage;
    private customerContract.Presenter presenter;
    private String avatarPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        // Initialize menu options
        personalInfoOption = view.findViewById(R.id.personalInfoOption);
        settingsOption = view.findViewById(R.id.settingsOption);
        logoutOption = view.findViewById(R.id.logoutOption);
        shopInfoOption = view.findViewById(R.id.ShopInfoOption);
        txtName = view.findViewById(R.id.tvUserName);
        profileImage = view.findViewById(R.id.imgProfile);

        presenter = new customerPresenter(this);

        // Set default user name
        SessionManager sessionManager = new SessionManager(getContext());
        String email = sessionManager.getEmail();

        // Assuming you have a method to get user name from email
        presenter.loadCustomerInfo(email);

        // Set click listeners
        personalInfoOption.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Thông tin cá nhân clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            startActivity(intent);
        });


        settingsOption.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Cài đặt clicked", Toast.LENGTH_SHORT).show();
            // Add logic to navigate to settings screen
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        });

        logoutOption.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đăng xuất clicked", Toast.LENGTH_SHORT).show();
            // Add logic to handle logout
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        shopInfoOption.setOnClickListener(v -> showShopInfoDialog());
        return view;
    }
    private void showShopInfoDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_shop_info);

        TextView tvShopName = dialog.findViewById(R.id.tvShopName);
        TextView tvShopPhone = dialog.findViewById(R.id.tvShopPhone);
        TextView tvShopAddress = dialog.findViewById(R.id.tvShopAddress);

        tvShopPhone.setOnClickListener(v -> {
            String phone = tvShopPhone.getText().toString();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            startActivity(intent);
        });

        dialog.show();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void displayCustomerInfo(Customer customer) {
        txtName.setText(customer.getName());
        avatarPath = customer.getAvatar();
        Log.d("ProfileFragment", "Avatar URL: " + avatarPath);

        // Load avatar image using Glide
        if (avatarPath != null && !avatarPath.isEmpty()) {
            if (avatarPath.startsWith("http")) {
                Glide.with(getContext())
                        .load(avatarPath)
                        .placeholder(R.drawable.avt)
                        .error(R.drawable.avt)
                        .into(profileImage);
            } else if (avatarPath.startsWith("content://")) {
                Glide.with(requireContext())
                        .load(Uri.parse(avatarPath))
                        .placeholder(R.drawable.avt)
                        .error(R.drawable.avt)
                        .into(profileImage);
            } else {
                Glide.with(requireContext())
                        .load(Uri.parse(avatarPath))
                        .placeholder(R.drawable.avt)
                        .error(R.drawable.avt)
                        .into(profileImage);
            }

    }}


    @Override
    public void showUpdateSuccess(String message) {

    }

    @Override
    public void showUpdateError(String error) {

    }

    @Override
    public void showError(String message) {

    }
}