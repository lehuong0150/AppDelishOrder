package com.example.appdelishorder.Contract;

import com.example.appdelishorder.Model.CartItem;
import com.example.appdelishorder.Model.Order;

import java.util.List;

public interface cartContract {
    interface View {
        void showLoading();
        void hideLoading();
        void displayCartItems(List<CartItem> items);
        void displaySubtotal(int subtotal);
        void displayDeliveryFee(int deliveryFee);
        void displayTotal(int total);
        void showEmptyCart();
        void showError(String message);
        void showOrderSuccess(Order order);
        void updateItemUI(int position);
    }

    interface Presenter {
        void loadCartItems();
        void incrementItemQuantity(int position);
        void decrementItemQuantity(int position);
        void removeCartItem(int position);
        void placeOrder(String address, String phone, String paymentMethod);
        void detachView();
    }
}
