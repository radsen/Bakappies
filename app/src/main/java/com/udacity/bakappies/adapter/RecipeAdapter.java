package com.udacity.bakappies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.bakappies.R;
import com.udacity.bakappies.model.Recipe;
import com.udacity.bakappies.util.BindingUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by radsen on 5/10/17.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final ClickListener listener;
    private Context context;
    private List<Recipe> recipeList;

    public interface ClickListener {
        void onItemClick(Recipe recipe, int position);
    }

    public RecipeAdapter(Context context, List<Recipe> recipeList, ClickListener listener){
        this.context = context;
        this.recipeList = recipeList;
        this.listener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Recipe recipe = recipeList.get(position);
        BindingUtils.loadImage(holder.ivRecipe, recipe.getImage(), R.drawable.ic_bake_default);
        holder.tvName.setText(recipe.getName());
        String format = context.getString(R.string.txt_servings);
        holder.tvServings.setText(String.format(format, recipe.getServings()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(recipe, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(recipeList == null)
            return 0;

        return recipeList.size();
    }

    public void swap(List<Recipe> recipes) {
        if(recipes != null){
            recipeList = recipes;
        }

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_recipe)
        ImageView ivRecipe;

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.tv_servings)
        TextView tvServings;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
