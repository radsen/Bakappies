package com.udacity.bakappies.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;
import com.udacity.bakappies.R;

/**
 * Created by radsen on 5/10/17.
 */

public class BindingUtils {

    public static void loadImage(ImageView view, String url, int defaultResource){
        if(TextUtils.isEmpty(url)){
            view.setImageResource(defaultResource);
        } else {
            Picasso.with(view.getContext())
                    .load(url)
                    .error(defaultResource)
                    .into(view);
        }
    }

    public static void loadImage(Context context, RemoteViews views, String url, int viewId,
                                 int appWidgetId, int defaultResource) {
        if(TextUtils.isEmpty(url)){
            views.setImageViewResource(viewId, defaultResource);
        } else {
            Picasso.with(context)
                    .load(url)
                    .error(defaultResource)
                    .into(views, viewId, new int[]{ appWidgetId });
        }
    }
}
