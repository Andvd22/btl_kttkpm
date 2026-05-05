package btl.kttkpm.quanlybanhangsieuthi.service;

import btl.kttkpm.quanlybanhangsieuthi.entity.Bill;
import btl.kttkpm.quanlybanhangsieuthi.entity.IncomeStat;
import btl.kttkpm.quanlybanhangsieuthi.repository.BillRepository;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {

    private final BillRepository billRepository;

    public StatisticService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public IncomeStat statisticByMonth(int month, int year) {
        LocalDate start = LocalDate.of(year, Month.of(month), 1);
        return build("MONTH-" + month + "-" + year, start, start.withDayOfMonth(start.lengthOfMonth()));
    }

    public IncomeStat statisticByQuarter(int quarter, int year) {
        int startMonth = (quarter - 1) * 3 + 1;
        LocalDate start = LocalDate.of(year, startMonth, 1);
        LocalDate end = start.plusMonths(2).withDayOfMonth(start.plusMonths(2).lengthOfMonth());
        return build("QUARTER-" + quarter + "-" + year, start, end);
    }

    public IncomeStat statisticByYear(int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        return build("YEAR-" + year, start, end);
    }

    public IncomeStat statisticByDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("Vui long nhap day du ngay bat dau va ngay ket thuc");
        }
        if (toDate.isBefore(fromDate)) {
            throw new IllegalArgumentException("Ngay ket thuc phai lon hon hoac bang ngay bat dau");
        }
        return build("DATE-" + fromDate + "-" + toDate, fromDate, toDate);
    }

    private IncomeStat build(String periodType, LocalDate start, LocalDate end) {
        List<Bill> bills = billRepository.findByPaymentTimeBetweenAndStatus(start, end, 1);
        float totalRevenue = 0f;
        int totalBills = bills.size();
        int totalSoldQuantity = 0;
        for (Bill bill : bills) {
            totalRevenue += bill.getPaidAmount() == null ? bill.getTotalAmount() : bill.getPaidAmount();
            totalSoldQuantity += bill.getTotalQuantity();
        }
        IncomeStat stat = new IncomeStat();
        stat.setPeriodType(periodType);
        stat.setTotalRevenue(totalRevenue);
        stat.setTotalBills(totalBills);
        stat.setTotalSoldQuantity(totalSoldQuantity);
        return stat;
    }
}
