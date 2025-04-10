package com.example.appdelishorder.Model;

public class Product {
    private int id;
    private String name;
    private int price;
    private String descript;
    private int quantity;
    private String imageProduct;
    private String categoryId;
    private String categoryName;
    private boolean isAvailable;
    private String createdAt;


    public Product(String name, int price, String descript, int quantity, String imageProduct, String categoryId, String categoryName, boolean isAvailable, String createdAt) {
        this.name = name;
        this.price = price;
        this.descript = descript;
        this.quantity = quantity;
        this.imageProduct = imageProduct;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.isAvailable = isAvailable;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageProduct() {
        return imageProduct;
    }

    public void setImageProduct(String imageProduct) {
        this.imageProduct = imageProduct;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
