package com.example.appdelishorder.Utils;

import android.util.Log;

import com.example.appdelishorder.Retrofit.ApiService;
import com.example.appdelishorder.Retrofit.APIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderUtils {

    public static void sendOrderNotification(String orderId, String message) {
        ApiService apiService = APIClient.getClient().create(ApiService.class);

        // Gửi thông báo qua API
        Call<Void> call = apiService.sendOrderNotification(orderId, message);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("OrderUtils", "Thông báo đã được gửi thành công.");
                } else {
                    Log.e("OrderUtils", "Lỗi khi gửi thông báo: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("OrderUtils", "Lỗi khi gửi thông báo: " + t.getMessage());
            }
        });
    }
}