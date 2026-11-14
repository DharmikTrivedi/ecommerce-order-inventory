package com.oims.controller;

import com.oims.dto.MonthlySales;
import com.oims.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales")
    public String monthlySales(Model model) {
        List<MonthlySales> rows = reportService.monthlySales();
        BigDecimal max = BigDecimal.ONE;
        BigDecimal total = BigDecimal.ZERO;
        long orders = 0;
        for (MonthlySales m : rows) {
            if (m.revenue().compareTo(max) > 0) max = m.revenue();
            total = total.add(m.revenue());
            orders += m.orderCount();
        }
        model.addAttribute("rows", rows);
        model.addAttribute("maxRevenue", max);
        model.addAttribute("grandTotal", total);
        model.addAttribute("totalOrders", orders);
        return "reports/sales";
    }

    @GetMapping("/top-products")
    public String topProducts(@RequestParam(defaultValue = "10") int n, Model model) {
        if (n < 1 || n > 50) n = 10;
        model.addAttribute("rows", reportService.topSelling(n));
        model.addAttribute("n", n);
        return "reports/top-products";
    }

    @GetMapping("/low-stock")
    public String lowStock(Model model) {
        model.addAttribute("rows", reportService.lowStock());
        return "reports/low-stock";
    }
}
