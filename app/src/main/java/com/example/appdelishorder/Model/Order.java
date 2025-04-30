package com.example.appdelishorder.Model;

import com.example.appdelishorder.Utils.OrderStatusUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Order {
    private int id;
    private String shippingAddress;
    private String phone;
    private String regTime;
    private int status;
    private String accountEmail;
    private String nameCustomer;
    private String paymentMethod;
    private String paymentStatus;
    private float totalPrice;
    private boolean isRate;
    private List<OrderDetail> orderDetails;

    public Order() {
        // Constructor rỗng thật sự — để Firebase/Gson deserialize không lỗi
    }

    public static Order createNewOrder() {
        Order order = new Order();
        order.regTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                .format(new Date());
        order.status = OrderStatusUtil.STATUS_PENDING;
        return order;
    }
    public Order(int id, String shippingAddress, String phone, String regTime, int status, String nameCustomer, String paymentMethod, String paymentStatus, float totalPrice, boolean isRate) {
        this.id = id;
        this.shippingAddress = shippingAddress;
        this.phone = phone;
        this.regTime = regTime;
        this.status = status;
        this.nameCustomer = nameCustomer;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.totalPrice = totalPrice;
        this.isRate = isRate;}
    public Order(String shippingAddress, String phone, String regTime, int status, String accountEmail, String paymentMethod, String paymentStatus, float totalPrice, boolean isRate) {
        this.shippingAddress = shippingAddress;
        this.phone = phone;
        this.regTime = regTime;
        this.status = status;
        this.accountEmail = accountEmail;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.totalPrice = totalPrice;
        this.isRate = isRate; // Default value for isRate}
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

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getNameCustomer() {
        return nameCustomer;
    }

    public void setNameCustomer(String nameCustomer) {
        this.nameCustomer = nameCustomer;
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

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public boolean isRate() {
        return isRate;
    }
    public void setRate(boolean rate) {
        isRate = rate;
    }

    public String getAccountEmail() {
        return accountEmail;
    }
    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }
}
