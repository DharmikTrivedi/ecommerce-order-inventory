package com.oims.controller;

import com.oims.exception.BusinessException;
import com.oims.model.CartItem;
import com.oims.model.Customer;
import com.oims.model.Order;
import com.oims.model.PaymentMode;
import com.oims.service.*;
import com.oims.web.PageUtil;
import com.oims.web.SessionCustomer;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

/** customer side: catalog, cart and checkout */
@Controller
public class ShopController {

    private static final Set<String> SORT_FIELDS = Set.of("name", "price");

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CartService cartService;
    private final OrderService orderService;
    private final CustomerService customerService;

    public ShopController(ProductService productService, CategoryService categoryService, CartService cartService,
                          OrderService orderService, CustomerService customerService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.customerService = customerService;
    }

    @GetMapping("/shop")
    public String catalog(@RequestParam(required = false) String q,
                          @RequestParam(required = false) Integer category,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "name") String sort,
                          @RequestParam(defaultValue = "asc") String dir,
                          HttpSession session, Model model) {
        model.addAttribute("page", productService.search(q, category, true,
                PageUtil.build(page, 12, sort, dir, SORT_FIELDS, "name")));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("q", q);
        model.addAttribute("category", category);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        Integer cid = SessionCustomer.get(session);
        if (cid != null) {
            model.addAttribute("wishlist", customerService.get(cid).getWishlist());
        }
        return "shop/catalog";
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model, RedirectAttributes ra) {
        Integer cid = SessionCustomer.get(session);
        if (cid == null) {
            ra.addFlashAttribute("info", "Select a customer from 'Shopping as' to view the cart");
            return "redirect:/shop";
        }
        List<CartItem> items = cartService.getCart(cid);
        model.addAttribute("items", items);
        model.addAttribute("total", cartService.total(items));
        model.addAttribute("maxQty", CartService.MAX_QTY_PER_ITEM);
        return "shop/cart";
    }

    @PostMapping("/cart/{itemId}/remove")
    public String removeFromCart(@PathVariable Integer itemId, HttpSession session, RedirectAttributes ra) {
        Integer cid = SessionCustomer.get(session);
        if (cid != null) {
            cartService.remove(cid, itemId);
            ra.addFlashAttribute("success", "Item removed from cart");
        }
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkoutForm(HttpSession session, Model model, RedirectAttributes ra) {
        Integer cid = SessionCustomer.get(session);
        if (cid == null) {
            return "redirect:/shop";
        }
        List<CartItem> items = cartService.getCart(cid);
        if (items.isEmpty()) {
            ra.addFlashAttribute("error", "Your cart is empty");
            return "redirect:/cart";
        }
        Customer c = customerService.get(cid);
        model.addAttribute("customer", c);
        model.addAttribute("items", items);
        model.addAttribute("total", cartService.total(items));
        model.addAttribute("defaultAddress", c.getAddress() + ", " + c.getCity() + " " + c.getPincode());
        model.addAttribute("paymentModes", PaymentMode.values());
        return "shop/checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam(required = false) PaymentMode paymentMode,
                             @RequestParam(defaultValue = "") String shippingAddress,
                             HttpSession session, RedirectAttributes ra) {
        Integer cid = SessionCustomer.get(session);
        if (cid == null) {
            return "redirect:/shop";
        }
        // server side checks (same rules as the JS on the page)
        if (paymentMode == null) {
            ra.addFlashAttribute("error", "Please choose a payment mode");
            return "redirect:/checkout";
        }
        if (shippingAddress.trim().length() < 10) {
            ra.addFlashAttribute("error", "Shipping address looks too short");
            return "redirect:/checkout";
        }
        try {
            Order order = orderService.placeOrder(cid, paymentMode, shippingAddress);
            ra.addFlashAttribute("success", "Order " + order.getOrderNumber() + " placed successfully");
            return "redirect:/orders/" + order.getId();
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", "Order not placed: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/my-orders")
    public String myOrders(HttpSession session, Model model, RedirectAttributes ra) {
        Integer cid = SessionCustomer.get(session);
        if (cid == null) {
            ra.addFlashAttribute("info", "Select a customer from 'Shopping as' to see their orders");
            return "redirect:/shop";
        }
        return "redirect:/customers/" + cid;
    }
}
