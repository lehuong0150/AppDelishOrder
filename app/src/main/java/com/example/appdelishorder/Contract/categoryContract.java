package com.example.appdelishorder.Contract;

import com.example.appdelishorder.Model.Category;

import java.util.List;

public interface categoryContract {
    interface View {
        void showLoading();
        void hideLoading();
        void showCategories(List<Category> categories);
        void showError(String message);
    }

    interface Presenter {
        void loadCategories();
        void onDetach();
    }
}

