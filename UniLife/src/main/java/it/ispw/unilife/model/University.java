package it.ispw.unilife.model;


public class University {
    private String name;
    private String location;
    private int ranking;
    private double livingCosts;



    public University(String name, String location, int ranking, double livingCosts) {
        this.name = name;
        this.location = location;
        this.ranking = ranking;
        this.livingCosts = livingCosts;
    }


    public String findLocation() {
        return location;
    }

    public int calculateRanking() { return ranking; }

    public double estimateLivingCosts() {
        return livingCosts;
    }

    public double getLivingCosts() { return livingCosts; }

    public int getRanking() { return ranking; }

    public void setName(String name) { this.name = name; }

    public void setLocation(String location) { this.location = location; }

    public String getName() { return name; }
    public String getLocation() { return location; }

    public void setLivingCosts(double livingCosts) { this.livingCosts = livingCosts; }
}