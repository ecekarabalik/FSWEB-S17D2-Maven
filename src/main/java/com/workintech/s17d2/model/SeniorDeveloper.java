package com.workintech.s17d2.model;

public class SeniorDeveloper extends Developer {
    public SeniorDeveloper(Integer id, String name, Double salaryNet) {
        super(id, name, salaryNet, Experience.SENIOR);
    }
}