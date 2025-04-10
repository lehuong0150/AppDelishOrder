package com.example.appdelishorder.Presenter;

import com.example.appdelishorder.Contract.categoryContract;
import com.example.appdelishorder.Model.Category;
import com.example.appdelishorder.Retrofit.APIClient;
import com.example.appdelishorder.Retrofit.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class categoryPresenter implements categoryContract.Presenter {
    private categoryContract.View view;
    private ApiService apiService;

    public categoryPresenter(categoryContract.View view) {
        this.view = view;
        this.apiService = APIClient.getClient().create(ApiService.class);;
    }

    @Override
    public void loadCategories() {
        // Gọi API qua ApiService
        Call<List<Category>> call = apiService.getCategories();

        // Thực hiện cuộc gọi không đồng bộ
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (view != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Gọi phương thức showCategories trong View để hiển thị danh sách
                        view.showCategories(response.body());
                    } else {
                        // Gọi phương thức showError nếu phản hồi không thành công
                        view.showError("Failed to load categories");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                if (view != null) {
                    // Gọi phương thức showError nếu cuộc gọi API thất bại
                    view.showError(t.getMessage());
                }
            }
        });
    }

    @Override
    public void onDetach() {
        view = null;
    }
}
