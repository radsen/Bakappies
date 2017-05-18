package com.udacity.bakappies.activity;

import android.os.Bundle;

import com.udacity.bakappies.R;
import com.udacity.bakappies.fragment.FragmentMenu;

public class MainActivity extends BaseActivity {

    private FragmentMenu fragmentRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentRecipe = FragmentMenu.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragmentRecipe)
                .commit();
    }

}
