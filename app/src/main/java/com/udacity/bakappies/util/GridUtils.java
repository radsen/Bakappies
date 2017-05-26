package com.udacity.bakappies.util;

import android.content.Context;
import android.util.DisplayMetrics;

import com.udacity.bakappies.R;

/**
 * Created by radsen on 5/18/17.
 */

public class GridUtils {
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = context.getResources().getInteger(R.integer.scale_factor);
        int noOfColumns = (int) (dpWidth / scalingFactor);
        return noOfColumns;
    }
}
