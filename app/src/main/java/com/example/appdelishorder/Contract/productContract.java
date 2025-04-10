package com.example.appdelishorder.Contract;

import com.example.appdelishorder.Model.Product;

import java.util.List;

public interface productContract {
    interface View {
        void showLoading();
        void hideLoading();
        void showProducts(List<Product> products);
        void showErrorProduct(String message);
    }

    interface Presenter {
        void loadProducts();
        void loadProductsByCategory(String categoryId);
        void searchProducts(String query);
        void onDetach();
    }
}
