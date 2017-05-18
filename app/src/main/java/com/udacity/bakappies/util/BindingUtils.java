package com.udacity.bakappies.util;

import android.text.TextUtils;
import android.widget.ImageView;

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

}
