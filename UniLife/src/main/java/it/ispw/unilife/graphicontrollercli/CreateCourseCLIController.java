package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.bean.*;
import it.ispw.unilife.controller.ManageCourse;
import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.view.viewcli.CreateCourseCLIView;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CreateCourseCLIController implements CLIContoller {
    private final CreateCourseCLIView view = new CreateCourseCLIView();
    private final TokenBean tokenBean;
    private final ManageCourse appController = new ManageCourse();

    public CreateCourseCLIController(TokenBean tokenBean) {
        this.tokenBean = tokenBean;
    }

    @Override
    public void start(Scanner scanner) {
        view.showHeader();

        try {
            // 1. Raccolta Dati Base
            String title = promptAndGet("Title", scanner, true);
            String description = promptAndGet("Description", scanner, false);
            String language = promptAndGet("Language", scanner, false);

            // 2. Parsing e Validazione Numeri
            int duration = getValidDuration(scanner);
            double fees = getValidFees(scanner);

            // 3. Parsing Tags
            view.promptTags();
            List<String> tags = parseTags(scanner.nextLine().trim());

            // 4. Selezione Tipo Corso
            String courseType = selectCourseType(scanner);

            // 5. Raccolta Requisiti
            List<RequirementBean> reqList = collectRequirements(scanner);
            if (reqList.isEmpty()) return;

            // --- COSTRUZIONE DEL BEAN (Divisa per evitare > 7 parametri) ---

            // Fase A: Creazione e dati testuali (4 parametri)
            CourseBean courseBean = initCourseBean(title, description, language, courseType);

            // Fase B: Inserimento dati numerici e liste (5 parametri)
            finalizeCourseBean(courseBean, duration, fees, tags, reqList);

            // 6. Invio (Adesso accetta 1 solo parametro)
            submitCourse(courseBean);

        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError("Unexpected error: " + e.getMessage());
        }
    }

    // --- METODI HELPER PER RIDURRE I PARAMETRI ---

    /**
     * Inizializza il bean con le stringhe base.
     */
    private CourseBean initCourseBean(String title, String desc, String lang, String type) {
        CourseBean bean = new CourseBean();
        bean.setTitle(title);
        bean.setDescription(desc);
        bean.setLanguageOfInstruction(lang);
        bean.setCourseType(type);
        return bean;
    }

    /**
     * Completa il bean con numeri, tag e requisiti.
     */
    private void finalizeCourseBean(CourseBean bean, int duration, double fees,
                                    List<String> tags, List<RequirementBean> reqList) {
        bean.setDuration(duration);
        bean.setFees(fees);
        bean.setTags(tags);

        AdmissionRequirementBean admissionBean = new AdmissionRequirementBean();
        admissionBean.setRequirements(reqList);
        bean.setAdmissionRequirement(admissionBean);
    }

    /**
     * Invia il corso al controller applicativo.
     * Parametri ridotti da 8 a 1.
     */
    private void submitCourse(CourseBean courseBean) throws DAOException {
        appController.addCourse(tokenBean, courseBean);
        view.showMessage("Course created successfully!");
    }

    // --- ALTRI METODI DI SUPPORTO (Input/Output) ---

    private String promptAndGet(String fieldName, Scanner scanner, boolean isMandatory) {
        if (fieldName.equals("Title")) view.promptTitle();
        else if (fieldName.equals("Description")) view.promptDescription();
        else if (fieldName.equals("Language")) view.promptLanguage();

        String input = scanner.nextLine().trim();
        if (isMandatory && input.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is mandatory.");
        }
        return input;
    }

    private int getValidDuration(Scanner scanner) {
        view.promptDuration();
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Duration must be a valid integer number.");
        }
    }

    private double getValidFees(Scanner scanner) {
        view.promptFees();
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Fees must be a valid numeric value.");
        }
    }

    private List<String> parseTags(String tagsStr) {
        List<String> tags = new ArrayList<>();
        if (!tagsStr.isEmpty()) {
            String[] rawTags = tagsStr.split(",");
            for (String t : rawTags) {
                CourseTags validTag = CourseTags.fromString(t.trim());
                if (validTag != null) tags.add(validTag.name());
            }
        }
        return tags;
    }

    private String selectCourseType(Scanner scanner) {
        view.showCourseTypeMenu();
        String input = scanner.nextLine().trim();
        switch (input) {
            case "1": return "Undergraduate";
            case "2": return "PostGraduate";
            case "3": return "Phd";
            default: throw new IllegalArgumentException("Invalid course type.");
        }
    }

    private List<RequirementBean> collectRequirements(Scanner scanner) {
        List<RequirementBean> reqList = new ArrayList<>();
        boolean addingReqs = true;

        while (addingReqs) {
            view.showRequirementsMenu();
            String reqInput = scanner.nextLine().trim();

            switch (reqInput) {
                case "1":
                    RequirementBean textReq = collectTextRequirement(scanner);
                    if (textReq != null) reqList.add(textReq);
                    break;
                case "2":
                    RequirementBean docReq = collectDocRequirement(scanner);
                    if (docReq != null) reqList.add(docReq);
                    break;
                case "3":
                    addingReqs = false; // Stop adding, return list
                    break;
                case "4": // <--- Ritorna NULL per segnalare l'annullamento
                    view.showMessage("Course creation cancelled.");
                    return new ArrayList<>(); // Return null to signal cancellation
                default:
                    view.showError("Invalid input");
            }
        }

        return reqList;
    }

    private RequirementBean collectTextRequirement(Scanner scanner) {
        RequirementBean req = new RequirementBean();
        req.setType("TEXT");

        view.promptReqName();
        String name = scanner.nextLine().trim();
        req.setLabel(name);
        req.setName(name.replaceAll("\\s+", "_").toLowerCase());

        view.promptReqDescription();
        req.setDescription(scanner.nextLine().trim());

        view.promptReqMinChars();
        try {
            String minInput = scanner.nextLine().trim();
            if(!minInput.isEmpty()) req.setMinChars(Integer.parseInt(minInput));
        } catch (NumberFormatException e) {
            view.showError("Min chars must be a number");
            return null;
        }

        view.promptReqMaxChars();
        try {
            String maxInput = scanner.nextLine().trim();
            if(!maxInput.isEmpty()) req.setMaxChars(Integer.parseInt(maxInput));
        } catch (NumberFormatException e) {
            view.showError("Max chars must be a number");
            return null;
        }

        return req;
    }

    private RequirementBean collectDocRequirement(Scanner scanner) {
        RequirementBean req = new RequirementBean();
        req.setType("DOCUMENT");

        view.promptReqName();
        String name = scanner.nextLine().trim();
        req.setLabel(name);
        req.setName(name.replaceAll("\\s+", "_").toLowerCase());

        view.promptReqDescription();
        req.setDescription(scanner.nextLine().trim());

        view.promptReqExtension();
        req.setAllowedExtension(scanner.nextLine().trim());

        view.promptReqMaxSizeMB();
        try {
            String sizeInput = scanner.nextLine().trim();
            if(!sizeInput.isEmpty()) req.setMaxSizeMB(Double.parseDouble(sizeInput));
        } catch (NumberFormatException e) {
            view.showError("Size must be a number");
            return null;
        }

        view.promptReqIsCertificate();
        String certInput = scanner.nextLine().trim().toLowerCase();
        req.setCertificate("y".equals(certInput) || "yes".equals(certInput));

        return req;
    }
}