package com.database.services;

import com.database.models.Product;
import com.database.models.Property;
import com.database.services.exceptions.InvalidParameterProvidedException;
import com.database.services.entityManager.EntityManagerService;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;

public class ProductService extends EntityManagerService {

    public void addProductToDatabase(Product product) {
        if (!isProductCorrect(product)) {
            throw new InvalidParameterProvidedException("Product shouln't be null and some fields shouldn't be null");
        }
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(product);
        tx.commit();
    }

    public Product getProductById(Long productId) {
        if (productId == null && productId <= 0) {
            throw new InvalidParameterProvidedException("productId shouldn't be null");
        }
        return entityManager.find(Product.class, productId);
    }

    public void removeProductFromDatabase(Long productId) {
        Product productById = getProductById(productId);
        EntityTransaction tx = entityManager.getTransaction();
        removeAllPropertiesForOneProduct(productId);
        tx.begin();
        entityManager.remove(productById);
        tx.commit();
    }

    public void buyProduct(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new InvalidParameterProvidedException("quantity should be higher than 0");
        }

        Product product = getProductById(productId);

        if (quantity > product.getQuantity()) {
            throw new InvalidParameterProvidedException("provided quantity shouldn't be higher than quantity of product in database");
        }

        EntityTransaction tx = entityManager.getTransaction();
        product.setQuantity(product.getQuantity() - quantity);
        tx.begin();
        entityManager.merge(product);
        tx.commit();
    }

    public void addKeyValueProperty(Long productId, String key, String value) {
        if (key == null || value == null || key.equals("") || value.equals("")) {
            throw new InvalidParameterProvidedException("provided parameter shouldn't be null or empty string");
        }

        Product productById = getProductById(productId);
        Property property = new Property(key, value, productById);

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(property);
        productById.getProperties().add(property);
        tx.commit();
    }

    public void removeKeyValueProperty(Long productId, String key, String value) {
        if (key == null || value == null || key.equals("") || value.equals("")) {
            throw new InvalidParameterProvidedException("provided parameter shouldn't be null or empty string");
        }

        Product productById = getProductById(productId);

        Query q1 = entityManager.createQuery("from Property p where p.product = ?1 AND p.property =?2 AND p.result =?3", Property.class);
        q1.setParameter(1, productById);
        q1.setParameter(2, key);
        q1.setParameter(3, value);

        List resultList = q1.getResultList();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.remove(resultList.get(0));
        tx.commit();

    }

    public void updateName(Long productId, String name) {
        Product productById = getProductById(productId);
        productById.setName(name);
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.merge(productById);
        tx.commit();
    }

    public void updatePrice(Long productId, float price) {
        Product productById = getProductById(productId);
        productById.setPriceBeforeDiscount(price);
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.merge(productById);
        tx.commit();
    }

    public void updateQuantity(Long productId, int quantity) {
        Product productById = getProductById(productId);
        productById.setQuantity(quantity);
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.merge(quantity);
        tx.commit();
    }

    public void updateDescription(Long productId, String description) {
        Product productById = getProductById(productId);
        productById.setDescription(description);
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.merge(description);
        tx.commit();
    }

    public void updateDiscount(Long productId, float discount) {
        Product productById = getProductById(productId);
        productById.setDiscount(discount);
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.merge(discount);
        tx.commit();
    }


    private void removeAllPropertiesForOneProduct(Long productId) {
        Product productById = getProductById(productId);

        Query q1 = entityManager.createQuery("from Property p where p.product = ?1", Property.class);
        q1.setParameter(1, productById);
        List resultList = q1.getResultList();

        EntityTransaction tx = entityManager.getTransaction();

        tx.begin();
        resultList.stream().forEach(property -> entityManager.remove(property));
        tx.commit();
    }

    private boolean isProductCorrect(Product product) {
        return product != null && product.getDescription() != null && (product.getDiscount() >= 0 && product.getDiscount() <= 1) && product.getName() != null && (product.getPriceBeforeDiscount() > 0 && product.getPriceBeforeDiscount() < 10000) || product.getProductType() != null && (product.getQuantity() >= 0 && product.getQuantity() < 10000);
    }
}
