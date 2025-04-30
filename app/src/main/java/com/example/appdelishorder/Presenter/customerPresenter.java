package com.example.appdelishorder.Presenter;

import android.util.Log;

import com.example.appdelishorder.Contract.customerContract;
import com.example.appdelishorder.Model.Customer;
import com.example.appdelishorder.Retrofit.APIClient;
import com.example.appdelishorder.Retrofit.ApiService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class customerPresenter implements customerContract.Presenter {

    private customerContract.View view;
    private ApiService apiService;
    private static final String TAG = "CustomerPresenter";

    public customerPresenter(customerContract.View view) {
        this.view = view;
        this.apiService = APIClient.getClient().create(ApiService.class);
    }

    @Override
    public void loadCustomerInfo(String email) {
        view.showLoading();

        // Gọi API để lấy thông tin khách hàng theo email
        apiService.getCustomerInformation(email).enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                view.hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    // Lấy thông tin thành công
                    Customer customer = response.body();
                    view.displayCustomerInfo(customer);
                } else {
                    try {
                        if (response.errorBody() != null) {
                            // Lấy chuỗi lỗi từ phản hồi của server
                            String errorResponse = response.errorBody().string();

                            // Kiểm tra xem phản hồi có phải JSON hợp lệ hay không
                            if (isValidJSON(errorResponse)) {
                                JSONObject jsonObject = new JSONObject(errorResponse);
                                String errorMessage = jsonObject.optString("error", "Không thể lấy thông tin khách hàng.");
                                view.showError(errorMessage);
                            } else {
                                // Nếu phản hồi không phải là JSON
                                view.showError("Lỗi từ server: " + errorResponse);
                            }
                        } else {
                            view.showError("Không thể lấy thông tin khách hàng. Vui lòng thử lại sau.");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        view.showError("Đã xảy ra lỗi khi xử lý phản hồi từ server.");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                view.hideLoading();
                view.showError("Không thể kết nối tới server. Vui lòng kiểm tra kết nối mạng.");
                Log.e(TAG, "Error loading customer info: " + t.getMessage());
            }
        });
    }

    public void updateCustomerInfo(int idCustomer, Customer customer) {
        view.showLoading();

        apiService.updateCustomerInfo(idCustomer, customer).enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                view.hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    // Cập nhật thành công
                    view.showUpdateSuccess("Cập nhật thông tin thành công!");
                    view.displayCustomerInfo(response.body());
                } else {
                    // Xử lý lỗi
                    try {
                        String errorMessage = "Cập nhật thông tin thất bại.";
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            if (isValidJSON(errorBody)) {
                                JSONObject jsonObject = new JSONObject(errorBody);
                                errorMessage = jsonObject.optString("message", errorMessage);
                            }
                        }
                        view.showUpdateError(errorMessage);
                    } catch (Exception e) {
                        view.showUpdateError("Đã xảy ra lỗi khi xử lý phản hồi.");
                        Log.e(TAG, "Error processing response: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                view.hideLoading();
                view.showUpdateError("Không thể kết nối đến server.");
                Log.e(TAG, "Network error: " + t.getMessage());
            }
        });
    }

    // Kiểm tra chuỗi có phải JSON hợp lệ không
    private boolean isValidJSON(String json) {
        try {
            new JSONObject(json);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

}
