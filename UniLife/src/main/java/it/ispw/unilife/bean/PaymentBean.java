package it.ispw.unilife.bean;

import com.stripe.model.PaymentIntent;

public class PaymentBean {
    private float amount;
    private String status;
    private PaymentIntent stripe;
    private String currency;
    private String paymentMethod;

    public float getAmount() {return amount;}
    public void setAmount(float amount) {this.amount = amount;}
    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}
    public PaymentIntent getStripe() {return stripe;}
    public void setStripe(PaymentIntent stripe) {this.stripe = stripe;}
    public String getCurrency() {return currency;}
    public void setCurrency(String currency) {this.currency = currency;}
    public String getPaymentMethodId() {return paymentMethod;}
    public void setPaymentMethodId(String paymentMethodId) {this.paymentMethod = paymentMethodId;}
}
