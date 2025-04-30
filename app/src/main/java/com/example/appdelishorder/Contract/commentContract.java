package com.example.appdelishorder.Contract;

import com.example.appdelishorder.Model.Comment;
import com.example.appdelishorder.Model.Customer;

import java.util.List;

public interface commentContract {
    interface View {
        void showLoading(boolean isLoading);
        void showComments(List<Comment> comments);
        void showError(String message);
        void showEmptyComments();
        void updateProductRating(float rating);
        void onCommentSubmitted();
    }

    interface Presenter {
        void getCommentsByProductId(int productId);
        void submitComment(Comment comment);
        void onDetach();
    }
}
