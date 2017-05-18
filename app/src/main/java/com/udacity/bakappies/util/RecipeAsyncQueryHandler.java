package com.udacity.bakappies.util;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by radsen on 5/11/17.
 */

public class RecipeAsyncQueryHandler extends AsyncQueryHandler {

    public static final int INGREDIENT_TOKEN = 1001;
    public static final int STEP_TOKEN = 1002;

    private final QueryListener listener;

    public interface QueryListener{
        void onQueryComplete(int token, Cursor cursor);
    }

    public RecipeAsyncQueryHandler(ContentResolver cr, QueryListener listener) {
        super(cr);
        this.listener = listener;
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
        if(listener != null){
            listener.onQueryComplete(token, cursor);
        }
    }
}
