package com.udacity.bakappies.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
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
            partIngredient.glIngredientTable.removeAllViews();
            for(Ingredient ingredient : recipe.getIngredients()){

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);

                LinearLayout tableRow = new LinearLayout(context);
                tableRow.setOrientation(LinearLayout.HORIZONTAL);
                tableRow.setLayoutParams(params);
                tableRow.setWeightSum(1.0f);

                params.weight = 0.5f;
                String strIngredient = String.format(context.getString(R.string.format_ingredient),
                        ingredient.getIngredient());
                TextView tvIngredient = createColumnTextView(strIngredient, params, false);
                tableRow.addView(tvIngredient);

                params.weight = 0.2f;
                TextView tvQuantity = createColumnTextView(String.valueOf(ingredient.getQuantity()),
                        params, true);
                tableRow.addView(tvQuantity);

                params.weight = 0.3f;
                TextView tvMeasure = createColumnTextView(ingredient.getMeasure(), params, false);
                tableRow.addView(tvMeasure);

                partIngredient.glIngredientTable.addView(tableRow);
            }
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
            final int stepRealPosition = position - PART_STEP;
            final Step step = recipe.getSteps().get(stepRealPosition);
            String title = String.format(context.getString(R.string.txt_step_title), position - SECTION_STEPS);
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

    private TextView createColumnTextView(String text, LinearLayout.LayoutParams params,
                                          boolean isAlignedToRight) {

        TextView textView = new TextView(context);
        textView.setText(text);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Small);
        } else {
            textView.setTextAppearance(context ,
                    android.R.style.TextAppearance_DeviceDefault_Small);
        }

        if(isAlignedToRight){
            Resources resources =  context.getResources();
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8.0f,
                    resources.getDisplayMetrics());
            params.setMarginEnd(px);
            textView.setGravity(Gravity.RIGHT);
        }

        textView.setLayoutParams(params);

        return textView;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position){
            case SECTION_INGREDIENTS:
                return SECTION_INGREDIENTS;
            case PART_INGREDIENT:
                return  PART_INGREDIENT;
            case SECTION_STEPS:
                return SECTION_STEPS;
            default:
                return PART_STEP;
        }
    }

    @Override
    public int getItemCount() {
        int parts = 0;

        if(recipe == null){
            return parts;
        }

        if(recipe.getIngredients().size() > 0){
            parts++;
        }

        if(recipe.getSteps().size() > 0){
            parts = parts + recipe.getSteps().size();
        }

        return parts;
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

        @BindView(R.id.gl_ingredient_table)
        GridLayout glIngredientTable;

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
