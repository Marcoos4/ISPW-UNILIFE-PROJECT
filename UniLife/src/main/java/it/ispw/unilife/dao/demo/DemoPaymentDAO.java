package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.PaymentDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Payment;

import java.util.ArrayList;
import java.util.List;

public class DemoPaymentDAO implements PaymentDAO {

    private static DemoPaymentDAO instance = null;
    private final List<Payment> cache = new ArrayList<>();

    private DemoPaymentDAO() {}

    public static DemoPaymentDAO getInstance() {
        if (instance == null) {
            instance = new DemoPaymentDAO();
        }
        return instance;
    }

    public Payment getPayment(String stripe){
        if (stripe == null) return null;
        for (Payment p : cache) {
            if (p.getStripe() != null && stripe.equals(p.getStripe().getId())) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void insert(Payment item){
        cache.add(item);
    }

    @Override
    public void update(Payment item){
        if (item.getStripe() != null) {
            cache.removeIf(p -> p.getStripe() != null && p.getStripe().getId().equals(item.getStripe().getId()));
        }
        cache.add(item);
    }

    @Override
    public void delete(Payment item){
        if (item.getStripe() != null) {
            cache.removeIf(p -> p.getStripe() != null && p.getStripe().getId().equals(item.getStripe().getId()));
        }
    }

    @Override
    public List<Payment> getAll() throws DAOException {
        return new ArrayList<>(cache);
    }

}
