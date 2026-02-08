package it.ispw.unilife.model;

import com.stripe.model.PaymentIntent;
import it.ispw.unilife.enums.PaymentStatus;

public class Payment {
    private float amount;
    private PaymentStatus status;
    private PaymentIntent stripe;

    public Payment(float amount, PaymentIntent stripe, PaymentStatus status) {
        this.amount = amount;
        this.stripe = stripe;
        this.status = status;
    }

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }

    public float showCost(){
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public PaymentIntent getStripe() {
        return stripe;
    }

    public void setStripe(PaymentIntent stripe) {
        this.stripe = stripe;
    }
}
