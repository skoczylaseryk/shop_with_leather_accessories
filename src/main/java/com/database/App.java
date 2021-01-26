package com.database;

import com.database.models.Product;
import com.database.models.ShoppingCart;
import com.database.models.enums.ProductType;
import com.database.services.CustomerService;
import com.database.models.Address;
import com.database.models.Customer;
import com.database.services.ProductService;
import com.database.services.ShoppingCartService;

import java.util.Date;
import java.util.HashMap;

public class App {
    public static void main(String[] args) {
        Product product = new Product("test6", 0, 0, 1, "test", 1, ProductType.BAG);
        ProductService productService = new ProductService();

        productService.addProductToDatabase(product);
        productService.addKeyValueProperty(product.getId(), "123", "123");
        productService.addKeyValueProperty(product.getId(), "123", "123");
        productService.addKeyValueProperty(product.getId(), "123", "123");
        System.out.println(product.getProperties());
    }
}
