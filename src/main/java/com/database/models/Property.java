package com.database.models;

import javax.persistence.*;


@Entity

public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String property;
    private String result;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Property() {
    }

    public Property(String property, String result, Product product) {
        this.property = property;
        this.result = result;
        this.product = product;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
