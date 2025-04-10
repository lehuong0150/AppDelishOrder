package com.example.appdelishorder.Contract;

import com.example.appdelishorder.Model.Comment;

import java.util.List;

public interface commentContract {
    interface View {
        void showLoading();
        void hideLoading();
        void showComments(List<Comment> comments);
        void showError(String message);
    }

    interface Presenter {
        void getCommentsByProduct(int productId);
        void onDetach();
    }
}
