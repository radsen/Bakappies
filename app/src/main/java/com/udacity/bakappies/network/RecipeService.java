package com.udacity.bakappies.network;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.udacity.bakappies.common.BakappiesConstants;
import com.udacity.bakappies.data.BakappiesContract.RecipesEntry;
import com.udacity.bakappies.data.BakappiesContract.IngredientEntry;
import com.udacity.bakappies.data.BakappiesContract.StepEntry;
import com.udacity.bakappies.model.Ingredient;
import com.udacity.bakappies.model.Recipe;
import com.udacity.bakappies.model.Step;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by radsen on 5/11/17.
 */

public class RecipeService extends IntentService implements BakappiesConstants {

    private static final String TAG = RecipeService.class.getSimpleName();

    private BakingAppService api;

    public RecipeService() {
        super(Recipe.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        api = ServiceFactory.createService(BakingAppService.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int code = intent.getIntExtra(SERVICE_KEY, -1);
        Intent result = new Intent();
        switch (code){
            case RECIPE_LIST:
                try {
                    List<Recipe> recipes = api.fetchRecipes().execute().body();
                    cache(recipes);
                    result.setAction(ACTION_RECIPES);
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }
                break;
        }

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(result);
    }

    private void cache(List<Recipe> recipes) {
        List<ContentValues> cvRecipeList = new ArrayList<>();
        List<ContentValues> cvIngredientList = new ArrayList<>();
        List<ContentValues> cvStepList = new ArrayList<>();

        for (Recipe recipe : recipes){
            ContentValues cvRecipe = new ContentValues();
            cvRecipe.put(RecipesEntry._ID, recipe.getId());
            cvRecipe.put(RecipesEntry.NAME, recipe.getName());
            cvRecipe.put(RecipesEntry.SERVINGS, recipe.getServings());
            cvRecipe.put(RecipesEntry.IMAGE, recipe.getImage());

            for (Ingredient ingredient : recipe.getIngredients()){
                ContentValues cvIngredient = new ContentValues();
                cvIngredient.put(IngredientEntry.RECIPE_ID, recipe.getId());
                cvIngredient.put(IngredientEntry.QTY, ingredient.getQuantity());
                cvIngredient.put(IngredientEntry.MEASURE, ingredient.getMeasure());
                cvIngredient.put(IngredientEntry.INGREDIENT, ingredient.getIngredient());

                cvIngredientList.add(cvIngredient);
            }

            for (Step step : recipe.getSteps()){
                ContentValues cvStep = new ContentValues();
                cvStep.put(StepEntry.RECIPE_ID, recipe.getId());
                cvStep.put(StepEntry.SERVER_ID, step.getId());
                cvStep.put(StepEntry.SHORT_DESC, step.getShortDescription());
                cvStep.put(StepEntry.DESCRIPTION, step.getDescription());
                cvStep.put(StepEntry.VIDEO_URL, step.getVideoURL());
                cvStep.put(StepEntry.THUMBNAIL_URL, step.getThumbnailURL());

                cvStepList.add(cvStep);
            }

            cvRecipeList.add(cvRecipe);
        }

        getContentResolver().bulkInsert(RecipesEntry.CONTENT_URI,
                cvRecipeList.toArray(new ContentValues[cvRecipeList.size()]));

        getContentResolver().bulkInsert(IngredientEntry.CONTENT_URI,
                cvIngredientList.toArray(new ContentValues[cvIngredientList.size()]));

        getContentResolver().bulkInsert(StepEntry.CONTENT_URI,
                cvStepList.toArray(new ContentValues[cvStepList.size()]));
    }
}
