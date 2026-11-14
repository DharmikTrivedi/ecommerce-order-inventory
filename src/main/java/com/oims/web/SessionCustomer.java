package com.oims.web;

import jakarta.servlet.http.HttpSession;

/**
 * There is no login in this project. The header has a "Shopping as" dropdown and the
 * chosen customer id is kept in the session so cart / checkout know whose cart it is.
 */
public final class SessionCustomer {

    public static final String KEY = "customerId";

    private SessionCustomer() {
    }

    public static Integer get(HttpSession session) {
        return (Integer) session.getAttribute(KEY);
    }

    public static void set(HttpSession session, Integer customerId) {
        if (customerId == null) {
            session.removeAttribute(KEY);
        } else {
            session.setAttribute(KEY, customerId);
        }
    }
}
