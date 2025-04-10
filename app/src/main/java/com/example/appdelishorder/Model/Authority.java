package com.example.appdelishorder.Model;

public class Authority {
    private String accountEmail;
    private String roleId;

    public Authority(String accountEmail, String roleId) {
        this.accountEmail = accountEmail;
        this.roleId = roleId;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
