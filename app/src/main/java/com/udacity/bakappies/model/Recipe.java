package com.udacity.bakappies.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.udacity.bakappies.data.BakappiesContract;
import com.udacity.bakappies.data.BakappiesContract.IngredientEntry;
import com.udacity.bakappies.data.BakappiesContract.StepEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by radsen on 5/10/17.
 */

public class Recipe implements Parcelable {
    private int id;
    private String name;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private int servings;
    private String image;

    public Recipe(int id, String name, int servings, String image) {
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.image = image;
    }

    protected Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        servings = in.readInt();
        image = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public Recipe() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeInt(servings);
        parcel.writeString(image);
    }

    public void setIngredients(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0){
            ingredients = new ArrayList<>();
            if(cursor.moveToFirst()){
                do{
                    int idxId = cursor.getColumnIndex(IngredientEntry._ID);
                    int idxQty = cursor.getColumnIndex(IngredientEntry.QTY);
                    int idxMeasure = cursor.getColumnIndex(IngredientEntry.MEASURE);
                    int idxIngredient = cursor.getColumnIndex(IngredientEntry.INGREDIENT);

                    int id = cursor.getInt(idxId);
                    float qty = cursor.getFloat(idxQty);
                    String measure = cursor.getString(idxMeasure);
                    String ingredient = cursor.getString(idxIngredient);
                    ingredients.add(new Ingredient(qty, measure, ingredient));
                } while (cursor.moveToNext());
            }
        }
    }

    public void setSteps(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0){
            steps = new ArrayList<>();
            if(cursor.moveToFirst()){
                do{
                    int idxId = cursor.getColumnIndex(StepEntry._ID);
                    int idxShortDec = cursor.getColumnIndex(StepEntry.SHORT_DESC);
                    int idxDesc = cursor.getColumnIndex(StepEntry.DESCRIPTION);
                    int idxVideo = cursor.getColumnIndex(StepEntry.VIDEO_URL);
                    int idxThumb = cursor.getColumnIndex(StepEntry.THUMBNAIL_URL);

                    int id = cursor.getInt(idxId);
                    String shortDescription = cursor.getString(idxShortDec);
                    String description = cursor.getString(idxDesc);
                    String videoUrl = cursor.getString(idxVideo);
                    String thumbnail = cursor.getString(idxThumb);
                    steps.add(new Step(id, shortDescription, description, videoUrl, thumbnail));
                } while (cursor.moveToNext());
            }
        }
    }

    public static Recipe create(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(BakappiesContract.RecipesEntry._ID));
        String name = cursor.getString(cursor.getColumnIndex(BakappiesContract.RecipesEntry.NAME));
        int servings = cursor.getInt(cursor.getColumnIndex(BakappiesContract.RecipesEntry.SERVINGS));
        String image = cursor.getString(cursor.getColumnIndex(BakappiesContract.RecipesEntry.IMAGE));
        return new Recipe(id, name, servings, image);
    }
}
