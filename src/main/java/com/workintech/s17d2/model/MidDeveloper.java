package com.workintech.s17d2.model;

public class MidDeveloper extends Developer {
    public MidDeveloper(Integer id, String name, Double salaryNet) {
        super(id, name, salaryNet, Experience.MID);
    }
}