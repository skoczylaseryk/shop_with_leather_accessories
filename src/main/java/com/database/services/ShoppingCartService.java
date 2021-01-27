package com.database.services;

import com.database.models.Customer;
import com.database.models.Product;
import com.database.models.ShoppingCart;
import com.database.services.exceptions.InvalidParameterProvidedException;
import com.database.services.entityManager.EntityManagerService;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;

public class ShoppingCartService extends EntityManagerService {

    public void addShoppingCartToDatabase(ShoppingCart shoppingCart) {
        if (shoppingCart == null) {
            throw new InvalidParameterProvidedException("shoppingCart shouldn't be null");
        }
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(shoppingCart);
        shoppingCart.getCustomer().getShoppingCarts().add(shoppingCart);
        tx.commit();
    }

    public ShoppingCart getShoppingCartById(Long shoppingCartId) {
        if (shoppingCartId == null) {
            throw new InvalidParameterProvidedException("shoppingCartId shouldn't be null");
        }
        return entityManager.find(ShoppingCart.class, shoppingCartId);
    }

    public void removeShoppingCartFromDatabase(Long shoppingCartId) {
        ShoppingCart shoppingCartById = getShoppingCartById(shoppingCartId);
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.remove(shoppingCartById);
        tx.commit();
    }

    public void removeProductFromShoppingCart(Long shoppingCartId, Product product) {
        ShoppingCart shoppingCartById = getShoppingCartById(shoppingCartId);
        EntityTransaction tx = entityManager.getTransaction();
        shoppingCartById.setTotalPrice(shoppingCartById.getTotalPrice() - product.getPriceBeforeDiscount());
        tx.begin();
        shoppingCartById.getProducts().remove(product);
        entityManager.merge(shoppingCartById);
        tx.commit();
    }

    public void addProductToShoppingCart(Long shoppingCartId, Product product) {
        ShoppingCart shoppingCartById = getShoppingCartById(shoppingCartId);
        shoppingCartById.getProducts().add(product);
        product.getShoppingCarts().add(shoppingCartById);
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        shoppingCartById.setTotalPrice(shoppingCartById.getTotalPrice() + product.getPriceAfterDiscount());
        tx.commit();
    }

    public void updateCustomerOfShoppingCart(Long shoppingCartId, Customer customer) {
        if (customer == null || customer.getDateOfBirth() == null || customer.getEmail() == null || customer.getName() == null || customer.getPassword() == null) {
            throw new InvalidParameterProvidedException("customer shouldn't be null and customer parameters shouldn't be null");
        }

        ShoppingCart shoppingCartById = getShoppingCartById(shoppingCartId);
        EntityTransaction tx = entityManager.getTransaction();

        Query q = entityManager
                .createQuery(
                        "from Customer c where" +
                                " c.dateOfBirth = ?1 AND c.email = ?2 " +
                                "AND c.isAdmin = ?3 " +
                                "AND c.name = ?4 " +
                                "AND c.password = ?5" +
                                "AND c.surname = ?6" +
                                "AND c.address_id = ?7", Customer.class);
        q.setParameter(1, customer.getDateOfBirth());
        q.setParameter(2, customer.getEmail());
        q.setParameter(3, customer.isAdmin());
        q.setParameter(4, customer.getName());
        q.setParameter(5, customer.getPassword());
        q.setParameter(6, customer.getSurname());
        q.setParameter(7, customer.getAddress().getId());

        List resultList = q.getResultList();
        if (resultList.size() == 1) {
            tx.begin();
            shoppingCartById.setCustomer(customer);
            entityManager.merge(shoppingCartById);
            tx.commit();
        } else {
            tx.begin();
            entityManager.persist(customer);
            shoppingCartById.setCustomer(customer);
            entityManager.merge(shoppingCartById);
            tx.commit();
        }
    }
}
