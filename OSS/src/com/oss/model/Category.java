package com.oss.model;

public class Category {
    private int    categoryId;
    private String categoryName;
    private String description;
    private String status;
    private int    productCount;

    public int    getCategoryId()          { return categoryId; }
    public void   setCategoryId(int v)     { this.categoryId = v; }
    public String getCategoryName()        { return categoryName; }
    public void   setCategoryName(String v){ this.categoryName = v; }
    public String getDescription()         { return description; }
    public void   setDescription(String v) { this.description = v; }
    public String getStatus()              { return status; }
    public void   setStatus(String v)      { this.status = v; }
    public int    getProductCount()        { return productCount; }
    public void   setProductCount(int v)   { this.productCount = v; }
}
