package com.example.appdelishorder.Presenter;

import com.example.appdelishorder.Contract.cartContract;
import com.example.appdelishorder.Model.Cart;
import com.example.appdelishorder.Model.CartItem;
import com.example.appdelishorder.Model.Order;
import com.example.appdelishorder.Model.OrderDetail;
import com.example.appdelishorder.Retrofit.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class cartPresenter implements cartContract.Presenter {

    private cartContract.View view;
    private ApiService apiService;
    private Cart cart;
    private String userEmail;

    public cartPresenter(cartContract.View view, ApiService apiService, Cart cart, String userEmail) {
        this.view = view;
        this.apiService = apiService;
        this.cart = cart;
        this.userEmail = userEmail;
    }

    @Override
    public void loadCartItems() {
        if (view == null) return;

        if (cart.getItems().isEmpty()) {
            view.showEmptyCart();
        } else {
            view.displayCartItems(cart.getItems());
            view.displaySubtotal(cart.getSubtotal());
            view.displayDeliveryFee(cart.getDeliveryFee());
            view.displayTotal(cart.getTotal());
        }
    }

    @Override
    public void incrementItemQuantity(int position) {
        if (view == null || position >= cart.getItems().size()) return;

        CartItem item = cart.getItems().get(position);
        if (item.getQuantity() < item.getProduct().getQuantity()) {
            cart.updateItemQuantity(item.getProduct().getId(), item.getQuantity() + 1);
            view.updateItemUI(position);
            view.displaySubtotal(cart.getSubtotal());
            view.displayTotal(cart.getTotal());
        } else {
            view.showError("Maximum available quantity reached");
        }
    }

    @Override
    public void decrementItemQuantity(int position) {
        if (view == null || position >= cart.getItems().size()) return;

        CartItem item = cart.getItems().get(position);
        if (item.getQuantity() > 1) {
            cart.updateItemQuantity(item.getProduct().getId(), item.getQuantity() - 1);
            view.updateItemUI(position);
            view.displaySubtotal(cart.getSubtotal());
            view.displayTotal(cart.getTotal());
        }
    }

    @Override
    public void removeCartItem(int position) {
        if (view == null || position >= cart.getItems().size()) return;

        CartItem item = cart.getItems().get(position);
        cart.removeItem(item.getProduct().getId());

        if (cart.getItems().isEmpty()) {
            view.showEmptyCart();
        } else {
            view.displayCartItems(cart.getItems());
            view.displaySubtotal(cart.getSubtotal());
            view.displayTotal(cart.getTotal());
        }
    }

    @Override
    public void placeOrder(String address, String phone, String paymentMethod) {
        if (view == null || cart.getItems().isEmpty()) return;

        view.showLoading();

        // Create order object
        Order order = Order.createNewOrder();
        order.setAccountEmail(userEmail);
        order.setShippingAddress(address);
        order.setPhone(phone);
        order.setPaymentMethod(paymentMethod);
        order.setTotalPrice(cart.getTotal());
        order.setStatus(Integer.parseInt("Pending"));

        // Create order details
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            OrderDetail detail = new OrderDetail();
            detail.setProductId(item.getProduct().getId());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getProduct().getPrice());
            orderDetails.add(detail);
        }
        order.setOrderDetails(orderDetails);

        // Send order to server
        apiService.createOrder(order).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (view == null) return;

                view.hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    // Clear cart after successful order
                    cart.clear();
                    view.showOrderSuccess(response.body());
                } else {
                    view.showError("Failed to place order: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                if (view == null) return;

                view.hideLoading();
                view.showError("Network error: " + t.getMessage());
            }
        });
    }

    @Override
    public void detachView() {
        this.view = null;
    }
}