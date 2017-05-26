package com.udacity.bakappies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by radsen on 5/8/17.
 */

public class BakappiesContract {

    public static final String CONTENT_AUTHORITY = "com.udacity.bakappies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_RECIPE = "recipes";

    public static final class RecipesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPE)
                .build();

        public static final String TABLE_NAME = "recipe";

        public static final String NAME = "name";
        public static final String SERVINGS = "servings";
        public static final String IMAGE = "image";

        public static final String[] PROJECTION =
                new String[]{
                        _ID,
                        NAME,
                        SERVINGS,
                        IMAGE
        };

    }

    public static final String PATH_INGREDIENT = "ingredients";

    public static final class IngredientEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPE)
                .appendPath(PATH_INGREDIENT)
                .build();

        public static final String TABLE_NAME = "ingredient";

        public static final String RECIPE_ID = "recipe_id";
        public static final String QTY = "quantity";
        public static final String MEASURE = "measure";
        public static final String INGREDIENT = "ingredient";

        public static final String[] PROJECTION =
                new String[]{
                        _ID,
                        RECIPE_ID,
                        QTY,
                        MEASURE,
                        INGREDIENT
                };
    }

    public static final String PATH_STEP = "steps";

    public static final class StepEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPE)
                .appendPath(PATH_STEP)
                .build();

        public static final String TABLE_NAME = "step";

        // Server ID does nothing, it comes with the same ids for all the steps
        public static final String SERVER_ID = "server_id";

        public static final String RECIPE_ID = "recipe_id";
        public static final String SHORT_DESC = "shortDescription";
        public static final String DESCRIPTION = "description";
        public static final String VIDEO_URL = "videoURL";
        public static final String THUMBNAIL_URL = "thumbnailURL";

        public static final String[] PROJECTION =
                new String[]{
                        _ID,
                        RECIPE_ID,
                        SHORT_DESC,
                        DESCRIPTION,
                        VIDEO_URL,
                        THUMBNAIL_URL
                };
    }

    public static Uri buildUriForRandomRecipe(){
        Uri uri = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPE)
                .appendPath("random")
                .build();

        return uri;
    }

    public static Uri buildUriIngredient(int recipeId){
        Uri uri = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPE)
                .appendPath(String.valueOf(recipeId))
                .appendPath(PATH_INGREDIENT)
                .build();
        return uri;
    }

    public static Uri buildUriStep(int recipeId){
        Uri uri = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPE)
                .appendPath(String.valueOf(recipeId))
                .appendPath(PATH_STEP)
                .build();
        return uri;
    }
}
