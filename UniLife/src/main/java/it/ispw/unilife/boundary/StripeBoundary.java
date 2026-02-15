package it.ispw.unilife.boundary;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import it.ispw.unilife.bean.PaymentBean;

import java.util.logging.Logger;

public class StripeBoundary {

    private static final Logger logger = Logger.getLogger(StripeBoundary.class.getName());

    public StripeBoundary() {
        Stripe.apiKey = "sk_test_";
    }

    private PaymentIntentCreateParams setUpPayment(long amount, String currency, String paymentMethod) {
        return PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setPaymentMethod(paymentMethod)
                .setConfirm(true)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                .build()
                )
                .build();
    }

    public PaymentBean doPayment(PaymentBean paymentBean) {
        try {
            PaymentIntentCreateParams params = this.setUpPayment((long)paymentBean.getAmount(), paymentBean.getCurrency(), paymentBean.getPaymentMethodId());

            PaymentIntent payment = PaymentIntent.create(params);
            paymentBean.setStripe(payment);
            String failed = "FAILED";

            return switch (payment.getStatus()) {
                case "succeeded" -> {
                    logger.info("STRIPE: Pagamento completato! ID: " + payment.getId());
                    paymentBean.setStatus("PAID");
                    yield paymentBean;
                }
                case "requires_payment_method" -> {
                    logger.severe("STRIPE: Carta rifiutata o fondi insufficienti.");
                    paymentBean.setStatus(failed);
                    yield paymentBean;
                }
                case "requires_action" -> {
                    logger.severe("STRIPE: Richiesta autenticazione 3DS (Non supportata in questo flow API).");
                    paymentBean.setStatus(failed);
                    yield paymentBean;
                }
                default -> {
                    logger.severe("STRIPE: Stato imprevisto: " + payment.getStatus());
                    paymentBean.setStatus(failed);
                    yield paymentBean;
                }
            };

        } catch (StripeException e) {
            logger.severe("STRIPE EXCEPTION: " + e.getMessage());
            paymentBean.setStatus("FAILED");
            return paymentBean;
        }
    }
}