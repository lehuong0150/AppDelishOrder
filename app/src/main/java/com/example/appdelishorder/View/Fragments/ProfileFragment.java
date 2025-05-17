package com.example.appdelishorder.View.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.appdelishorder.Contract.customerContract;
import com.example.appdelishorder.Model.Customer;
import com.example.appdelishorder.Presenter.customerPresenter;
import com.example.appdelishorder.R;
import com.example.appdelishorder.Utils.SessionManager;
import com.example.appdelishorder.View.Activities.LoginActivity;
import com.example.appdelishorder.View.Activities.ProfileActivity;
import com.example.appdelishorder.View.Activities.SettingsActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements customerContract.View {

    private LinearLayout personalInfoOption, settingsOption, logoutOption;
    private TextView txtName;
    private CircleImageView profileImage;
    private customerContract.Presenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Initialize menu options
        personalInfoOption = view.findViewById(R.id.personalInfoOption);
        settingsOption = view.findViewById(R.id.settingsOption);
        logoutOption = view.findViewById(R.id.logoutOption);
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
        return view;
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
        // Assuming you have a method to load image from URL
        Glide.with(getContext())
                .load(customer.getAvatar())
                .placeholder(R.drawable.baseline_perm_contact_calendar_24)
                .into(profileImage);
    }

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