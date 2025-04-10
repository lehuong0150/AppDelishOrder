package com.example.appdelishorder.Model;

public class Comment {
    private String accountEmail;
    private int productId;
    private  org.threeten.bp.LocalDateTime regTime;
    private String descript;
    private int evaluate;

    public Comment(String accountEmail, int productId, org.threeten.bp.LocalDateTime regTime, String descript, int evaluate) {
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

    public org.threeten.bp.LocalDateTime getRegTime() {
        return regTime;
    }

    public void setRegTime(org.threeten.bp.LocalDateTime regTime) {
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
}
