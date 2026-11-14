package com.oims.service;

import com.oims.TestData;
import com.oims.exception.BusinessException;
import com.oims.model.CartItem;
import com.oims.model.Customer;
import com.oims.model.Product;
import com.oims.repository.CartItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** business rule: 1 to 10 units per product, never more than stock, only active products */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock CartItemRepository cartRepo;
    @Mock ProductService productService;
    @Mock CustomerService customerService;
    @InjectMocks CartService cartService;

    Product mouse;
    Customer aarav;

    @BeforeEach
    void setUp() {
        mouse = TestData.product("EL-1001", "Wireless Mouse", "599.00", 4, 2, null, null);
        mouse.setId(1);
        aarav = TestData.customer("Aarav", "a@test.com");
        aarav.setId(7);
    }

    @Test
    void addToCart_withinStock_createsLine() {
        when(productService.get(1)).thenReturn(mouse);
        when(cartRepo.findByCustomerIdAndProductId(7, 1)).thenReturn(Optional.empty());
        when(customerService.get(7)).thenReturn(aarav);
        when(cartRepo.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));

        CartItem line = cartService.addToCart(7, 1, 3);

        assertThat(line.getQuantity()).isEqualTo(3);
        assertThat(line.getSubtotal()).isEqualByComparingTo("1797.00");
    }

    @Test
    void addToCart_moreThanStock_isRejected() {
        when(productService.get(1)).thenReturn(mouse);
        when(cartRepo.findByCustomerIdAndProductId(7, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addToCart(7, 1, 5))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Only 4 unit(s)");
        verify(cartRepo, never()).save(any());
    }

    @Test
    void addToCart_existingLineGoesOverStock_isRejected() {
        CartItem existing = new CartItem(aarav, mouse, 3);
        when(productService.get(1)).thenReturn(mouse);
        when(cartRepo.findByCustomerIdAndProductId(7, 1)).thenReturn(Optional.of(existing));

        // 3 already in cart + 2 more = 5 > 4 in stock
        assertThatThrownBy(() -> cartService.addToCart(7, 1, 2))
                .isInstanceOf(BusinessException.class);
        assertThat(existing.getQuantity()).isEqualTo(3);
    }

    @Test
    void quantityAboveTen_isRejectedEvenIfInStock() {
        mouse.setStockQty(100);
        assertThatThrownBy(() -> cartService.validateQuantity(mouse, 11))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("at most 10");
    }

    @Test
    void inactiveProduct_cannotBeAdded() {
        mouse.setActive(false);
        when(productService.get(1)).thenReturn(mouse);
        doThrow(new BusinessException("Wireless Mouse is no longer available"))
                .when(productService).checkSellable(mouse);

        assertThatThrownBy(() -> cartService.addToCart(7, 1, 1))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no longer available");
    }
}
