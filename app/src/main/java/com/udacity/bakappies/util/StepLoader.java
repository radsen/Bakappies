package com.udacity.bakappies.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.udacity.bakappies.data.BakappiesContract.StepEntry;
import com.udacity.bakappies.model.Recipe;
import com.udacity.bakappies.model.Step;

import java.util.List;

/**
 * Created by radsen on 5/13/17.
 */

public class StepLoader extends AsyncTaskLoader<List<Step>> {

    public static final int STEP_LOADER = 5001;
    private final Uri uri;
    private final Recipe recipe;

    public StepLoader(Context context, Uri uri) {
        super(context);
        this.uri = uri;
        recipe = new Recipe();
    }

    @Override
    public List<Step> loadInBackground() {

        Cursor cRecipeSteps = getContext().getContentResolver().query(
                uri,
                StepEntry.PROJECTION,
                null,
                null,
                null
        );
        recipe.setSteps(cRecipeSteps);
        return recipe.getSteps();
    }
}
