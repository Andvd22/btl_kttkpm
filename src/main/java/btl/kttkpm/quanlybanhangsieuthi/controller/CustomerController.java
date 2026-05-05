package btl.kttkpm.quanlybanhangsieuthi.controller;

import btl.kttkpm.quanlybanhangsieuthi.dto.CustomerForm;
import btl.kttkpm.quanlybanhangsieuthi.entity.Customer;
import btl.kttkpm.quanlybanhangsieuthi.entity.User;
import btl.kttkpm.quanlybanhangsieuthi.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session,
            Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        Page<Customer> customerPage = customerService.search(keyword, page, size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("customers", customerPage.getContent());
        model.addAttribute("currentPage", customerPage.getNumber());
        model.addAttribute("totalPages", customerPage.getTotalPages());
        model.addAttribute("size", size);
        return "CustomerList";
    }

    @GetMapping("/customers/new")
    public String createForm(HttpSession session, Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        model.addAttribute("customerForm", new CustomerForm());
        model.addAttribute("action", "/customers/new");
        return "CustomerForm";
    }

    @PostMapping("/customers/new")
    public String create(
            @Valid @ModelAttribute("customerForm") CustomerForm customerForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("action", "/customers/new");
            return "CustomerForm";
        }
        try {
            customerService.create(customerForm);
            return "redirect:/customers";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("action", "/customers/new");
            return "CustomerForm";
        }
    }

    @GetMapping("/customers/{id}/edit")
    public String editForm(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        model.addAttribute("customerForm", customerService.toForm(customerService.getById(id)));
        model.addAttribute("action", "/customers/" + id + "/edit");
        return "CustomerForm";
    }

    @PostMapping("/customers/{id}/edit")
    public String update(
            @PathVariable Integer id,
            @Valid @ModelAttribute("customerForm") CustomerForm customerForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("action", "/customers/" + id + "/edit");
            return "CustomerForm";
        }
        try {
            customerService.update(id, customerForm);
            return "redirect:/customers";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("action", "/customers/" + id + "/edit");
            return "CustomerForm";
        }
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("currentUser") instanceof User;
    }
}
