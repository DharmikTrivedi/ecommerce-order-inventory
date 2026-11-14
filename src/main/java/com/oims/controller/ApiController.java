package com.oims.controller;

import com.oims.exception.BusinessException;
import com.oims.exception.NotFoundException;
import com.oims.model.CartItem;
import com.oims.model.Customer;
import com.oims.model.Product;
import com.oims.service.CartService;
import com.oims.service.CustomerService;
import com.oims.service.ProductService;
import com.oims.web.SessionCustomer;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** JSON endpoints called with jQuery $.ajax from the pages */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final CartService cartService;
    private final ProductService productService;
    private final CustomerService customerService;

    public ApiController(CartService cartService, ProductService productService, CustomerService customerService) {
        this.cartService = cartService;
        this.productService = productService;
        this.customerService = customerService;
    }

    @PostMapping("/cart/add")
    public Map<String, Object> addToCart(@RequestParam Integer productId,
                                         @RequestParam(defaultValue = "1") int qty,
                                         HttpSession session) {
        Integer cid = requireCustomer(session);
        CartItem line = cartService.addToCart(cid, productId, qty);
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("ok", true);
        res.put("message", line.getProduct().getName() + " added to cart (qty " + line.getQuantity() + ")");
        res.put("cartCount", cartService.itemCount(cid));
        return res;
    }

    @PostMapping("/cart/{itemId}/qty")
    public Map<String, Object> changeQty(@PathVariable Integer itemId, @RequestParam int qty, HttpSession session) {
        Integer cid = requireCustomer(session);
        CartItem line = cartService.updateQuantity(cid, itemId, qty);
        List<CartItem> cart = cartService.getCart(cid);
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("ok", true);
        res.put("subtotal", line.getSubtotal());
        res.put("total", cartService.total(cart));
        res.put("cartCount", cartService.itemCount(cid));
        return res;
    }

    @GetMapping("/products/{id}/stock")
    public Map<String, Object> stock(@PathVariable Integer id) {
        Product p = productService.get(id);
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("productId", p.getId());
        res.put("name", p.getName());
        res.put("stock", p.getStockQty());
        res.put("active", p.isActive());
        res.put("lowStock", p.isLowStock());
        return res;
    }

    // live search box on the shop page
    @GetMapping("/products/search")
    public List<Map<String, Object>> search(@RequestParam String q) {
        if (q.trim().length() < 2) {
            return List.of();
        }
        return productService.quickSearch(q).stream().map(p -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", p.getId());
            m.put("name", p.getName());
            m.put("sku", p.getSku());
            m.put("price", p.getPrice());
            m.put("stock", p.getStockQty());
            return m;
        }).toList();
    }

    @PostMapping("/wishlist/{productId}")
    public Map<String, Object> toggleWishlist(@PathVariable Integer productId, HttpSession session) {
        Integer cid = requireCustomer(session);
        boolean added = customerService.toggleWishlist(cid, productId);
        return Map.of("ok", true, "inWishlist", added);
    }

    // used by the customer form to warn about duplicate email before submitting
    @GetMapping("/customers/email-available")
    public Map<String, Object> emailAvailable(@RequestParam String email, @RequestParam(required = false) Integer id) {
        Customer probe = new Customer();
        probe.setEmail(email.trim());
        probe.setId(id);
        return Map.of("available", !customerService.isEmailTaken(probe));
    }

    private Integer requireCustomer(HttpSession session) {
        Integer cid = SessionCustomer.get(session);
        if (cid == null) {
            throw new BusinessException("Choose a customer from 'Shopping as' first");
        }
        return cid;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> onBusinessError(BusinessException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("ok", false, "message", e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> onNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("ok", false, "message", e.getMessage()));
    }
}
