package btl.kttkpm.quanlybanhangsieuthi.controller;

import btl.kttkpm.quanlybanhangsieuthi.dto.BillCartItem;
import btl.kttkpm.quanlybanhangsieuthi.dto.CartItemForm;
import btl.kttkpm.quanlybanhangsieuthi.dto.CheckoutForm;
import btl.kttkpm.quanlybanhangsieuthi.entity.Bill;
import btl.kttkpm.quanlybanhangsieuthi.entity.Customer;
import btl.kttkpm.quanlybanhangsieuthi.entity.Item;
import btl.kttkpm.quanlybanhangsieuthi.entity.Staff;
import btl.kttkpm.quanlybanhangsieuthi.entity.User;
import btl.kttkpm.quanlybanhangsieuthi.service.BillService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping("/bills/counter")
    public String counterPage(
            @RequestParam(required = false) String itemKeyword,
            @RequestParam(required = false) String customerKeyword,
            @RequestParam(required = false) Integer selectedCustomerId,
            @RequestParam(defaultValue = "0") int itemPage,
            @RequestParam(defaultValue = "0") int customerPage,
            HttpSession session,
            Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        CheckoutForm checkoutForm = new CheckoutForm();
        if (selectedCustomerId != null) {
            session.setAttribute("selectedCustomerId", selectedCustomerId);
        }
        Integer currentCustomerId = (Integer) session.getAttribute("selectedCustomerId");
        Customer selectedCustomer = billService.findCustomerById(currentCustomerId);
        if (selectedCustomer != null) {
            checkoutForm.setCustomerId(selectedCustomer.getId());
        }

        Page<Item> itemResult = billService.searchItems(itemKeyword, itemPage, 5);
        Page<Customer> customerResult = billService.searchCustomers(customerKeyword, customerPage, 5);
        model.addAttribute("items", itemResult.getContent());
        model.addAttribute("customers", customerResult.getContent());
        model.addAttribute("selectedCustomer", selectedCustomer);
        model.addAttribute("itemKeyword", itemKeyword);
        model.addAttribute("customerKeyword", customerKeyword);
        model.addAttribute("itemCurrentPage", itemResult.getNumber());
        model.addAttribute("itemTotalPages", itemResult.getTotalPages());
        model.addAttribute("customerCurrentPage", customerResult.getNumber());
        model.addAttribute("customerTotalPages", customerResult.getTotalPages());
        model.addAttribute("cartItemForm", new CartItemForm());
        model.addAttribute("checkoutForm", checkoutForm);
        model.addAttribute("cartItems", getCart(session));
        model.addAttribute("cartTotal", getCart(session).stream().map(BillCartItem::getLineAmount).reduce(0f, Float::sum));
        return "CounterPayment";
    }

    @PostMapping("/bills/cart/add")
    public String addToCart(
            @Valid @ModelAttribute("cartItemForm") CartItemForm form,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            return counterPage(null, null, null, 0, 0, session, model);
        }
        try {
            getCart(session).add(billService.createCartItem(form));
            return "redirect:/bills/counter";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return counterPage(null, null, null, 0, 0, session, model);
        }
    }

    @PostMapping("/bills/cart/clear")
    public String clearCart(HttpSession session) {
        getCart(session).clear();
        session.removeAttribute("selectedCustomerId");
        return "redirect:/bills/counter";
    }

    @PostMapping("/bills/checkout")
    public String checkout(
            @ModelAttribute CheckoutForm checkoutForm,
            HttpSession session,
            Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("currentUser");
        if (!(user instanceof Staff staff)) {
            model.addAttribute("error", "Tai khoan hien tai khong duoc phep lap hoa don");
            return counterPage(null, null, checkoutForm.getCustomerId(), 0, 0, session, model);
        }
        try {
            Bill bill = billService.checkout(staff, checkoutForm, getCart(session));
            getCart(session).clear();
            session.removeAttribute("selectedCustomerId");
            model.addAttribute("success", "Thanh toan thanh cong. Ma hoa don: " + bill.getId());
            return counterPage(null, null, null, 0, 0, session, model);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return counterPage(null, null, checkoutForm.getCustomerId(), 0, 0, session, model);
        }
    }

    @SuppressWarnings("unchecked")
    private List<BillCartItem> getCart(HttpSession session) {
        Object cart = session.getAttribute("billCart");
        if (cart == null) {
            List<BillCartItem> cartItems = new ArrayList<>();
            session.setAttribute("billCart", cartItems);
            return cartItems;
        }
        return (List<BillCartItem>) cart;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("currentUser") instanceof User;
    }
}
