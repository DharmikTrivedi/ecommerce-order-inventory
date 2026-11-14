package com.oims.web;

import com.oims.service.CartService;
import com.oims.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/** puts the data needed by the common header (customer switcher + cart badge) into every page */
@Component
public class HeaderInterceptor implements HandlerInterceptor {

    private final CustomerService customerService;
    private final CartService cartService;

    public HeaderInterceptor(CustomerService customerService, CartService cartService) {
        this.customerService = customerService;
        this.cartService = cartService;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView mav) {
        if (mav == null || mav.getViewName() == null || mav.getViewName().startsWith("redirect:")) {
            return;
        }
        Integer cid = SessionCustomer.get(request.getSession());
        mav.addObject("allCustomers", customerService.findAll());
        mav.addObject("currentCustomerId", cid);
        mav.addObject("cartCount", cartService.itemCount(cid));
    }
}
