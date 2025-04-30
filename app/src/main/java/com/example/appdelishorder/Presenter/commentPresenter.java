package com.example.appdelishorder.Presenter;
import com.example.appdelishorder.Contract.commentContract;
import com.example.appdelishorder.Model.Comment;
import com.example.appdelishorder.Model.Customer;
import com.example.appdelishorder.Model.Product;
import com.example.appdelishorder.Retrofit.APIClient;
import com.example.appdelishorder.Retrofit.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class commentPresenter implements commentContract.Presenter {
    private commentContract.View view;
    private ApiService apiService;
    private List<Call<?>> pendingCalls;
    private Product currentProduct;

    public commentPresenter(commentContract.View view) {
        this.view = view;
        this.apiService = APIClient.getClient().create(ApiService.class);
        this.pendingCalls = new ArrayList<>();
    }


    @Override
    public void getCommentsByProductId(int productId) {
        Call<List<Comment>> call = apiService.getCommentsByProductId(productId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (view != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Comment> comments = response.body();
                        if (!comments.isEmpty()) {
                            view.showComments(comments);

                            // Calculate average rating
                            float totalRating = 0;
                            for (Comment comment : comments) {
                                totalRating += comment.getEvaluate();
                            }
                            float averageRating = totalRating / comments.size();
                            view.updateProductRating(averageRating);
                        } else {
                            view.showEmptyComments();
                        }
                    } else {
                        view.showError("Failed to load comments. Code: " + response.code());
                    }
                    view.showLoading(false);
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                if (view != null) {
                    view.showError("Error: " + t.getMessage());
                    view.showLoading(false);
                }
            }
        });
    }

    @Override
    public void submitComment(Comment comment) {
        Call<Comment> call = apiService.addComment(comment);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (view != null) {
                    if (response.isSuccessful()) {
                        view.onCommentSubmitted();
                    } else {
                        view.showError("Failed to submit comment. Code: " + response.code());
                    }
                    view.showLoading(false);
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                if (view != null) {
                    view.showError("Error: " + t.getMessage());
                    view.showLoading(false);
                }
            }
        });
    }

    @Override
    public void onDetach() {
        this.view = null;
    }
}