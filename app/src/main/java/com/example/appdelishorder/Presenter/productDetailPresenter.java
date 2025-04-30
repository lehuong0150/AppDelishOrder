package com.example.appdelishorder.Presenter;

import com.example.appdelishorder.Contract.productDetailContract;
import com.example.appdelishorder.Model.Cart;
import com.example.appdelishorder.Model.Comment;
import com.example.appdelishorder.Model.Product;
import com.example.appdelishorder.Retrofit.APIClient;
import com.example.appdelishorder.Retrofit.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class productDetailPresenter implements productDetailContract.Presenter {

    private productDetailContract.View view;
    private ApiService apiService;
    private Product currentProduct;
    private int quantity = 1; // Default quantity
    private Cart cart;

    public productDetailPresenter(productDetailContract.View view) {
        this.view = view;
        this.apiService = APIClient.getClient().create(ApiService.class);
        this.cart = cart;
    }

    @Override
    public void loadProductDetails(int productId) {
        if (view == null) return;

        view.showLoading();

        apiService.getProductById(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (view == null) return;

                view.hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    currentProduct = response.body();
                    view.displayProductDetails(currentProduct);
                    updateTotalPrice();
                } else {
                    view.showError("Failed to load product details");
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                if (view == null) return;

                view.hideLoading();
                view.showError("Network error: " + t.getMessage());
            }
        });
    }
    @Override
    public void incrementQuantity() {
        if (currentProduct == null) return;

        // Make sure we don't exceed available stock
        if (quantity < currentProduct.getQuantity()) {
            quantity++;
            view.displayQuantity(quantity);
            updateTotalPrice();
        } else {
            view.showError("Maximum available quantity reached");
        }
    }

    @Override
    public void decrementQuantity() {
        if (quantity > 1) {
            quantity--;
            view.displayQuantity(quantity);
            updateTotalPrice();
        }
    }

    @Override
    public void updateTotalPrice() {
        if (currentProduct == null) return;

        float totalPrice = currentProduct.getPrice() * quantity;
        view.displayTotalPrice(totalPrice);
    }

    @Override
    public void addToCart() {
        if (currentProduct == null) return;

        // Add the product to the cart
        cart.addItem(currentProduct, quantity);

        // Show success message
        view.showAddedToCartSuccess();
    }

    @Override
    public void detachView() {
        this.view = null;
    }
}