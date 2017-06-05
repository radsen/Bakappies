package com.udacity.bakappies;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;

import com.udacity.bakappies.activity.RecipeActivity;
import com.udacity.bakappies.common.BakappiesConstants;
import com.udacity.bakappies.model.Recipe;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.udacity.bakappies.util.RecyclerViewItemCountAssertion.withItemCount;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Created by radsen on 6/2/17.
 */

public class RecipeActivityTest {

    private static final String TAG = RecipeActivityTest.class.getSimpleName();

    private static final int ID = 1;
    private static final String RECIPE_NAME = "Any Name";
    private static final int SERVINGS = 8;
    private static final String IMG_URL = "http://domain.com/img.jpg";

    @Rule
    public IntentsTestRule<RecipeActivity> mIntentTestRule =
            new IntentsTestRule<>(RecipeActivity.class, true, false);

    @Before
    public void stubAllExternalIntents() {

        Intent resultData = new Intent();
        resultData.putExtra(BakappiesConstants.RECIPE_KEY,
                new Recipe(ID, RECIPE_NAME, SERVINGS, IMG_URL));

        mIntentTestRule.launchActivity(resultData);
    }

    @Test
    public void checkRecipeHasContent(){
        onView(withId(R.id.rv_recipe_parts)).check(withItemCount(greaterThan(0)));
    }

    @Test
    public void clickStepOpensDetail(){

        onView(ViewMatchers.withId(R.id.rv_recipe_parts))
                .perform(RecyclerViewActions.actionOnItemAtPosition(13, click()));

        onView(withId(R.id.vp_steps)).check(matches(isDisplayed()));

    }
}
