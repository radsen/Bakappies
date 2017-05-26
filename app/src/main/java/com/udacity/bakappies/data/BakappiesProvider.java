package com.udacity.bakappies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.udacity.bakappies.data.BakappiesContract.RecipesEntry;
import com.udacity.bakappies.data.BakappiesContract.IngredientEntry;
import com.udacity.bakappies.data.BakappiesContract.StepEntry;

/**
 * Created by radsen on 5/8/17.
 */

public class BakappiesProvider extends ContentProvider {

    private static final int CODE_RECIPE = 101;
    private static final int CODE_INGREDIENTS_BY_RECIPE = 102;
    private static final int CODE_STEPS_BY_RECIPE = 103;
    private static final int CODE_RANDOM_RECIPE = 104;

    private static final int CODE_INGREDIENT = 201;
    private static final int CODE_INGREDIENT_BY_ID = 202;

    private static final int CODE_STEP = 301;
    private static final int CODE_STEP_BY_ID = 302;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(BakappiesContract.CONTENT_AUTHORITY, BakappiesContract.PATH_RECIPE,
                CODE_RECIPE);

        uriMatcher.addURI(BakappiesContract.CONTENT_AUTHORITY,
                BakappiesContract.PATH_RECIPE + "/random",
                CODE_RANDOM_RECIPE);

        uriMatcher.addURI(BakappiesContract.CONTENT_AUTHORITY,
                BakappiesContract.PATH_RECIPE + "/#/" + BakappiesContract.PATH_INGREDIENT,
                CODE_INGREDIENTS_BY_RECIPE);

        uriMatcher.addURI(BakappiesContract.CONTENT_AUTHORITY,
                BakappiesContract.PATH_RECIPE + "/#/" + BakappiesContract.PATH_STEP,
                CODE_STEPS_BY_RECIPE);

        uriMatcher.addURI(BakappiesContract.CONTENT_AUTHORITY,
                BakappiesContract.PATH_RECIPE + "/" + BakappiesContract.PATH_INGREDIENT,
                CODE_INGREDIENT);

        uriMatcher.addURI(BakappiesContract.CONTENT_AUTHORITY,
                BakappiesContract.PATH_RECIPE + "/" + BakappiesContract.PATH_STEP,
                CODE_STEP);

        uriMatcher.addURI(BakappiesContract.CONTENT_AUTHORITY,
                BakappiesContract.PATH_INGREDIENT + "/#",
                CODE_INGREDIENT_BY_ID);

        uriMatcher.addURI(BakappiesContract.CONTENT_AUTHORITY,
                BakappiesContract.PATH_STEP + "/#",
                CODE_STEP_BY_ID);

        return uriMatcher;
    }

    private BakappiesDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BakappiesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s,
                        @Nullable String[] strings1, @Nullable String s1) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int code = sUriMatcher.match(uri);
        Cursor cursor = null;
        int index = 0;
        int recipeId = 0;

        switch (code){
            case CODE_RECIPE:
                cursor = db.query(
                        RecipesEntry.TABLE_NAME,
                        RecipesEntry.PROJECTION,
                        null,
                        null,
                        null,
                        null,
                        RecipesEntry.NAME
                );
                break;
            case CODE_INGREDIENTS_BY_RECIPE:
                index = uri.getPathSegments().size() - 2;
                recipeId = Integer.parseInt(uri.getPathSegments().get(index));
                cursor = db.query(
                        IngredientEntry.TABLE_NAME,
                        IngredientEntry.PROJECTION,
                        IngredientEntry.RECIPE_ID + " = ? ",
                        new String[]{ String.valueOf(recipeId) },
                        null,
                        null,
                        IngredientEntry.INGREDIENT
                );
                break;
            case CODE_STEPS_BY_RECIPE:
                index = uri.getPathSegments().size() - 2;
                recipeId = Integer.parseInt(uri.getPathSegments().get(index));
                cursor = db.query(
                        StepEntry.TABLE_NAME,
                        StepEntry.PROJECTION,
                        StepEntry.RECIPE_ID + " = ? ",
                        new String[]{ String.valueOf(recipeId) },
                        null,
                        null,
                        null
                );
                break;
            case CODE_INGREDIENT:
                cursor = db.query(
                        IngredientEntry.TABLE_NAME,
                        IngredientEntry.PROJECTION,
                        null,
                        null,
                        null,
                        null,
                        IngredientEntry.INGREDIENT
                );
                break;
            case CODE_STEP:
                cursor = db.query(
                        StepEntry.TABLE_NAME,
                        StepEntry.PROJECTION,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                break;
            case  CODE_RANDOM_RECIPE:
                String orderBy = "RANDOM() LIMIT 1";
                cursor = db.query(
                        RecipesEntry.TABLE_NAME,
                        RecipesEntry.PROJECTION,
                        null,
                        null,
                        null,
                        null,
                        orderBy
                );
                break;
        }

        if(cursor != null && cursor.getCount() > 0){
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String tableName = "";
        switch (sUriMatcher.match(uri)){
            case CODE_RECIPE:
                tableName = RecipesEntry.TABLE_NAME;
                break;
            case CODE_INGREDIENT:
                tableName = IngredientEntry.TABLE_NAME;
                break;
            case CODE_STEP:
                tableName = StepEntry.TABLE_NAME;
                break;
        }

        if(!tableName.isEmpty()){
            db.beginTransaction();
            int rowsInserted = 0;

            try {
                for (ContentValues value : values){
                    long id = db.insert(tableName, null, value);

                    if(id != -1){
                        rowsInserted++;
                    }
                }

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            if(rowsInserted > 0){
                getContext().getContentResolver().notifyChange(uri, null);
            }

            return rowsInserted;
        } else {
            return bulkInsert(uri, values);
        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s,
                      @Nullable String[] strings) {
        return 0;
    }
}
