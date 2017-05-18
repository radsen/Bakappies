package com.udacity.bakappies.network;

import com.udacity.bakappies.model.Recipe;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by radsen on 5/10/17.
 */

public interface BakingAppService {

    //@GET("topher/2017/March/58d1537b_baking/baking.json")
    @GET("android-baking-app-json")
    Call<List<Recipe>> fetchRecipes();
}
