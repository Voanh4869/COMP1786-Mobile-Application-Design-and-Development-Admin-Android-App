package com.example.expensetrackerapp.models;

import java.io.Serializable;

public class Project implements Serializable {
    private int id;
    private String name, description, startDate, endDate, manager, status;
    private double budget;

    public Project(int id, String name, String description, String startDate,
                   String endDate, String manager, String status, double budget) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.manager = manager;
        this.status = status;
        this.budget = budget;
    }

    public Project(String name, String description, String startDate,
                   String endDate, String manager, String status, double budget) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.manager = manager;
        this.status = status;
        this.budget = budget;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getManager() { return manager; }
    public String getStatus() { return status; }
    public double getBudget() { return budget; }
}