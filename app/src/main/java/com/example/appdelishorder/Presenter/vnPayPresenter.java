package com.example.appdelishorder.Presenter;


import com.example.appdelishorder.Contract.vnPayContract;
import com.example.appdelishorder.Model.VNPayResponse;
import com.example.appdelishorder.Retrofit.APIClient;
import com.example.appdelishorder.Retrofit.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class vnPayPresenter implements vnPayContract.Presenter {
    private vnPayContract.View view;
    private ApiService apiService;

    public vnPayPresenter(vnPayContract.View view) {
        this.view = view;
        this.apiService = APIClient.getClient().create(ApiService.class);
    }

    @Override
    public void createVNPayPayment(String amount, String orderId) {
        apiService.createPayment(amount, orderId).enqueue(new Callback<VNPayResponse>() {
            @Override
            public void onResponse(Call<VNPayResponse> call, Response<VNPayResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    view.onVNPayUrlReceived(response.body().paymentUrl);
                } else {
                    view.onVNPayError("Không lấy được link thanh toán");
                }
            }

            @Override
            public void onFailure(Call<VNPayResponse> call, Throwable t) {
                view.onVNPayError(t.getMessage());
            }
        });
    }
}