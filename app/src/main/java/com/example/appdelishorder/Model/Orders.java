package com.example.appdelishorder.Model;

public class Orders {
    private int id;
    private String shippingAddress;
    private String phone;
    private org.threeten.bp.LocalDateTime regTime;
    private int status;
    private String accountEmail;
    private String paymentMethod;
    private String paymentStatus;
    private float totalPrice;

    public Orders(int id, String shippingAddress, String phone, org.threeten.bp.LocalDateTime regTime, int status, String accountEmail, String paymentMethod, String paymentStatus, float totalPrice) {
        this.id = id;
        this.shippingAddress = shippingAddress;
        this.phone = phone;
        this.regTime = regTime;
        this.status = status;
        this.accountEmail = accountEmail;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public org.threeten.bp.LocalDateTime getRegTime() {
        return regTime;
    }

    public void setRegTime(org.threeten.bp.LocalDateTime regTime) {
        this.regTime = regTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }
}
