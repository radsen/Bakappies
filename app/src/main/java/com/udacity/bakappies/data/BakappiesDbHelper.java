package com.udacity.bakappies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.udacity.bakappies.data.BakappiesContract.RecipesEntry;
import com.udacity.bakappies.data.BakappiesContract.IngredientEntry;
import com.udacity.bakappies.data.BakappiesContract.StepEntry;

/**
 * Created by radsen on 5/8/17.
 */

public class BakappiesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bakappies.db";

    private static final int DATABASE_VERSION = 3;

    private static final String SQL_CREATE_RECIPE_TABLE =
            "CREATE TABLE " + RecipesEntry.TABLE_NAME + " (" +
                    RecipesEntry._ID + " INTEGER PRIMARY KEY, " +
                    RecipesEntry.NAME + " TEXT NOT NULL, " +
                    RecipesEntry.SERVINGS + " INTEGER DEFAULT 0, " +
                    RecipesEntry.IMAGE + " TEXT, " +
                    " UNIQUE (" + RecipesEntry._ID + ") ON CONFLICT REPLACE);";

    private static final String SQL_CREATE_INGREDIENT_TABLE =
            "CREATE TABLE " + IngredientEntry.TABLE_NAME + " (" +
                    IngredientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    IngredientEntry.RECIPE_ID + " INTEGER, " +
                    IngredientEntry.QTY + " INTEGER DEFAULT 0, " +
                    IngredientEntry.MEASURE + " TEXT NOT NULL, " +
                    IngredientEntry.INGREDIENT + " TEXT NOT NULL, " +
                    " UNIQUE (" + IngredientEntry._ID + ") ON CONFLICT REPLACE);";

    private static final String SQL_CREATE_STEP_TABLE =
            "CREATE TABLE " + StepEntry.TABLE_NAME + " (" +
                    StepEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    StepEntry.SERVER_ID + " INTEGER, " +
                    StepEntry.RECIPE_ID + " INTEGER, " +
                    StepEntry.SHORT_DESC + " TEXT NOT NULL, " +
                    StepEntry.DESCRIPTION + " TEXT NOT NULL, " +
                    StepEntry.VIDEO_URL + " TEXT, " +
                    StepEntry.THUMBNAIL_URL + " TEXT, " +
                    " UNIQUE (" + StepEntry._ID +
                    ") ON CONFLICT REPLACE);";

    public BakappiesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_RECIPE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_INGREDIENT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STEP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + IngredientEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + StepEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
