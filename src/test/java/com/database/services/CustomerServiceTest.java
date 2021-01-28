package com.database.services;

import com.database.models.Address;
import com.database.models.Customer;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

public class CustomerServiceTest {
    private CustomerService customerService = new CustomerService();
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("shop-database");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();

    @Test
    public void method_addCustomerToDatabase_desc_CustomerServiceShouldAddCustomerToDatabaseAndShouldAddAddress() {
        Address address = new Address("Poland", "30-091", "Cracow", "street", 1);

        Customer customer = new Customer("John", "Smith", "xyz@test.com", new Date(11111999L), "password", false, address);

        customerService.addCustomerToDatabase(customer);

        List<Customer> customers = findCustomers(customer);
        List<Address> addresses = findAddresses(address);

        deleteAddressFromDatabase(customer.getAddress().getId());

        assertEquals(1, addresses.size());
        assertEquals(1, customers.size());
        assertEquals(1, address.getCustomers().size());
    }

    @Test
    public void method_addCustomerToDatabase_desc_CustomerServiceShouldAddCustomerWithExistingAddressToDatabaseAndShouldntDoubleThisAddress() {
        Address address = new Address("Poland", "30-091", "Cracow", "street", 2);

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(address);
        tx.commit();

        Customer customer = new Customer("Eric", "Smith", "xyz@test.com", new Date(11111999L), "password", false, address);
        customerService.addCustomerToDatabase(customer);

        List<Customer> customers = findCustomers(customer);
        List<Address> addresses = findAddresses(address);

        deleteCustomerFromDatabase(customers.get(0).getId());
        deleteAddressFromDatabase(addresses.get(0).getId());             //TODO test works but not clean after exec

        assertEquals(1, customers.size());
        assertEquals(1, addresses.size());
        assertEquals(1, address.getCustomers().size());
    }

    @Test
    public void method_removeCustomerFromDatabase_CustomerServiceShouldRemoveCustomerFromDatabaseAndUpdateAddressSetOfCustomers(){
        Address address = new Address("Poland", "30-091", "Cracow", "street", 3);

        Customer customer = new Customer("Eric", "Smith", "xyz@test.com", new Date(11111999L), "password", false, address);
        customerService.addCustomerToDatabase(customer);

        customerService.removeCustomerFromDatabase(customer.getId());

        assertNull(entityManager.find(Customer.class,customer.getId()));
        assertEquals(0,address.getCustomers().size());
    }

    private List<Customer> findCustomers(Customer customer) {
        Query query = entityManager
                .createQuery(
                        "from Customer c where c.dateOfBirth = ?1 AND c.email = ?2 AND c.isAdmin = ?3 AND c.name = ?4 AND c.password = ?5 AND c.surname = ?6 AND c.address = ?7", Customer.class);
        query.setParameter(1, customer.getDateOfBirth());
        query.setParameter(2, customer.getEmail());
        query.setParameter(3, customer.isAdmin());
        query.setParameter(4, customer.getName());
        query.setParameter(5, customer.getPassword());
        query.setParameter(6, customer.getSurname());
        query.setParameter(7, customer.getAddress());

        return query.getResultList();
    }

    private List<Address> findAddresses(Address address) {
        Query query = entityManager
                .createQuery(
                        "from Address a where a.city = ?1 AND a.country = ?2 AND a.homeNumber = ?3 AND a.street = ?4 AND a.zipCode = ?5", Address.class);
        query.setParameter(1, address.getCity());
        query.setParameter(2, address.getCountry());
        query.setParameter(3, address.getHomeNumber());
        query.setParameter(4, address.getStreet());
        query.setParameter(5, address.getZipCode());

        return query.getResultList();
    }

    private void deleteAddressFromDatabase(Long addressId) {
        EntityTransaction tx = entityManager.getTransaction();
        Address address = entityManager.find(Address.class, addressId);
        tx.begin();
        entityManager.remove(address);
        tx.commit();
    }

    private void deleteCustomerFromDatabase(Long customerId) {
        EntityTransaction tx = entityManager.getTransaction();
        Customer customer = entityManager.find(Customer.class, customerId);
        tx.begin();
        entityManager.remove(customer);
        tx.commit();
    }

    @AfterSuite
    private void closeConnection() {
        customerService.closeSession();
        entityManager.close();
        entityManagerFactory.close();
    }
}
