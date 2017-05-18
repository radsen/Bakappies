package com.udacity.bakappies.network;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;

import com.udacity.bakappies.common.BakappiesConstants;
import com.udacity.bakappies.data.BakappiesContract.RecipesEntry;

/**
 * Created by radsen on 5/11/17.
 */

public class SyncUtils {

    private static boolean sInitialized;

    synchronized public static void initialize(final Context context){
        if(sInitialized){
            return;
        }

        sInitialized = true;

        Thread checkContent = new Thread(){
            @Override
            public void run() {
                super.run();
                Cursor cursor = context.getContentResolver()
                        .query(RecipesEntry.CONTENT_URI,
                                RecipesEntry.PROJECTION,
                                null,
                                null,
                                null
                        );

                if(cursor == null || cursor.getCount() == 0){
                    startRecipeSync(context);
                } else {
                    Intent result = new Intent();
                    result.setAction(BakappiesConstants.ACTION_RECIPES);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(result);
                }

            }
        };

        checkContent.start();
    }

    public static void startRecipeSync(Context context) {
        Intent iService = new Intent(context, RecipeService.class);
        iService.putExtra(BakappiesConstants.SERVICE_KEY, BakappiesConstants.RECIPE_LIST);
        context.startService(iService);
    }

    public static boolean isInitialized() {
        return sInitialized;
    }
}
