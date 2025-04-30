package com.example.appdelishorder.Model;

public class OrderDetail {
    private int orderId;
    private  int productId;
    private String productName;
    private String imageProduct;
    private int quantity;
    private float price;
    private boolean isRated;

    public OrderDetail() {

    }

    public OrderDetail(int orderId,int productId, String nameProduct, String imageProduct, int quantity, float price, boolean isRated) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = nameProduct;
        this.imageProduct = imageProduct;
        this.quantity = quantity;
        this.price = price;
        this.isRated = isRated;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageProduct() {
        return imageProduct;
    }

    public void setImageProduct(String imageProduct) {
        this.imageProduct = imageProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
    public int getProductId() {
        return productId;
    }
    public void setProductId(int productId) {
        this.productId = productId;
    }
    public boolean isRate() {
        return isRated;
    }
    public void setRate(boolean rate) {
        this.isRated = rate;
    }
}