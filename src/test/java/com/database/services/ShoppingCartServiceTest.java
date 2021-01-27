package com.database.services;

import com.database.models.Address;
import com.database.models.Customer;
import com.database.models.Product;
import com.database.models.ShoppingCart;
import com.database.models.enums.ProductType;
import com.database.models.enums.Status;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Date;

import static org.testng.AssertJUnit.assertEquals;

public class ShoppingCartServiceTest {
    private ShoppingCartService shoppingCartService = new ShoppingCartService();
    private ProductService productService = new ProductService();
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("shop-database");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();

    @Test
    public void method_addShoppingCartToDatabase_desc_ShoppingCartServiceShouldAddShoppingCartToDatabase() {
        Address address = new Address("Poland", "30-091", "Cracow", "street", 3);
        Customer customer = new Customer("Patrick", "Smith", "xyz@test.com", new Date(11111999L), "password", false, address);

        ShoppingCart shoppingCart = new ShoppingCart(new Date(11112020), 0, Status.IN_PROGRESS, customer);

        shoppingCartService.addShoppingCartToDatabase(shoppingCart);

        deleteInputedShoppingCartFromDatabase(shoppingCart.getId());
        deleteInputedCustomerFromDatabase(customer.getId());
        deleteInputedAddressFromDatabase(address.getId());
    }

    @Test
    public void method_addProductToShoppingCart_ShoppingCartServiceShouldAddProductToShoppingCart() {
        Address address = new Address("Poland", "30-091", "Cracow", "street", 3);
        Customer customer = new Customer("Patrick", "Smith", "xyz@test.com", new Date(11111999L), "password", false, address);

        ShoppingCart shoppingCart = new ShoppingCart(new Date(11112020), 0, Status.IN_PROGRESS, customer);

        shoppingCartService.addShoppingCartToDatabase(shoppingCart);

        Product product = new Product("test1", 0, 100, 1, "testSHC", 1, ProductType.BAG);
        productService.addProductToDatabase(product);

        shoppingCartService.addProductToShoppingCart(shoppingCart.getId(), product);

        assertEquals(1, shoppingCart.getProducts().size());
        assertEquals(1, product.getShoppingCarts().size());
        assertEquals(100.0f, shoppingCart.getTotalPrice());

        deleteInputedShoppingCartFromDatabase(shoppingCart.getId());
        deleteInputedCustomerFromDatabase(customer.getId());
        deleteInputedAddressFromDatabase(address.getId());
    }

    private void deleteInputedShoppingCartFromDatabase(Long shoppingCartId) {
        EntityTransaction tx = entityManager.getTransaction();
        ShoppingCart shoppingCart = entityManager.find(ShoppingCart.class, shoppingCartId);
        tx.begin();
        entityManager.remove(shoppingCart);
        tx.commit();
    }

    private void deleteInputedAddressFromDatabase(Long addressId) {
        EntityTransaction tx = entityManager.getTransaction();
        Address address = entityManager.find(Address.class, addressId);
        tx.begin();
        entityManager.remove(address);
        tx.commit();
    }

    private void deleteInputedCustomerFromDatabase(Long customerId) {
        EntityTransaction tx = entityManager.getTransaction();
        Customer customer = entityManager.find(Customer.class, customerId);
        tx.begin();
        entityManager.remove(customer);
        tx.commit();
    }

    @AfterSuite
    private void closeConnection() {
        shoppingCartService.closeSession();
        entityManager.close();
        entityManagerFactory.close();
    }
}
