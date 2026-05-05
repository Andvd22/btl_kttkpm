package btl.kttkpm.quanlybanhangsieuthi.dto;

public class CheckoutForm {

    private Integer customerId;
    private String paymentMethod;
    private Float receivedAmount;
    private Boolean paymentConfirmed;
    private Boolean useRewardPoint;
    private String note;

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Float getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(Float receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public Boolean getPaymentConfirmed() {
        return paymentConfirmed;
    }

    public void setPaymentConfirmed(Boolean paymentConfirmed) {
        this.paymentConfirmed = paymentConfirmed;
    }

    public Boolean getUseRewardPoint() {
        return useRewardPoint;
    }

    public void setUseRewardPoint(Boolean useRewardPoint) {
        this.useRewardPoint = useRewardPoint;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
