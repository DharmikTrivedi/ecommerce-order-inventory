package com.oims;

import com.oims.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/** small helpers to build entities for tests */
public final class TestData {

    private TestData() {
    }

    public static Category category(String name) {
        Category c = new Category();
        c.setName(name);
        return c;
    }

    public static Seller seller(String name, String email) {
        Seller s = new Seller();
        s.setName(name);
        s.setEmail(email);
        s.setPhone("9800000000");
        s.setJoinedOn(LocalDate.of(2025, 1, 1));
        return s;
    }

    public static Customer customer(String first, String email) {
        Customer c = new Customer();
        c.setFirstName(first);
        c.setLastName("Test");
        c.setEmail(email);
        c.setPhone("9811111111");
        c.setAddress("1 Test Street");
        c.setCity("Pune");
        c.setPincode("411001");
        c.setRegisteredOn(LocalDate.of(2025, 1, 1));
        return c;
    }

    public static Product product(String sku, String name, String price, int stock, int reorder,
                                  Category cat, Seller seller) {
        Product p = new Product();
        p.setSku(sku);
        p.setName(name);
        p.setPrice(new BigDecimal(price));
        p.setStockQty(stock);
        p.setReorderLevel(reorder);
        p.setActive(true);
        p.setCategory(cat);
        p.setSeller(seller);
        p.setCreatedOn(LocalDate.of(2025, 1, 1));
        return p;
    }
}
