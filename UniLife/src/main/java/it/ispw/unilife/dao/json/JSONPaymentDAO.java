package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stripe.model.PaymentIntent;
import it.ispw.unilife.dao.PaymentDAO;
import it.ispw.unilife.enums.PaymentStatus;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Payment;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JSONPaymentDAO implements PaymentDAO {

    private static final String FILE_NAME = "payments.json";
    private static JSONPaymentDAO instance = null;
    private final List<JsonRecords.PaymentRecord> records = new ArrayList<>();
    private final List<Payment> cache = new ArrayList<>();

    private JSONPaymentDAO() {
        loadFromFile();
    }

    public static synchronized JSONPaymentDAO getInstance() {
        if (instance == null) {
            instance = new JSONPaymentDAO();
        }
        return instance;
    }

    private void loadFromFile() {
        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return;

        String json = JsonUtil.readFile(file);
        Type listType = new TypeToken<List<JsonRecords.PaymentRecord>>() {}.getType();
        List<JsonRecords.PaymentRecord> loaded = gson.fromJson(json, listType);
        if (loaded != null) {
            records.addAll(loaded);
        }
    }

    private void saveToFile() {
        JsonUtil.writeFile(JsonUtil.getFile(FILE_NAME), JsonUtil.getGson().toJson(records));
    }

    public Payment getPayment(String stripe){
        if (stripe == null) return null;
        for (JsonRecords.PaymentRecord r : records) {
            if (stripe.equals(r.getPaymentStripe())) {
                PaymentIntent stripeObj = new PaymentIntent();
                stripeObj.setId(r.getPaymentStripe());
                return new Payment(r.getAmount(), stripeObj, PaymentStatus.fromString(r.getStatus()));
            }
        }
        return null;
    }

    @Override
    public void insert(Payment item) throws DAOException {
        if (item.getStripe() == null || item.getStripe().getId() == null) {
            throw new DAOException("Cannot insert Payment without a valid Stripe ID");
        }
        JsonRecords.PaymentRecord r = new JsonRecords.PaymentRecord();
        r.setAmount(item.showCost());
        r.setStatus(item.getStatus().toString());
        r.setPaymentStripe(item.getStripe().getId());
        records.add(r);
        cache.add(item);
        saveToFile();
    }

    @Override
    public void update(Payment item) throws DAOException {
        if (item.getStripe() == null || item.getStripe().getId() == null) {
            throw new DAOException("Cannot update Payment without a valid Stripe ID");
        }
        records.removeIf(r -> r.getPaymentStripe().equals(item.getStripe().getId()));
        JsonRecords.PaymentRecord r = new JsonRecords.PaymentRecord();
        r.setAmount(item.showCost());
        r.setStatus(item.getStatus().toString());
        r.setPaymentStripe(item.getStripe().getId());
        records.add(r);
        saveToFile();
    }

    @Override
    public void delete(Payment item) throws DAOException {
        if (item.getStripe() == null || item.getStripe().getId() == null) return;
        records.removeIf(r -> r.getPaymentStripe().equals(item.getStripe().getId()));
        saveToFile();
    }

    @Override
    public List<Payment> getAll() throws DAOException {
        return List.of();
    }

}