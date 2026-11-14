package com.oims.controller;

import com.oims.TestData;
import com.oims.model.*;
import com.oims.repository.*;
import com.oims.web.SessionCustomer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Sends real HTTP requests through the controllers (MockMvc) against the H2 test database.
 * The printed request/response of each test is used as evidence in the report.
 */
@SpringBootTest
@AutoConfigureMockMvc
class EndpointTest {

    @Autowired MockMvc mvc;
    @Autowired CategoryRepository categoryRepo;
    @Autowired SellerRepository sellerRepo;
    @Autowired ProductRepository productRepo;
    @Autowired CustomerRepository customerRepo;
    @Autowired CartItemRepository cartRepo;
    @Autowired OrderRepository orderRepo;
    @Autowired StockMovementRepository movementRepo;
    @Autowired ReturnRequestRepository returnRepo;

    Product mouse, laptopStand;
    Customer customer;
    MockHttpSession session;

    @BeforeEach
    void setUp() {
        returnRepo.deleteAll();
        movementRepo.deleteAll();
        orderRepo.deleteAll();
        cartRepo.deleteAll();
        customerRepo.deleteAll();
        productRepo.deleteAll();
        sellerRepo.deleteAll();
        categoryRepo.deleteAll();

        Category el = categoryRepo.save(TestData.category("Electronics"));
        Seller s = sellerRepo.save(TestData.seller("Krishna Traders", "k@test.com"));
        mouse = productRepo.save(TestData.product("EL-1001", "Wireless Mouse", "599.00", 45, 10, el, s));
        laptopStand = productRepo.save(TestData.product("EL-1006", "Laptop Stand", "1299.00", 3, 5, el, s));
        customer = customerRepo.save(TestData.customer("Rohan", "rohan@test.com"));

        session = new MockHttpSession();
        session.setAttribute(SessionCustomer.KEY, customer.getId());
    }

    @Test
    void stockLookup_returnsJson() throws Exception {
        mvc.perform(get("/api/products/" + laptopStand.getId() + "/stock"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop Stand"))
                .andExpect(jsonPath("$.stock").value(3))
                .andExpect(jsonPath("$.lowStock").value(true));
    }

    @Test
    void addToCart_ajax_returnsNewCartCount() throws Exception {
        mvc.perform(post("/api/cart/add").session(session)
                        .param("productId", mouse.getId().toString()).param("qty", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.cartCount").value(2));
    }

    @Test
    void addToCart_moreThanStock_returns400WithMessage() throws Exception {
        mvc.perform(post("/api/cart/add").session(session)
                        .param("productId", laptopStand.getId().toString()).param("qty", "4"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ok").value(false))
                .andExpect(jsonPath("$.message").value("Only 3 unit(s) of Laptop Stand in stock"));
    }

    @Test
    void addToCart_withoutCustomer_returns400() throws Exception {
        mvc.perform(post("/api/cart/add").param("productId", mouse.getId().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Choose a customer from 'Shopping as' first"));
    }

    @Test
    void missingProduct_showsFriendlyErrorPage() throws Exception {
        mvc.perform(get("/products/99999/edit"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("message", "Product with id 99999 was not found"));
    }

    @Test
    void invalidCustomerForm_isRejectedOnServer() throws Exception {
        mvc.perform(post("/customers/save")
                        .param("firstName", "")
                        .param("lastName", "Shah")
                        .param("email", "not-an-email")
                        .param("phone", "12345")
                        .param("address", "31 Athwa Lines")
                        .param("city", "Surat")
                        .param("pincode", "39500"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("customers/form"))
                .andExpect(model().attributeHasFieldErrors("customer", "firstName", "email", "phone", "pincode"));
        assertThat(customerRepo.count()).isEqualTo(1);
    }

    @Test
    void checkout_placesOrderAndReducesStock() throws Exception {
        cartRepo.save(new CartItem(customer, mouse, 2));

        mvc.perform(post("/checkout").session(session)
                        .param("paymentMode", "UPI")
                        .param("shippingAddress", "8 FC Road, Pune 411004"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("success"));

        assertThat(productRepo.findById(mouse.getId()).get().getStockQty()).isEqualTo(43);
        assertThat(cartRepo.countItemsInCart(customer.getId())).isZero();
        assertThat(orderRepo.count()).isEqualTo(1);
    }

    @Test
    void checkout_rollsBackWholeOrderWhenOneItemIsShort() throws Exception {
        cartRepo.save(new CartItem(customer, mouse, 2));         // ok
        cartRepo.save(new CartItem(customer, laptopStand, 3));   // ok right now
        laptopStand.setStockQty(1);                              // someone else bought 2 meanwhile
        productRepo.save(laptopStand);

        mvc.perform(post("/checkout").session(session)
                        .param("paymentMode", "COD")
                        .param("shippingAddress", "8 FC Road, Pune 411004"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"))
                .andExpect(flash().attribute("error",
                        "Order not placed: Not enough stock for Laptop Stand: requested 3, available 1"));

        // mouse stock must be untouched because the whole transaction rolled back
        assertThat(productRepo.findById(mouse.getId()).get().getStockQty()).isEqualTo(45);
        assertThat(orderRepo.count()).isZero();
        assertThat(movementRepo.count()).isZero();
        assertThat(cartRepo.countItemsInCart(customer.getId())).isEqualTo(5);
    }
}
