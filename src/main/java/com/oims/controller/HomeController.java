package com.oims.controller;

import com.oims.service.ReportService;
import com.oims.web.SessionCustomer;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final ReportService reportService;

    public HomeController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("counts", reportService.dashboardCounts());
        model.addAttribute("recentOrders", reportService.recentOrders());
        model.addAttribute("movements", reportService.recentStockMovements());
        model.addAttribute("lowStock", reportService.lowStock());
        return "dashboard";
    }

    // "Shopping as" dropdown in the header
    @PostMapping("/switch-customer")
    public String switchCustomer(@RequestParam(required = false) Integer customerId, HttpSession session,
                                 @RequestHeader(value = "Referer", required = false) String referer) {
        SessionCustomer.set(session, customerId);
        return "redirect:" + (referer != null ? referer : "/shop");
    }
}
