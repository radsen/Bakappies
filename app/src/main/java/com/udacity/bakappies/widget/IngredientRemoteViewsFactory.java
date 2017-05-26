package com.udacity.bakappies.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.udacity.bakappies.R;
import com.udacity.bakappies.activity.RecipeActivity;
import com.udacity.bakappies.common.BakappiesConstants;
import com.udacity.bakappies.data.BakappiesContract;
import com.udacity.bakappies.data.BakappiesContract.IngredientEntry;
import com.udacity.bakappies.model.Recipe;

/**
 * Created by radsen on 5/24/17.
 */

public class IngredientRemoteViewsFactory implements RemoteViewsFactory {

    private static final int INGREDIENT_TYPE = 1;

    private final Context context;
    private Cursor mIngredientCursor;
    private int recipeId;

    public IngredientRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;

        recipeId = intent.getIntExtra(BakappiesConstants.RECIPE_ID_KEY, 0);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        mIngredientCursor = context.getContentResolver().query(
                BakappiesContract.buildUriIngredient(recipeId),
                BakappiesContract.IngredientEntry.PROJECTION,
                null,
                null,
                null
        );
    }

    @Override
    public void onDestroy() {
        if(mIngredientCursor != null){
            mIngredientCursor.close();
        }
    }

    @Override
    public int getCount() {
        if (mIngredientCursor == null){
            return 0;
        }

        return mIngredientCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if(mIngredientCursor == null || mIngredientCursor.getCount() == 0) return null;

        mIngredientCursor.moveToPosition(position);

        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.widget_item);

        int ingredientIdx = mIngredientCursor.getColumnIndex(IngredientEntry.INGREDIENT);
        int quantityIdx = mIngredientCursor.getColumnIndex(IngredientEntry.QTY);
        int measureIdx = mIngredientCursor.getColumnIndex(IngredientEntry.MEASURE);

        String ingredient = mIngredientCursor.getString(ingredientIdx);
        float quantity = mIngredientCursor.getFloat(quantityIdx);
        String measure = mIngredientCursor.getString(measureIdx);

        remoteView.setTextViewText(R.id.ingredient, ingredient);
        remoteView.setTextViewText(R.id.quantity, String.valueOf(quantity));
        remoteView.setTextViewText(R.id.measure, measure);

        Intent clickIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(context.getPackageName(), position);
        clickIntent.putExtras(bundle);
        remoteView.setOnClickFillInIntent(R.id.widget_container, clickIntent);

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return INGREDIENT_TYPE;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
