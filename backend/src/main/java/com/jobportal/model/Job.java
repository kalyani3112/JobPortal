package com.jobportal.model;

public class Job {

    private int id;
    private String title;
    private String company;
    private String location;
    private String skills;
    private String description;

    public Job() {
    }

    public Job(int id, String title, String company, String location, String skills, String description) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.location = location;
        this.skills = skills;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}