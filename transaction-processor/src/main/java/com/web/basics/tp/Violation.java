/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.basics.tp;

/**
 *
 * @author regan
 */
public class Violation {
    private int order;
    private String property;
    private String description;

    public Violation() {
    }

    public Violation(int order, String property, String description) {
        this.order = order;
        this.property = property;
        this.description = description;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Order = " + order + " Property = " + property
                + " Description = " + description;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Violation violation = (Violation) o;

        if (order != violation.order) return false;
        return property != null ? property.equals(violation.property) : violation.property == null;
    }

    @Override
    public int hashCode() {
        int result = order;
        result = 31 * result + (property != null ? property.hashCode() : 0);
        return result;
    }
}
