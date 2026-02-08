package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.bean.CourseBean;
import it.ispw.unilife.bean.FilterCourseBean;
import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.controller.CourseDiscoveryAndApplication;
import it.ispw.unilife.view.viewcli.SearchCourseCLIView;

import java.util.*;

public class SearchCourseCLIController implements CLIContoller {
    private final SearchCourseCLIView view = new SearchCourseCLIView();
    private final TokenBean tokenBean;
    private final CourseDiscoveryAndApplication courseController = new CourseDiscoveryAndApplication();
    private List<CourseBean> currentCourses;

    // Filter sets
    private Set<String> universityNames = new HashSet<>();
    private Set<String> locations = new HashSet<>();
    private Set<String> courseTypes = new HashSet<>();
    private Set<String> durations = new HashSet<>();
    private Set<String> rankings = new HashSet<>();
    private Set<String> languages = new HashSet<>();

    public SearchCourseCLIController(TokenBean tokenBean) {
        this.tokenBean = tokenBean;
    }

    @Override
    public void start(Scanner scanner) {
        view.showHeader();
        populateFilters();
        loadCourses(null);

        boolean flag = true;
        while (flag) {
            view.showMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    onSearch(scanner);
                    break;
                case "2":
                    onApplyFilters(scanner);
                    break;
                case "3":
                    onSelectCourse(scanner);
                    break;
                case "4":
                    flag = false;
                    break;
                case "0":
                    System.exit(0);
                    break;
                default:
                    view.showError("Invalid input");
            }
        }
    }

    private void populateFilters() {
        List<FilterCourseBean> filters = courseController.listSearchFilter();
        for (FilterCourseBean filter : filters) {
            if (filter.getUniversityName() != null) universityNames.add(filter.getUniversityName());
            if (filter.getUniversityLocation() != null) locations.add(filter.getUniversityLocation());
            if (filter.getCourseType() != null) courseTypes.add(filter.getCourseType());
            if (filter.getCourseDurationRange() != null) durations.add(filter.getCourseDurationRange());
            if (filter.getUniversityRankingRange() != null) rankings.add(filter.getUniversityRankingRange());
            if (filter.getLanguageOfInstruction() != null) languages.add(filter.getLanguageOfInstruction());
        }
    }

    private void loadCourses(FilterCourseBean filter) {
        currentCourses = courseController.searchCoursesByFilters(filter);
        view.showCourseList(currentCourses);
    }

    private void onSearch(Scanner scanner) {
        view.promptSearchText();
        String searchText = scanner.nextLine().trim();

        if (!searchText.isEmpty()) {
            CourseBean searchBean = new CourseBean();
            searchBean.setTitle(searchText);
            searchBean.setCourseType(searchText);
            currentCourses = courseController.searchCourseByName(searchBean);
            view.showCourseList(currentCourses);
        } else {
            loadCourses(null);
        }
    }

    private void onApplyFilters(Scanner scanner) {
        FilterCourseBean filter = new FilterCourseBean();

        view.showFilterMenu();

        String selected;

        selected = selectFromSet(scanner, "University Name", universityNames);
        if (selected != null) filter.setUniversityName(selected);

        selected = selectFromSet(scanner, "Location", locations);
        if (selected != null) filter.setUniversityLocation(selected);

        selected = selectFromSet(scanner, "Course Type", courseTypes);
        if (selected != null) filter.setCourseType(selected);

        selected = selectFromSet(scanner, "Duration Range", durations);
        if (selected != null) filter.setCourseDurationRange(selected);

        selected = selectFromSet(scanner, "Ranking Range", rankings);
        if (selected != null) filter.setUniversityRankingRange(selected);

        selected = selectFromSet(scanner, "Language", languages);
        if (selected != null) filter.setLanguageOfInstruction(selected);

        loadCourses(filter);
    }

    private String selectFromSet(Scanner scanner, String label, Set<String> options) {
        if (options == null || options.isEmpty()) return null;

        view.promptFilterOption(label, options);
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return null;

            List<String> list = new ArrayList<>(options);
            if (choice >= 1 && choice <= list.size()) {
                return list.get(choice - 1);
            }
        } catch (NumberFormatException e) {
            // skip
        }
        return null;
    }

    private void onSelectCourse(Scanner scanner) {
        if (currentCourses == null || currentCourses.isEmpty()) {
            view.showError("No courses to select.");
            return;
        }

        view.promptCourseNumber();
        try {
            int num = Integer.parseInt(scanner.nextLine().trim());
            if (num < 1 || num > currentCourses.size()) {
                view.showError("Invalid course number.");
                return;
            }
            CourseBean selected = currentCourses.get(num - 1);
            new CourseDetailCLIController(tokenBean, selected).start(scanner);
        } catch (NumberFormatException e) {
            view.showError("Invalid input.");
        }
    }
}
