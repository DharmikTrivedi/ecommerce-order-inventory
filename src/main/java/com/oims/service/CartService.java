package com.oims.service;

import com.oims.exception.BusinessException;
import com.oims.exception.NotFoundException;
import com.oims.model.CartItem;
import com.oims.model.Customer;
import com.oims.model.Product;
import com.oims.repository.CartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    public static final int MAX_QTY_PER_ITEM = 10;

    private final CartItemRepository cartRepo;
    private final ProductService productService;
    private final CustomerService customerService;

    public CartService(CartItemRepository cartRepo, ProductService productService, CustomerService customerService) {
        this.cartRepo = cartRepo;
        this.productService = productService;
        this.customerService = customerService;
    }

    public List<CartItem> getCart(Integer customerId) {
        return cartRepo.findByCustomerIdOrderByAddedAtAsc(customerId);
    }

    public int itemCount(Integer customerId) {
        return customerId == null ? 0 : cartRepo.countItemsInCart(customerId);
    }

    public BigDecimal total(List<CartItem> items) {
        BigDecimal sum = BigDecimal.ZERO;
        for (CartItem item : items) {
            sum = sum.add(item.getSubtotal());
        }
        return sum;
    }

    /** adds qty of a product to the cart (or increases the existing line) */
    @Transactional
    public CartItem addToCart(Integer customerId, Integer productId, int qty) {
        if (qty < 1) {
            throw new BusinessException("Quantity must be at least 1");
        }
        Product product = productService.get(productId);
        productService.checkSellable(product);

        CartItem line = cartRepo.findByCustomerIdAndProductId(customerId, productId).orElse(null);
        int newQty = (line == null ? 0 : line.getQuantity()) + qty;
        validateQuantity(product, newQty);

        if (line == null) {
            Customer customer = customerService.get(customerId);
            line = new CartItem(customer, product, newQty);
        } else {
            line.setQuantity(newQty);
        }
        return cartRepo.save(line);
    }

    @Transactional
    public CartItem updateQuantity(Integer customerId, Integer cartItemId, int qty) {
        CartItem line = getOwnLine(customerId, cartItemId);
        validateQuantity(line.getProduct(), qty);
        line.setQuantity(qty);
        return line;
    }

    @Transactional
    public void remove(Integer customerId, Integer cartItemId) {
        cartRepo.delete(getOwnLine(customerId, cartItemId));
    }

    // BR: 1..10 units per product, and never more than what is in stock
    void validateQuantity(Product product, int qty) {
        if (qty < 1) {
            throw new BusinessException("Quantity must be at least 1");
        }
        if (qty > MAX_QTY_PER_ITEM) {
            throw new BusinessException("You can buy at most " + MAX_QTY_PER_ITEM + " units of " + product.getName());
        }
        if (qty > product.getStockQty()) {
            throw new BusinessException("Only " + product.getStockQty() + " unit(s) of " + product.getName() + " in stock");
        }
    }

    private CartItem getOwnLine(Integer customerId, Integer cartItemId) {
        CartItem line = cartRepo.findById(cartItemId).orElseThrow(() -> new NotFoundException("Cart item", cartItemId));
        if (!line.getCustomer().getId().equals(customerId)) {
            throw new NotFoundException("Cart item", cartItemId);
        }
        return line;
    }
}
