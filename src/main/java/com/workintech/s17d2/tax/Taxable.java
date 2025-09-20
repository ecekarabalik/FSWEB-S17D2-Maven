package com.workintech.s17d2.tax;

public interface Taxable {
    double getSimpleTaxRate();   // 15
    double getMiddleTaxRate();   // 25
    double getUpperTaxRate();    // 35
}