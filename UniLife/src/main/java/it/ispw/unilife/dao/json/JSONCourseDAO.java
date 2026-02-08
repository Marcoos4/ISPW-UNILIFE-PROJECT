package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.CourseDAO;
import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.enums.CourseType;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.admission.AbstractRequirement;
import it.ispw.unilife.model.admission.AdmissionRequirements;
import it.ispw.unilife.model.admission.DocumentRequirement;
import it.ispw.unilife.model.admission.TextRequirement;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JSONCourseDAO implements CourseDAO {

    private static final String FILE_NAME = "courses.json";
    private static JSONCourseDAO instance = null;
    private final List<Course> cache = new ArrayList<>();

    private JSONCourseDAO() throws DAOException {
        try {
            getAll();
        } catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static synchronized JSONCourseDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new JSONCourseDAO();
        }
        return instance;
    }

    private void saveToFile() {
        Gson gson = JsonUtil.getGson();
        List<JsonRecords.CourseRecord> records = new ArrayList<>();
        for (Course c : cache) {
            JsonRecords.CourseRecord r = new JsonRecords.CourseRecord();
            r.setTitle(c.getCourseTitle());
            r.setDescription(c.getCourseDescription());
            r.setUniversityName(c.getUniversity().getName());
            r.setDuration(c.getCourseDuration());
            r.setFees(c.getCourseFees());
            r.setCourseType(c.getCourseType().toString());
            r.setLanguage(c.getLanguageOfInstruction());
            records.add(r);
        }
        JsonUtil.writeFile(JsonUtil.getFile(FILE_NAME), gson.toJson(records));
    }

    @Override
    public Course getCourse(String courseTitle, String universityName) throws DAOException {
        if (cache.isEmpty()) {
            getAll();
        }
        for (Course course : cache) {
            if (course.getCourseTitle().equals(courseTitle) && course.getUniversity().getName().equals(universityName)) {
                return course;
            }
        }
        throw new DAOException("Course not found");
    }

    @Override
    public List<Course> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        String json = JsonUtil.readFile(file);

        Type listType = new TypeToken<List<JsonRecords.CourseRecord>>() {}.getType();
        List<JsonRecords.CourseRecord> records = gson.fromJson(json, listType);

        if (records != null) {
            for (JsonRecords.CourseRecord r : records) {
                CourseType courseType = CourseType.fromString(r.getCourseType());

                AdmissionRequirements admissionRequirements = new AdmissionRequirements();
                List<DocumentRequirement> docReqs = JSONDocumentRequirementDAO.getInstance().getDocumentRequirements(r.getTitle(), r.getUniversityName());
                List<TextRequirement> textReqs = JSONTextRequirementDAO.getInstance().getTextRequirements(r.getTitle(), r.getUniversityName());

                for (DocumentRequirement dr : docReqs) {
                    admissionRequirements.addRequirement(dr);
                }
                for (TextRequirement tr : textReqs) {
                    admissionRequirements.addRequirement(tr);
                }

                List<CourseTags> courseTags = JSONCourseTagDAO.getInstance().getCourseTags(r.getTitle(), r.getUniversityName());

                Course course = new Course(r.getTitle(), r.getDescription(), courseType, r.getLanguage(),
                        JSONUniversityDAO.getInstance().getUniversity(r.getUniversityName()), courseTags, admissionRequirements);
                course.setCourseFees(r.getFees());
                course.setCourseDuration(r.getDuration());
                cache.add(course);
            }
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Course item) throws DAOException {
        for (CourseTags tag : item.getTags()) {
            JSONCourseTagDAO.getInstance().insert(tag, item);
        }
        for (AbstractRequirement req : item.getRequirements()) {
            if (req instanceof TextRequirement) {
                JSONTextRequirementDAO.getInstance().insert((TextRequirement) req, item);
            } else if (req instanceof DocumentRequirement) {
                JSONDocumentRequirementDAO.getInstance().insert((DocumentRequirement) req, item);
            }
        }
        cache.add(item);
        saveToFile();
    }

    @Override
    public void update(Course item) throws DAOException {
        for (CourseTags tag : item.getTags()) {
            JSONCourseTagDAO.getInstance().update(tag, item);
        }
        for (AbstractRequirement req : item.getRequirements()) {
            if (req instanceof TextRequirement) {
                JSONTextRequirementDAO.getInstance().update((TextRequirement) req, item);
            } else if (req instanceof DocumentRequirement) {
                JSONDocumentRequirementDAO.getInstance().update((DocumentRequirement) req, item);
            }
        }
        cache.removeIf(c -> c.getCourseTitle().equals(item.getCourseTitle()) && c.getUniversity().getName().equals(item.getUniversity().getName()));
        cache.add(item);
        saveToFile();
    }

    @Override
    public void delete(Course item) throws DAOException {
        for (CourseTags tag : item.getTags()) {
            JSONCourseTagDAO.getInstance().delete(tag, item);
        }
        for (AbstractRequirement req : item.getRequirements()) {
            if (req instanceof TextRequirement) {
                JSONTextRequirementDAO.getInstance().delete((TextRequirement) req, item);
            } else if (req instanceof DocumentRequirement) {
                JSONDocumentRequirementDAO.getInstance().delete((DocumentRequirement) req, item);
            }
        }
        cache.removeIf(c -> c.getCourseTitle().equals(item.getCourseTitle()) && c.getUniversity().getName().equals(item.getUniversity().getName()));
        saveToFile();
    }
}