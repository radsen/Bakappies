package com.udacity.bakappies.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.bakappies.R;
import com.udacity.bakappies.activity.StepDetailActivity;
import com.udacity.bakappies.adapter.RecipePartAdapter;
import com.udacity.bakappies.common.BakappiesConstants;
import com.udacity.bakappies.data.BakappiesContract;
import com.udacity.bakappies.data.BakappiesContract.IngredientEntry;
import com.udacity.bakappies.data.BakappiesContract.StepEntry;
import com.udacity.bakappies.model.Recipe;
import com.udacity.bakappies.model.Step;
import com.udacity.bakappies.util.RecipeAsyncQueryHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by radsen on 5/8/17.
 */

public class FragmentRecipe extends BaseFragment implements RecipeAsyncQueryHandler.QueryListener,
        RecipePartAdapter.ItemClickListener {

    private static final String TAG = FragmentRecipe.class.getSimpleName();

    @BindView(R.id.rv_recipe_parts)
    RecyclerView rvRecipeParts;

    private Unbinder unbinder;

    private RecipeAsyncQueryHandler recipeQueryHandler;
    private Recipe recipe;
    private RecipePartAdapter mAdapter;
    private OnRecipeListener recipeListener;
    private boolean isTablet;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnRecipeListener){
            recipeListener = (OnRecipeListener) context;
        } else {
            throw new ClassCastException("The activity must implement OnRecipeListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        isTablet = getResources().getBoolean(R.bool.isTablet);

        recipe = getArguments().getParcelable(BakappiesConstants.RECIPE_KEY);

        rvRecipeParts.setHasFixedSize(true);
        RecyclerView.LayoutManager mLinearLayout = new LinearLayoutManager(getContext());
        rvRecipeParts.setLayoutManager(mLinearLayout);
        mAdapter = new RecipePartAdapter(getContext(), null, this);
        rvRecipeParts.setAdapter(mAdapter);

        recipeQueryHandler = new RecipeAsyncQueryHandler(getContext().getContentResolver(), this);
        recipeQueryHandler.startQuery(
                RecipeAsyncQueryHandler.INGREDIENT_TOKEN,
                null,
                BakappiesContract.buildUriIngredient(recipe.getId()),
                IngredientEntry.PROJECTION,
                null,
                null,
                null
        );

        recipeQueryHandler.startQuery(
                RecipeAsyncQueryHandler.STEP_TOKEN,
                null,
                BakappiesContract.buildUriStep(recipe.getId()),
                StepEntry.PROJECTION,
                null,
                null,
                null
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static FragmentRecipe newInstance(Recipe recipe) {
        FragmentRecipe fragmentRecipe = new FragmentRecipe();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BakappiesConstants.RECIPE_KEY, recipe);
        fragmentRecipe.setArguments(bundle);
        return fragmentRecipe;
    }

    @Override
    public void onQueryComplete(int token, Cursor cursor) {
        switch (token){
            case RecipeAsyncQueryHandler.INGREDIENT_TOKEN:
                Log.d(TAG, "Ingredients: " + cursor.getCount());
                recipe.setIngredients(cursor);
                break;
            case RecipeAsyncQueryHandler.STEP_TOKEN:
                Log.d(TAG, "Steps: " + cursor.getCount());
                recipe.setSteps(cursor);
                mAdapter.swap(recipe);
                if (isTablet) recipeListener.onRecipeSelected(recipe.getSteps().get(0));
                break;
        }
    }

    @Override
    public void onClick(int position) {
        if(!getResources().getBoolean(R.bool.isTablet)){
            Intent intent = new Intent(getContext(), StepDetailActivity.class);
            intent.putExtra(BakappiesConstants.RECIPE_ID_KEY, recipe.getId());
            intent.putExtra(BakappiesConstants.RECIPE_NAME_KEY, recipe.getName());
            intent.putExtra(BakappiesConstants.STEP_NUMBER_KEY, position);
            startActivity(intent);
        } else {
            recipeListener.onRecipeSelected(recipe.getSteps().get(position));
        }

    }

    public interface OnRecipeListener {
        void onRecipeSelected(Step step);
    }
}
