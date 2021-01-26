package com.database.services;

import com.database.models.Address;
import com.database.models.Customer;
import com.database.services.exceptions.InvalidParameterProvidedException;
import com.database.services.sessionManager.EntityManagerService;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;


public class CustomerService extends EntityManagerService {

    public void addCustomerToDatabase(Customer customer) {
        if (customer == null) {
            throw new InvalidParameterProvidedException("customer shouldn't be null");
        }
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(customer);
        customer.getAddress().getCustomers().add(customer);
        tx.commit();
    }

    public Customer getCustomerById(Long customerId) {
        if (customerId == null || customerId <= 0) {
            throw new InvalidParameterProvidedException("customerId shouldn't be null or lower than 0");
        }
        return entityManager.find(Customer.class, customerId);
    }

    public void removeCustomerFromDatabase(Long customerId) {
        Customer customerById = getCustomerById(customerId);
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.remove(customerById);
        tx.commit();
    }

    public void updateEmail(Long customerId, String email) {
        if (email == null || email.equals("") || !email.contains("@")) {
            throw new InvalidParameterProvidedException("email shouldn't be null, empty or should contain @ character as well");
        }
        Customer customerById = getCustomerById(customerId);
        customerById.setEmail(email);
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.merge(customerById);
        tx.commit();
    }

    public void updatePassword(Long customerId, String password) {
        if (password == null || password.equals("")) {
            throw new InvalidParameterProvidedException("password shouldn't be null or empty as well");
        }
        Customer customerById = getCustomerById(customerId);
        customerById.setPassword(password);
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.merge(customerById);
        tx.commit();
    }

    public void updateIsAdminStatus(Long customerId, boolean isAdmin) {
        Customer customerById = getCustomerById(customerId);
        customerById.setAdmin(isAdmin);
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.merge(customerById);
        tx.commit();
    }

    public void updateAddress(Long customerId, Address address) {
        if (address == null || address.getCountry() == null || address.getCity() == null || address.getZipCode() == null || address.getStreet() == null || address.getHomeNumber() == null) {
            throw new InvalidParameterProvidedException("address shouldn't be null and address parameters shouldn't be null");
        }

        Customer customerById = getCustomerById(customerId);
        EntityTransaction tx = entityManager.getTransaction();
        Query q = entityManager
                .createQuery(
                        "from Address a where a.city = ?1 AND a.country = ?2 AND a.home_number = ?3 AND a.street = ?4 AND a.zipCode = ?5", Address.class);
        q.setParameter(1, address.getCity());
        q.setParameter(2, address.getCountry());
        q.setParameter(3, address.getHomeNumber());
        q.setParameter(4, address.getStreet());
        q.setParameter(5, address.getZipCode());

        List<Address> resultList = q.getResultList();

        tx.begin();
        if (resultList.size() == 1) {
            customerById.setAddress(resultList.get(0));

        } else {
            entityManager.persist(address);
            customerById.setAddress(address);
        }
        tx.commit();
    }
}
