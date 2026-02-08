    package it.ispw.unilife.model.admission;

    import java.util.ArrayList;
    import java.util.List;

    public class AdmissionRequirements {

        private List<AbstractRequirement> requirements = new ArrayList<>();

        public void addRequirement(AbstractRequirement req) {
            this.requirements.add(req);
        }

        public List<AbstractRequirement> getRequirements() { return requirements; }

    }
