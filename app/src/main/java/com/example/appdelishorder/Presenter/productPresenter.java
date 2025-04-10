package com.example.appdelishorder.Presenter;

import com.example.appdelishorder.Contract.productContract;
import com.example.appdelishorder.Model.Product;
import com.example.appdelishorder.Retrofit.APIClient;
import com.example.appdelishorder.Retrofit.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class productPresenter implements productContract.Presenter {
    private productContract.View view;
    private ApiService apiService;

    public productPresenter(productContract.View view) {
        this.view = view;
        this.apiService = APIClient.getClient().create(ApiService.class);
    }


    @Override
    public void loadProducts() {
        Call<List<Product>> call = apiService.getProducts();
        call.enqueue(new Callback<List<Product>>() {

            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    view.showProducts(response.body());
                } else {
                    view.showErrorProduct("Lỗi tải danh sách sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                view.showErrorProduct("Lỗi kết nối đến server");
            }
        });
    }

    @Override
    public void loadProductsByCategory(String categoryId) {
        Call<List<Product>> call = apiService.getProductsByCategory(categoryId);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (view != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        view.showProducts(response.body());
                    } else {
                        view.showErrorProduct("Không có sản phẩm nào!");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (view != null) {
                    view.showErrorProduct("Lỗi kết nối: " + t.getMessage());
                }
            }
        });
    }
    public void searchProducts(String keyword) {
        // Gửi yêu cầu tìm kiếm sản phẩm từ API
        Call<List<Product>> call = apiService.filterProductsByQuery(keyword);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    List<Product> products = response.body();
                    if (products != null && !products.isEmpty()) {
                        view.showProducts(products); // Hiển thị kết quả tìm kiếm
                    } else {
                        view.showErrorProduct("Không tìm thấy sản phẩm nào.");
                    }
                } else {
                    view.showErrorProduct("Không tìm thấy sản phẩm.");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                view.showErrorProduct("Lỗi kết nối: " + t.getMessage());
            }
        });
    }


    @Override
    public void onDetach() {

    }
}
