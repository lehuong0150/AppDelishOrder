package com.example.appdelishorder.Contract;

import com.example.appdelishorder.Model.Order;
import com.example.appdelishorder.Model.OrderDetail;

import java.util.List;
public interface orderContract {
    interface View {
        void showLoading();
        void hideLoading();
        void displayOrders(List<Order> orders);
        void displayOrderDetails(Order order);
        void onOrderSuccess(Order order);
        void showError(String message);
    }

    interface Presenter {
        void loadOrders(String email, String status);
        void loadOrderDetails(int orderId);
        void placeOrder(Order order);
        void updateOrderStatus(int orderId);
    }
}
