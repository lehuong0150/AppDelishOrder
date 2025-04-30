package com.example.appdelishorder.Contract;

import com.example.appdelishorder.Model.Comment;
import com.example.appdelishorder.Model.Product;

import java.util.List;

public interface productDetailContract {
    interface View {
        void showLoading();
        void hideLoading();
        void displayProductDetails(Product product);
        void displayQuantity(int quantity);
        void displayTotalPrice(float price);
        void showError(String message);
        void showAddedToCartSuccess();
    }

    interface Presenter {
        void loadProductDetails(int productId);
        void incrementQuantity();
        void decrementQuantity();
        void updateTotalPrice();
        void addToCart();
        void detachView();
    }
}