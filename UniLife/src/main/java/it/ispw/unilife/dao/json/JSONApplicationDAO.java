package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.ApplicationDAO;
import it.ispw.unilife.enums.ApplicationStatus;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.admission.Application;
import it.ispw.unilife.model.admission.ApplicationItem;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSONApplicationDAO implements ApplicationDAO {

    private static final Logger logger = Logger.getLogger(JSONApplicationDAO.class.getName());
    private static final String FILE_NAME = "applications.json";
    private static JSONApplicationDAO instance = null;
    private final List<Application> cache = new ArrayList<>();

    private JSONApplicationDAO() {
        // Carica la cache all'avvio
        try {
            getAll();
        } catch (DAOException e) {
            // In un costruttore singleton, lanciare RuntimeException è spesso l'unica via
            // se non si vuole gestire l'eccezione checked nel getInstance
            logger.log(Level.SEVERE, "Errore nel caricamento iniziale delle application", e);
        }
    }

    public static synchronized JSONApplicationDAO getInstance() {
        if (instance == null) {
            instance = new JSONApplicationDAO();
        }
        return instance;
    }

    private void saveToFile() {
        Gson gson = JsonUtil.getGson();
        List<JsonRecords.ApplicationRecord> records = new ArrayList<>();

        for (Application a : cache) {
            JsonRecords.ApplicationRecord r = new JsonRecords.ApplicationRecord();

            // --- USA I SETTER INVECE DELL'ACCESSO DIRETTO ---
            r.setCourseTitle(a.getCourse().getCourseTitle());
            r.setUniversityName(a.getCourse().getUniversity().getName());
            r.setCreationDate(a.getCreationDate());
            r.setStudentUsername(a.getApplicant().getUsername());
            r.setSubmissionDate(a.getSubmissionDate());
            r.setStatus(a.getStatus().toString());

            records.add(r);
        }

        // Scrittura su file usando l'utility JSON
        JsonUtil.writeFile(JsonUtil.getFile(FILE_NAME), gson.toJson(records));
    }

    @Override
    public List<Application> getAll() throws DAOException {
        // Se la cache è popolata, restituisci quella per evitare I/O disco
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);

        // Se il file non esiste o è vuoto, ritorna lista vuota
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        String json = JsonUtil.readFile(file);
        Type listType = new TypeToken<List<JsonRecords.ApplicationRecord>>() {}.getType();
        List<JsonRecords.ApplicationRecord> records = gson.fromJson(json, listType);

        if (records != null) {
            for (JsonRecords.ApplicationRecord r : records) {
                try {
                    // --- USA I GETTER INVECE DELL'ACCESSO DIRETTO ---

                    // Recupera gli item associati (requirements compilati)
                    List<ApplicationItem> items = JSONApplicationItemDAO.getInstance().getItems(
                            r.getCourseTitle(),
                            r.getUniversityName(),
                            r.getStudentUsername(),
                            r.getCreationDate()
                    );

                    // Ricostruisce l'oggetto Application completo
                    Application app = new Application(
                            ApplicationStatus.fromString(r.getStatus()),
                            r.getSubmissionDate(),
                            r.getCreationDate(),
                            JSONStudentDAO.getInstance().getStudent(r.getStudentUsername()),
                            JSONCourseDAO.getInstance().getCourse(r.getCourseTitle(), r.getUniversityName()),
                            items
                    );

                    cache.add(app);

                } catch (UserNotFoundException e) {
                    // Logga l'errore se un utente referenziato nel JSON non esiste più, ma continua a caricare gli altri
                    logger.log(Level.WARNING, "Studente non trovato per application: {0}", r.getStudentUsername());
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Errore generico nel parsing di una application", e);
                }
            }
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Application item) throws DAOException {
        // Prima salva gli item (composizione)
        for (ApplicationItem applicationItem : item.getItems()) {
            JSONApplicationItemDAO.getInstance().insert(applicationItem, item);
        }
        // Poi salva l'application
        cache.add(item);
        saveToFile();
    }

    @Override
    public void update(Application item) throws DAOException {
        // Aggiorna gli item
        for (ApplicationItem applicationItem : item.getItems()) {
            JSONApplicationItemDAO.getInstance().update(applicationItem, item);
        }
        // Aggiorna l'application in cache (rimuovi vecchia, aggiungi nuova)
        cache.removeIf(a -> a.equals(item));
        cache.add(item);
        saveToFile();
    }

    @Override
    public void delete(Application item) throws DAOException {
        // Nota: idealmente dovresti cancellare anche gli item associati in JSONApplicationItemDAO
        cache.removeIf(a -> a.equals(item));
        saveToFile();
    }

    @Override
    public List<Application> getApplications(String username) throws DAOException {
        // Assicurati che la cache sia caricata
        if (cache.isEmpty()) {
            getAll();
        }

        List<Application> res = new ArrayList<>();
        for (Application application : cache) {
            if (application.getApplicant().getUsername().equals(username)) {
                res.add(application);
            }
        }
        return res;
    }

    @Override
    public Application getApplication(String courseTitle, String universityName, String studentUsername, LocalDateTime creationDate) throws DAOException {
        // Assicurati che la cache sia caricata
        if (cache.isEmpty()) {
            getAll();
        }

        for (Application application : cache) {
            if (application.getApplicant().getUsername().equals(studentUsername)
                    && application.getCourse().getCourseTitle().equals(courseTitle)
                    && application.getCourse().getUniversity().getName().equals(universityName)
                    && application.getCreationDate().equals(creationDate)) {
                return application;
            }
        }
        return null;
    }
}