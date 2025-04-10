package com.example.appdelishorder.Contract;

import com.example.appdelishorder.Model.Orders;

import java.util.List;

public interface orderContract {
    interface View {
        void showLoading();
        void hideLoading();
        void showOrders(List<Orders> orders);
        void showError(String message);
    }

    interface Presenter {
        void getOrdersByAccount(String accountEmail);
        void onDetach();
    }
}
