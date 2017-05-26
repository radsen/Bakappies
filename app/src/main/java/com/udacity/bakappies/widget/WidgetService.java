package com.udacity.bakappies.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by radsen on 5/24/17.
 */

public class WidgetService extends RemoteViewsService {

    private static final String TAG = WidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "onGetViewFactory");
        return new IngredientRemoteViewsFactory(getApplicationContext(), intent);
    }
}
