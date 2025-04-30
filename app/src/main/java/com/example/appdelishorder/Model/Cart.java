package com.example.appdelishorder.Model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items;
    private int deliveryFee = 16000; // Default delivery fee from the UI

    public Cart() {
        items = new ArrayList<>();
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void addItem(Product product, int quantity) {
        // Check if the product already exists in the cart
        for (CartItem item : items) {
            if (item.getProduct().getId() == product.getId()) {
                // Product exists, update quantity
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        // Product does not exist in cart, add new item
        items.add(new CartItem(product, quantity));
    }

    public void updateItemQuantity(int productId, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getId() == productId) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public void removeItem(int productId) {
        items.removeIf(item -> item.getProduct().getId() == productId);
    }

    public int getItemCount() {
        return items.size();
    }

    public int getSubtotal() {
        int subtotal = 0;
        for (CartItem item : items) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    public int getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(int deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public int getTotal() {
        return getSubtotal() + deliveryFee;
    }

    public void clear() {
        items.clear();
    }
}

