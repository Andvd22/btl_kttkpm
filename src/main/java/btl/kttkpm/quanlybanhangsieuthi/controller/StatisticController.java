package btl.kttkpm.quanlybanhangsieuthi.controller;

import btl.kttkpm.quanlybanhangsieuthi.entity.IncomeStat;
import btl.kttkpm.quanlybanhangsieuthi.entity.User;
import btl.kttkpm.quanlybanhangsieuthi.service.StatisticService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StatisticController {

    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping("/statistics/income")
    public String incomeStatistic(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer quarter,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            HttpSession session,
            Model model) {
        if (!(session.getAttribute("currentUser") instanceof User)) {
            return "redirect:/login";
        }
        IncomeStat stat = null;
        try {
            if ("month".equalsIgnoreCase(type) && month != null && year != null) {
                stat = statisticService.statisticByMonth(month, year);
            } else if ("quarter".equalsIgnoreCase(type) && quarter != null && year != null) {
                stat = statisticService.statisticByQuarter(quarter, year);
            } else if ("year".equalsIgnoreCase(type) && year != null) {
                stat = statisticService.statisticByYear(year);
            } else if ("dateRange".equalsIgnoreCase(type)) {
                stat = statisticService.statisticByDateRange(fromDate, toDate);
            }
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
        }
        model.addAttribute("stat", stat);
        model.addAttribute("type", type);
        model.addAttribute("month", month);
        model.addAttribute("quarter", quarter);
        model.addAttribute("year", year);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        return "IncomeStatistic";
    }
}
