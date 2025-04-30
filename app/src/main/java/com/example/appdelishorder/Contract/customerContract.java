package com.example.appdelishorder.Contract;

import com.example.appdelishorder.Model.Customer;

public interface customerContract {
    interface View {
        void showLoading();
        void hideLoading();
        void displayCustomerInfo(Customer customer);
        void showUpdateSuccess(String message);
        void showUpdateError(String error);
        void showError(String message);
    }

    interface Presenter {
        void loadCustomerInfo(String email);
        void updateCustomerInfo(int idCustomer, Customer customer);
    }
}
