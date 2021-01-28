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
import static org.testng.AssertJUnit.assertNull;

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
    public void method_addProductToShoppingCart_desc_ShoppingCartServiceShouldAddProductToShoppingCart() {
        Address address = new Address("Poland", "30-091", "Cracow", "street", 3);
        Customer customer = new Customer("Patrick", "Smith", "xyz@test.com", new Date(11111999L), "password", false, address);

        ShoppingCart shoppingCart = new ShoppingCart(new Date(11112020), 0, Status.IN_PROGRESS, customer);

        shoppingCartService.addShoppingCartToDatabase(shoppingCart);

        Product product = new Product("test1", 0, 100, 1, "testSHC1", 1, ProductType.BAG);
        productService.addProductToDatabase(product);

        shoppingCartService.addProductToShoppingCart(shoppingCart.getId(), product);

        deleteInputedShoppingCartFromDatabase(shoppingCart.getId());
        deleteInputedCustomerFromDatabase(customer.getId());
        deleteInputedAddressFromDatabase(address.getId());

        assertEquals(1, shoppingCart.getProducts().size());
        assertEquals(1, product.getShoppingCarts().size());
        assertEquals(100.0f, shoppingCart.getTotalPrice());
    }

    @Test
    public void method_removeShoppingCartFromDatabase_desc_ShoppingCartServiceShouldRemoveEmptyShoppingCart() {
        Address address = new Address("Poland", "30-091", "Cracow", "street", 3);
        Customer customer = new Customer("Cris", "Smith", "xyz@test.com", new Date(11111999L), "password", false, address);

        ShoppingCart shoppingCart = new ShoppingCart(new Date(11112020), 0, Status.IN_PROGRESS, customer);

        shoppingCartService.addShoppingCartToDatabase(shoppingCart);
        shoppingCartService.removeShoppingCartFromDatabase(shoppingCart.getId());

        deleteInputedCustomerFromDatabase(customer.getId());
        deleteInputedAddressFromDatabase(address.getId());

        assertNull(entityManager.find(ShoppingCart.class, shoppingCart.getId()));
    }

    @Test
    public void method_removeShoppingCartFromDatabase_desc_ShoppingCartServiceShouldRemoveShoppingCartWithProduct() {
        Address address = new Address("Poland", "30-091", "Cracow", "street", 3);
        Customer customer = new Customer("Mike", "Smith", "xyz@test.com", new Date(11111999L), "password", false, address);

        ShoppingCart shoppingCart = new ShoppingCart(new Date(11112020), 0, Status.IN_PROGRESS, customer);

        Product product = new Product("test1", 0, 100, 1, "testSHC2", 1, ProductType.BAG);
        productService.addProductToDatabase(product);

        shoppingCartService.addShoppingCartToDatabase(shoppingCart);
        shoppingCartService.addProductToShoppingCart(shoppingCart.getId(), product);

        shoppingCartService.removeShoppingCartFromDatabase(shoppingCart.getId());

        deleteInputedCustomerFromDatabase(customer.getId());
        deleteInputedAddressFromDatabase(address.getId());
        deleteInputedProductFromDatabase(product.getId());

        assertNull(entityManager.find(ShoppingCart.class, shoppingCart.getId()));
        assertEquals(0, product.getShoppingCarts().size());
    }

    @Test
    public void method_removeProductFromShoppingCart_desc_ShoppingCartServiceShouldRemoveProductFromShoppingCart() {
        Address address = new Address("Poland", "30-091", "Cracow", "street", 3);
        Customer customer = new Customer("Brandon", "Smith", "xyz@test.com", new Date(11111999L), "password", false, address);

        ShoppingCart shoppingCart = new ShoppingCart(new Date(11112020), 0, Status.IN_PROGRESS, customer);

        Product product = new Product("test1", 0, 100, 1, "testSHC3", 1, ProductType.BAG);
        productService.addProductToDatabase(product);

        shoppingCartService.addShoppingCartToDatabase(shoppingCart);

        shoppingCartService.addProductToShoppingCart(shoppingCart.getId(), product);
        shoppingCartService.removeProductFromShoppingCart(shoppingCart.getId(), product);

        deleteInputedShoppingCartFromDatabase(shoppingCart.getId());
        deleteInputedCustomerFromDatabase(customer.getId());
        deleteInputedAddressFromDatabase(address.getId());
        deleteInputedProductFromDatabase(product.getId());

        assertEquals(0.0f,shoppingCart.getTotalPrice());
        assertEquals(0, shoppingCart.getProducts().size());
        assertEquals(0, product.getShoppingCarts().size());
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

    private void deleteInputedProductFromDatabase(Long productId) {
        EntityTransaction tx = entityManager.getTransaction();
        Product product = entityManager.find(Product.class, productId);
        tx.begin();
        entityManager.remove(product);
        tx.commit();
    }

    @AfterSuite
    private void closeConnection() {
        shoppingCartService.closeSession();
        entityManager.close();
        entityManagerFactory.close();
    }
}
