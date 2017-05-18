package com.udacity.bakappies.util;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.udacity.bakappies.data.BakappiesContract.RecipesEntry;
import com.udacity.bakappies.model.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by radsen on 5/11/17.
 */

public class RecipeLoader extends AsyncTaskLoader<List<Recipe>> {

    public RecipeLoader(Context context) {
        super(context);
    }

    @Override
    public List<Recipe> loadInBackground() {

        Cursor cursor = getContext().getContentResolver()
                .query(RecipesEntry.CONTENT_URI,
                        RecipesEntry.PROJECTION, null, null, null);

        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            List<Recipe> recipeList = new ArrayList<>();

            do{
                int idxId = cursor.getColumnIndex(RecipesEntry._ID);
                int idxName = cursor.getColumnIndex(RecipesEntry.NAME);
                int idxServings = cursor.getColumnIndex(RecipesEntry.SERVINGS);
                int idxImage = cursor.getColumnIndex(RecipesEntry.IMAGE);

                int id = cursor.getInt(idxId);
                String name = cursor.getString(idxName);
                int servings = cursor.getInt(idxServings);
                String image = cursor.getString(idxImage);
                recipeList.add(new Recipe(id, name, servings, image));
            }
            while (cursor.moveToNext());

            return recipeList;
        } else {
            return null;
        }
    }
}
