package com.example.appdelishorder.Presenter;

import android.util.Log;

import com.example.appdelishorder.Contract.orderContract;
import com.example.appdelishorder.Model.Order;
import com.example.appdelishorder.Model.OrderDetail;
import com.example.appdelishorder.Retrofit.APIClient;
import com.example.appdelishorder.Retrofit.ApiService;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class orderPresenter implements orderContract.Presenter {
    private orderContract.View view;
    private ApiService apiService;

    public orderPresenter(orderContract.View view) {
        this.view = view;
        this.apiService = APIClient.getClient().create(ApiService.class);
    }

    @Override
    public void loadOrders(String email,  String  status) {

        Call<List<Order>> call = apiService.getOrders(email, status);
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body();
                    view.displayOrders(orders);
                } else {
                    view.showError("Không tải được đơn hàng!");
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                view.showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    @Override
    public void loadOrderDetails(int orderId) {
        Call<Order> call = apiService.getOrderDetailById(orderId);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order orderDetails = response.body();
                    view.displayOrderDetails(orderDetails);
                } else {
                    view.showError("Không tải được chi tiết đơn hàng!");
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                view.showError("Lỗi kết nối: " + t.getMessage());
            }
        });

    }

    @Override
    public void placeOrder(Order order) {
        Call<Order> call = apiService.createOrder(order);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order orderResponse = response.body();
                    view.onOrderSuccess(orderResponse);
                } else {
                    try {
                        // In chi tiết lỗi trả về từ server (nếu có)
                        String errorBody = response.errorBody().string();
                        Log.e("OrderError", "Mã lỗi: " + response.code() + " | Nội dung lỗi: " + errorBody);
                        view.showError("Lỗi đặt hàng: " + errorBody);
                    } catch (IOException e) {
                        Log.e("OrderError", "Không đọc được lỗi", e);
                        view.showError("Đặt hàng thất bại! (không rõ lỗi)");
                    }
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                view.showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }


}
