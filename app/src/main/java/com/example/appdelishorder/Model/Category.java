package com.example.appdelishorder.Model;

import org.threeten.bp.format.DateTimeFormatter;

public class Category {
    private String id;
    private String name;
    private String imageCategory;
    private boolean isAvailable;
    private DateTimeFormatter createAt;


    public Category( String name, String imageCategory,boolean isAvailable) {
        this.name = name;
        this.imageCategory = imageCategory;
        this.isAvailable = isAvailable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageCategory() {
        return imageCategory;
    }

    public void setImageCategory(String imageCategory) {
        this.imageCategory = imageCategory;
    }

    public DateTimeFormatter getCreateAt() {
        return createAt;
    }

    public void setCreateAt(DateTimeFormatter createAt) {
        this.createAt = createAt;
    }

    public boolean isAvaible() {
        return isAvailable;
    }

    public void setAvaible(boolean avaible) {
        isAvailable = avaible;
    }


}
