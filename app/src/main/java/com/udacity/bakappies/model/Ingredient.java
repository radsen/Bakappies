package com.udacity.bakappies.model;

/**
 * Created by radsen on 5/10/17.
 */

public class Ingredient {

    private float quantity;
    private String measure;
    private String ingredient;

    public Ingredient(float qty, String measure, String ingredient) {
        this.quantity = qty;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}
