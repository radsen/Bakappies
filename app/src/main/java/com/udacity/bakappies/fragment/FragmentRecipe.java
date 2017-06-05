package com.udacity.bakappies.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
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

    public static final String TAG = FragmentRecipe.class.getSimpleName();

    @BindView(R.id.rv_recipe_parts)
    RecyclerView rvRecipeParts;

    private Unbinder unbinder;

    private RecipeAsyncQueryHandler recipeQueryHandler;
    private Recipe recipe;
    private RecipePartAdapter mAdapter;
    private OnRecipeListener recipeListener;
    private boolean isTablet;
    private boolean checkIngredients;
    private boolean checkSteps;

    private int mScrollPosition;
    private int mSelPos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
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
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        if(savedInstanceState != null){
            mScrollPosition = savedInstanceState.getInt(BakappiesConstants.SCROLLED_POSITION, 0);
            mSelPos = savedInstanceState.getInt(BakappiesConstants.SELECTED_POSITION,
                    RecipePartAdapter.NO_SELECTION);
        } else {
            mSelPos = RecipePartAdapter.NO_SELECTION;
        }

        isTablet = getResources().getBoolean(R.bool.isTablet);

        recipe = getArguments().getParcelable(BakappiesConstants.RECIPE_KEY);

        rvRecipeParts.setHasFixedSize(true);
        RecyclerView.LayoutManager mLinearLayout = new LinearLayoutManager(getContext());
        rvRecipeParts.setLayoutManager(mLinearLayout);
        setScrollChangedListener();
        mAdapter = new RecipePartAdapter(getContext(), null, this);
        rvRecipeParts.setAdapter(mAdapter);

        showProgress();

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

    private void setScrollChangedListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rvRecipeParts.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    mSelPos = RecipePartAdapter.NO_SELECTION;
                }
            });
        } else {
            rvRecipeParts.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    mSelPos = RecipePartAdapter.NO_SELECTION;
                }
            });
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        mScrollPosition = ((LinearLayoutManager) rvRecipeParts.getLayoutManager()).findFirstVisibleItemPosition();
        outState.putInt(BakappiesConstants.SCROLLED_POSITION, mScrollPosition);
        outState.putInt(BakappiesConstants.SELECTED_POSITION, mSelPos);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onSaveInstanceState");
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
                recipe.setIngredients(cursor);
                checkIngredients = true;
                break;
            case RecipeAsyncQueryHandler.STEP_TOKEN:
                recipe.setSteps(cursor);
                mAdapter.swap(recipe);
                checkSteps = true;
                if (isTablet) {
                    int position = (mSelPos != RecipePartAdapter.NO_SELECTION) ? mSelPos : 0;
                    recipeListener.onRecipeSelected(recipe.getSteps().get(position));
                    rvRecipeParts.scrollToPosition(mScrollPosition);
                    int selPos = (mSelPos != RecipePartAdapter.NO_SELECTION) ? mSelPos :
                            RecipePartAdapter.NO_SELECTION;
                    mAdapter.setSelectedStep(selPos);
                }
                break;
        }

        if(checkIngredients && checkSteps){
            hideProgress();
        }
    }

    @Override
    public void onClick(int position) {
        mSelPos = position;

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
