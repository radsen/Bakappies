package com.udacity.bakappies.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.widget.RemoteViewsService;

import com.udacity.bakappies.R;
import com.udacity.bakappies.data.BakappiesContract;
import com.udacity.bakappies.data.BakappiesContract.RecipesEntry;
import com.udacity.bakappies.model.Recipe;

/**
 * Created by radsen on 5/23/17.
 */

public class BakeryService extends IntentService {

    private static final String ACTION_UPDATE_BAKERY = "com.bakappies.action.update";
    private static final String ACTION_SHOW_INGREDIENT = "com.bakappies.action.ingredients";

    public BakeryService() {
        super(BakeryService.class.getSimpleName());
    }

    public static void startUpdatingBakery(Context context){
        Intent updateBakeryIntent = new Intent(context, BakeryService.class);
        updateBakeryIntent.setAction(ACTION_UPDATE_BAKERY);
        context.startService(updateBakeryIntent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        if(ACTION_UPDATE_BAKERY.equals(action)){
            handleBakeryUpdate();
        }
    }

    private void handleBakeryUpdate() {
        Cursor cursor = getContentResolver().query(
                BakappiesContract.buildUriForRandomRecipe(),
                RecipesEntry.PROJECTION,
                null,
                null,
                null);

        Recipe recipe = null;
        if(cursor != null || cursor.getCount() > 0){
            cursor.moveToFirst();

            recipe = Recipe.create(cursor);
            cursor.close();
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                BakappiesAppWidgetProvider.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_container);
        //Now update all widgets
        BakappiesAppWidgetProvider.updateWidgets(this, appWidgetManager, appWidgetIds, recipe);
    }
}
