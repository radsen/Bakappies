package com.udacity.bakappies.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.bakappies.R;
import com.udacity.bakappies.activity.RecipeActivity;
import com.udacity.bakappies.adapter.RecipeAdapter;
import com.udacity.bakappies.common.BakappiesConstants;
import com.udacity.bakappies.model.Recipe;
import com.udacity.bakappies.network.SyncUtils;
import com.udacity.bakappies.util.RecipeLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by radsen on 5/8/17.
 */

public class FragmentMenu extends BaseFragment implements RecipeAdapter.ClickListener,
        BakappiesConstants, LoaderManager.LoaderCallbacks<List<Recipe>> {

    private static final String TAG = FragmentMenu.class.getSimpleName();
    private static final int RECIPE_LOADER = 4001;

    @BindView(R.id.rv_recipes)
    RecyclerView rvRecipes;

    private Unbinder unbinder;
    private RecipeAdapter mAdapter;
    private IntentFilter mIntentFilter;

    private LoaderManager.LoaderCallbacks<List<Recipe>> cbLoader;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BakappiesConstants.ACTION_RECIPES)){
                getActivity().getSupportLoaderManager()
                        .restartLoader(RECIPE_LOADER, null, cbLoader)
                        .forceLoad();
            }
        }
    };

    public static FragmentMenu newInstance() {
        FragmentMenu fragmentMenu = new FragmentMenu();
        return fragmentMenu;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cbLoader = this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rvRecipes.setHasFixedSize(true);
        RecyclerView.LayoutManager mLinearLayout = new LinearLayoutManager(getContext());
        rvRecipes.setLayoutManager(mLinearLayout);
        mAdapter = new RecipeAdapter(getContext(), null, this);
        rvRecipes.setAdapter(mAdapter);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ACTION_RECIPES);

        if(SyncUtils.isInitialized()){
            getActivity().getSupportLoaderManager()
                    .restartLoader(RECIPE_LOADER, null, cbLoader)
                    .forceLoad();
        } else {
            SyncUtils.initialize(getContext());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, mIntentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(Recipe recipe, int position) {
        Log.d(TAG, "Position: " + position);
        Intent intent = new Intent(getContext(), RecipeActivity.class);
        intent.putExtra(RECIPE_KEY, recipe);
        startActivity(intent);
    }

    @Override
    public Loader<List<Recipe>> onCreateLoader(int id, Bundle args) {
        RecipeLoader loader = null;

        switch (id){
            case RECIPE_LOADER:
                loader = new RecipeLoader(getContext());
                break;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Recipe>> loader, List<Recipe> data) {
        mAdapter.swap(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Recipe>> loader) {
        mAdapter.swap(null);
    }
}
