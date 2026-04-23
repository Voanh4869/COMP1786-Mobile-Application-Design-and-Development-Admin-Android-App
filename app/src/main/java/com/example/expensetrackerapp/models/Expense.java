package com.example.expensetrackerapp.models;

import java.io.Serializable;

public class Expense implements Serializable {
    private int id;
    private int projectId;
    private String date;
    private double amount;
    private String currency;
    private String type;
    private String paymentMethod;
    private String claimant;
    private String paymentStatus;
    private String description;
    private String location;

    public Expense(int id, int projectId, String date, double amount, String currency, String type, 
                   String paymentMethod, String claimant, String paymentStatus, String description, String location) {
        this.id = id;
        this.projectId = projectId;
        this.date = date;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.claimant = claimant;
        this.paymentStatus = paymentStatus;
        this.description = description;
        this.location = location;
    }

    public int getId() { return id; }
    public int getProjectId() { return projectId; }
    public String getDate() { return date; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getType() { return type; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getClaimant() { return claimant; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
}