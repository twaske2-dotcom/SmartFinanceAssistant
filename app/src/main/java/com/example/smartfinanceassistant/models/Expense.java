package com.example.smartfinanceassistant.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expenses")
public class Expense {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String category;
    public double amount;
    public String date;
    public String note;
    public String type; // "income" or "expense"

    // Constructor
    public Expense(String title, String category,
                   double amount, String date,
                   String note, String type) {
        this.title = title;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.note = note;
        this.type = type;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public String getNote() { return note; }
    public String getType() { return type; }
}