package com.database.services;

import com.database.models.Product;
import com.database.models.Property;
import com.database.models.enums.ProductType;
import com.database.services.exceptions.InvalidParameterProvidedException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import javax.persistence.*;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.fail;

public class ProductServiceTest {
    private ProductService productService = new ProductService();
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("shop-database");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();


    @Test
    public void method_addProductToDatabase_desc_productServiceShouldCreateNewProductEntityInDatabase() {
        Product product = new Product("test1", 0, 0, 1, "test", 1, ProductType.BAG);

        productService.addProductToDatabase(product);

        List<Product> products = getProductsFromDatabse(product);

        deleteInputedProductFromDatabase(product.getId());

        assertEquals(1, products.size());
    }

    @Test
    public void method_getProductById_desc_productServiceShouldGetNewProductEntityFromDatabase() {
        Product product = new Product("test2", 0, 0, 1, "test", 1, ProductType.BAG);

        productService.addProductToDatabase(product);
        Product productById = productService.getProductById(product.getId());

        deleteInputedProductFromDatabase(product.getId());

        assertEquals(product.getDescription(), productById.getDescription());
        assertEquals(product.getDiscount(), productById.getDiscount());
        assertEquals(product.getName(), productById.getName());
        assertEquals(product.getPriceBeforeDiscount(), productById.getPriceBeforeDiscount());
        assertEquals(product.getProductType(), productById.getProductType());
        assertEquals(product.getQuantity(), productById.getQuantity());
    }

    @Test
    public void method_removeProductFromDatabase_desc_productServiceShouldRemoveNewProductEntityFromDatabaseAndShouldRemoveAllConectedProperties() {
        Product product = new Product("test3", 0, 0, 1, "test", 1, ProductType.BAG);

        productService.addProductToDatabase(product);
        productService.addKeyValueProperty(product.getId(), "size1", "1");
        productService.addKeyValueProperty(product.getId(), "size2", "2");
        productService.addKeyValueProperty(product.getId(), "size3", "3");
        productService.removeProductFromDatabase(product.getId());

        List<Product> products = getProductsFromDatabse(product);

        Query q2 = entityManager.createQuery("from Property p where p.product = ?1", Property.class);
        q2.setParameter(1, product);

        List resultList2 = q2.getResultList();

        assertEquals(0, products.size());
        assertEquals(0, resultList2.size());
    }

    @Test
    public void method_buyProduct_desc_productServiceShouldBuyNewProductFromDatabse() {
        Product product = new Product("test4", 0, 0, 1, "test", 1, ProductType.BAG);

        productService.addProductToDatabase(product);
        productService.buyProduct(product.getId(), 1);

        Product productById = productService.getProductById(product.getId());

        deleteInputedProductFromDatabase(product.getId());

        assertEquals(0, productById.getQuantity());
    }

    @Test
    public void method_buyProduct_desc_productServiceShouldReturnInvalidParameterProvidedExceptionWhenQuantityIsLowerThan0() {
        try {
            productService.buyProduct(100L, -1);
            fail();
        } catch (InvalidParameterProvidedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void method_buyProduct_desc_productServiceShouldReturnInvalidParameterProvidedExceptionWhenQuantityEquals0() {
        try {
            productService.buyProduct(100L, 0);
            fail();
        } catch (InvalidParameterProvidedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void method_buyProduct_desc_productServiceShouldReturnInvalidParameterProvidedExceptionWhenQuantityParameterIsHigherThanQuantityOfProductInDatabase() {
        Product product = new Product("test5", 0, 0, 1, "test", 1, ProductType.BAG);

        productService.addProductToDatabase(product);
        try {
            productService.buyProduct(product.getId(), 2);
            fail();
        } catch (InvalidParameterProvidedException e) {
            e.printStackTrace();
        } finally {
            deleteInputedProductFromDatabase(product.getId());
        }
    }

    @Test
    public void method_addKeyValueProperty_desc_productServiceShouldAddPropertyToTable() {
        Product product = new Product("test6", 0, 0, 1, "test", 1, ProductType.BAG);

        productService.addProductToDatabase(product);
        productService.addKeyValueProperty(product.getId(), "size", "M");

        Query q = entityManager.createQuery("from Property p where p.property = ?1 AND p.result =?2", Property.class);
        q.setParameter(1, "size");
        q.setParameter(2, "M");

        List resultList = q.getResultList();
        EntityTransaction tx = entityManager.getTransaction();

        tx.begin();
        resultList.stream().forEach(property -> entityManager.remove(property));
        tx.commit();

        deleteInputedProductFromDatabase(product.getId());

        assertEquals(1, resultList.size());
    }

    @Test
    public void method_addKeyValueProperty_desc_productServiceShouldReturnInvalidParameterProvidedExceptionWhenKeyIsNull() {
        try {
            productService.addKeyValueProperty(100L, null, "M");
            fail();
        } catch (InvalidParameterProvidedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void method_addKeyValueProperty_desc_productServiceShouldReturnInvalidParameterProvidedExceptionWhenKeyIsEmptyString() {
        try {
            productService.addKeyValueProperty(100L, "", "M");
            fail();
        } catch (InvalidParameterProvidedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void method_addKeyValueProperty_desc_productServiceShouldReturnInvalidParameterProvidedExceptionWhenValueIsNull() {
        try {
            productService.addKeyValueProperty(100L, "size", null);
            fail();
        } catch (InvalidParameterProvidedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void method_addKeyValueProperty_desc_productServiceShouldReturnInvalidParameterProvidedExceptionWhenValueIsEmptyString() {
        try {
            productService.addKeyValueProperty(100L, "size", "");
            fail();
        } catch (InvalidParameterProvidedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void method_removeKeyValueProperty_desc_productServiceShouldOnePairOfKeyValueInPropertyTable() {
        Product product = new Product("test7", 0, 0, 1, "test", 1, ProductType.BAG);

        productService.addProductToDatabase(product);
        productService.addKeyValueProperty(product.getId(), "size1", "1");
        productService.removeKeyValueProperty(product.getId(), "size1", "1");

        Query q = entityManager.createQuery("from Property p where p.property = ?1 AND p.result =?2", Property.class);
        q.setParameter(1, "size1");
        q.setParameter(2, "1");

        List resultList = q.getResultList();

        deleteInputedProductFromDatabase(product.getId());

        assertEquals(0, resultList.size());
    }

    @Test
    public void method_removeKeyValueProperty_desc_productServiceShouldReturnInvalidParameterProvidedExceptionForNullKey() {
        try {
            productService.removeKeyValueProperty(100L, null, "1");
            fail();
        } catch (InvalidParameterProvidedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void method_removeKeyValueProperty_desc_productServiceShouldReturnInvalidParameterProvidedExceptionForEmptyStringKey() {
        try {
            productService.removeKeyValueProperty(100L, "", "1");
            fail();
        } catch (InvalidParameterProvidedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void method_removeKeyValueProperty_desc_productServiceShouldReturnInvalidParameterProvidedExceptionForNullValue() {
        try {
            productService.removeKeyValueProperty(100L, "size1", null);
            fail();
        } catch (InvalidParameterProvidedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void method_removeKeyValueProperty_desc_productServiceShouldReturnInvalidParameterProvidedExceptionForEmptyStringValue() {
        try {
            productService.removeKeyValueProperty(100L, "size2", "");
            fail();
        } catch (InvalidParameterProvidedException e) {
            e.printStackTrace();
        }
    }

    private List<Product> getProductsFromDatabse(Product product){
        Query query = entityManager
                .createQuery(
                        "from Product p where p.description = ?1 AND p.discount = ?2 AND p.name = ?3 AND p.priceBeforeDiscount = ?4 AND p.productType = ?5 AND p.quantity = ?6 AND p.priceAfterDiscount = ?7", Product.class);
        query.setParameter(1, product.getDescription());
        query.setParameter(2, product.getDiscount());
        query.setParameter(3, product.getName());
        query.setParameter(4, product.getPriceBeforeDiscount());
        query.setParameter(5, product.getProductType());
        query.setParameter(6, product.getQuantity());
        query.setParameter(7, product.getPriceAfterDiscount());

        return query.getResultList();
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
        productService.closeSession();
    }
}