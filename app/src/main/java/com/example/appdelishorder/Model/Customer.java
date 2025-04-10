package com.example.appdelishorder.Model;

import java.util.Date;

public class Customer {
    private int id;
    private String name;
    private String phone;
    private String address;
    private String gender;
    private Date birthdate;
    private String accountEmail;

    public Customer(int id, String name, String phone, String address, String gender, Date birthdate, String accountEmail) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.gender = gender;
        this.birthdate = birthdate;
        this.accountEmail = accountEmail;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }
}
