package com.udacity.bakappies.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.widget.RemoteViews;

import com.udacity.bakappies.R;
import com.udacity.bakappies.activity.RecipeActivity;
import com.udacity.bakappies.common.BakappiesConstants;
import com.udacity.bakappies.model.Recipe;
import com.udacity.bakappies.util.BindingUtils;


/**
 * Created by radsen on 5/23/17.
 */

public class BakappiesAppWidgetProvider extends AppWidgetProvider {

    private static final String TAG = BakappiesAppWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        BakeryService.startUpdatingBakery(context);
    }

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager,
                                     int[] appWidgetIds, Recipe recipe) {
        for (int appWidgetId : appWidgetIds){
            updateWidget(context, appWidgetManager, appWidgetId, recipe);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        BakeryService.startUpdatingBakery(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    private static void updateWidget(Context context, AppWidgetManager appWidgetManager,
                                     int appWidgetId, Recipe recipe) {

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

        RemoteViews rv;
        if(width >= 110 && height >= 110) {
            rv = getRecipe(context, appWidgetManager, appWidgetId, recipe, true);
        } else  {
            rv = getRecipe(context, appWidgetManager, appWidgetId, recipe, false);
        }

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    private static RemoteViews getRecipe(Context context, AppWidgetManager appWidgetManager,
                                         int appWidgetId, Recipe recipe,
                                         boolean shouldShowIngredients) {

        Intent intent = new Intent(context, RecipeActivity.class);
        intent.putExtra(BakappiesConstants.RECIPE_KEY, recipe);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        int layout;
        if(shouldShowIngredients){
           layout = R.layout.bakappies_app_widget_ingredients;
        } else {
            layout = R.layout.bakappies_app_widget;
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), layout);

        BindingUtils.loadImage(context, views, recipe.getImage(), R.id.widget_recipe_image,
                appWidgetId, R.drawable.ic_bake_default);

        views.setTextViewText(R.id.widget_title, recipe.getName());
        String format = context.getString(R.string.txt_servings);
        views.setTextViewText(R.id.widget_servings, String.format(format, recipe.getServings()));

        if(shouldShowIngredients){
            Intent intentIngredients = new Intent(context, WidgetService.class);

            intentIngredients.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intentIngredients.setData(Uri.parse(intentIngredients.toUri(Intent.URI_INTENT_SCHEME)));

            intentIngredients.putExtra(BakappiesConstants.RECIPE_ID_KEY, recipe.getId());

            views.setRemoteAdapter(R.id.widget_ingredient_list, intentIngredients);
            views.setPendingIntentTemplate(R.id.widget_ingredient_list, pendingIntent);

            views.setEmptyView(R.id.widget_ingredient_list, R.id.empty_view);
        } else {
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);
        }

        return views;
    }
}
