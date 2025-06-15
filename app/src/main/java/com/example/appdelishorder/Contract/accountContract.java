package com.example.appdelishorder.Contract;

import com.example.appdelishorder.Model.Account;

public interface accountContract {

    interface View {
        void showLoginSuccess(String message);
        void showLoginError(String error);
        void showLoading();
        void hideLoading();
        void showRegisterSuccess(String message);
        void showRegisterError(String error);
        void showChangePasswordSuccess(String message);
        void showChangePasswordError(String error);
    }

    interface Presenter {
        void doLogin(String email, String password);
        void doRegister(String email, String password, String fullName);
        void doChangePassword(String email, String oldPassword, String newPassword);
    }
}
