package com.example.appdelishorder.Model;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class Comment {
    private String accountEmail;
    private int productId;
    private  String regTime;
    private String descript;
    private int evaluate;
    private  String  customerName;
    private String customerAvatar;

    public Comment(String accountEmail, int productId, String regTime, String descript, int evaluate, String customerName, String customerAvatar) {
        this.accountEmail = accountEmail;
        this.productId = productId;
        this.regTime = regTime;
        this.descript = descript;
        this.evaluate = evaluate;
        this.customerName = customerName;
        this.customerAvatar = customerAvatar;
    }
    public Comment(String accountEmail, int productId, String regTime, String descript, int evaluate) {
        this.accountEmail = accountEmail;
        this.productId = productId;
        this.regTime = regTime;
        this.descript = descript;
        this.evaluate = evaluate;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public int getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(int evaluate) {
        this.evaluate = evaluate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAvatar() {
        return customerAvatar;
    }

    public void setCustomerAvatar(String customerAvatar) {
        this.customerAvatar = customerAvatar;
    }

    public LocalDateTime getRegTimeAsDateTime() {
        if (regTime == null || regTime.isEmpty()) {
            return null;
        }

        try {
            // Thử nhiều định dạng khác nhau
            return LocalDateTime.parse(regTime, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e1) {
            try {
                return LocalDateTime.parse(regTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e2) {
                return null;
            }
        }
    }
}
