package com.udacity.bakappies.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.bakappies.R;
import com.udacity.bakappies.model.Ingredient;
import com.udacity.bakappies.model.Recipe;
import com.udacity.bakappies.model.Step;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by radsen on 5/11/17.
 */

public class RecipePartAdapter extends RecyclerView.Adapter<RecipePartAdapter.Part> {

    private static final int PART_INGREDIENT = 1;
    private static final int PART_STEP = 3;
    private static final int SECTION_INGREDIENTS = 0;
    private static final int SECTION_STEPS = 2;

    private final Context context;
    private final ItemClickListener itemClickListener;
    private Recipe recipe;

    public interface ItemClickListener {
        void onClick(int position);
    }

    public RecipePartAdapter(Context context, Recipe recipe, ItemClickListener itemClickListener) {
        this.context = context;
        this.recipe = recipe;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecipePartAdapter.Part onCreateViewHolder(ViewGroup parent, int viewType) {
        Part viewHolder = null;

        View view = null;
        switch (viewType){
            case SECTION_INGREDIENTS:
            case SECTION_STEPS:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_section, parent, false);
                viewHolder = new Section(view);
                break;
            case PART_INGREDIENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_ingredient, parent, false);
                viewHolder = new PartIngredient(view);
                break;
            case PART_STEP:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_step, parent, false);
                viewHolder = new PartStep(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(Part holder, final int position) {
        if(holder instanceof PartIngredient){
            PartIngredient partIngredient = (PartIngredient)holder;

            int ingredientRealPos = position - PART_INGREDIENT;

            int background = 0;
            if(ingredientRealPos == 0){
                background = R.drawable.bkg_table_top;
            } else if (ingredientRealPos == recipe.getIngredients().size() - PART_INGREDIENT){
                background = R.drawable.bkg_table_bottom;
            } else {
                background = R.drawable.bkg_table_row;
            }

            Resources resources = context.getResources();
            Drawable drawable = ResourcesCompat.getDrawable(resources, background, null);
            partIngredient.itemView.setBackground(drawable);

            Ingredient ingredient = recipe.getIngredients().get(ingredientRealPos);

            partIngredient.tvIngredient.setText(ingredient.getIngredient());
            partIngredient.tvQty.setText(String.valueOf(ingredient.getQuantity()));
            partIngredient.tvMeasure.setText(ingredient.getMeasure());

        } else if(holder instanceof Section) {
            Section section = (Section) holder;
            String title = "";
            if(position == SECTION_INGREDIENTS){
                title = context.getString(R.string.txt_title_ingredients);
            } else {
                title = context.getString(R.string.txt_title_steps);
            }
            section.tvSection.setText(title);
        } else if(holder instanceof PartStep) {
            PartStep partStep = (PartStep) holder;
            final int stepRealPosition = position - (recipe.getIngredients().size() + SECTION_STEPS);
            final Step step = recipe.getSteps().get(stepRealPosition);
            String title = String.format(context.getString(R.string.txt_step_title), stepRealPosition + PART_INGREDIENT);
            partStep.tvTitle.setText(title);
            partStep.tvDesc.setText(step.getShortDescription());
            ((PartStep) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onClick(stepRealPosition);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        int rowType = -1;

        if(position == SECTION_INGREDIENTS){
            rowType = SECTION_INGREDIENTS;
        } else if(position > SECTION_INGREDIENTS && position <= recipe.getIngredients().size()) {
            rowType =  PART_INGREDIENT;
        } else if (position > recipe.getIngredients().size() &&
                position < getItemCount() - recipe.getSteps().size()){
            rowType = SECTION_STEPS;
        } else if(position >= getItemCount() - recipe.getSteps().size()){
            rowType = PART_STEP;
        }

        return rowType;
    }

    @Override
    public int getItemCount() {
        int rows = 0;

        if(recipe == null){
            return rows;
        }

        if(recipe.getIngredients().size() > 0){
            rows++;
            rows = rows + recipe.getIngredients().size();
        }

        if(recipe.getSteps().size() > 0){
            rows++;
            rows = rows + recipe.getSteps().size();
        }

        return rows;
    }

    public void swap(Recipe recipe) {
        if (recipe != null){
            this.recipe = recipe;
        }

        notifyDataSetChanged();
    }

    public class Part extends RecyclerView.ViewHolder {

        public Part(View itemView) {
            super(itemView);
        }
    }

    public class PartIngredient extends Part {

        @BindView(R.id.ingredient)
        TextView tvIngredient;

        @BindView(R.id.quantity)
        TextView tvQty;

        @BindView(R.id.measure)
        TextView tvMeasure;

        public PartIngredient(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class PartStep extends Part {

        @BindView(R.id.tv_title)
        TextView tvTitle;

        @BindView(R.id.tv_description)
        TextView tvDesc;

        public PartStep(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }
    }

    public class Section extends Part {

        @BindView(R.id.tv_section_title)
        TextView tvSection;

        public Section(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
