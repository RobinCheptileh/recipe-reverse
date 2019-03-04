package com.cognition.android.recipereverse.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.cognition.android.recipereverse.R;
import com.cognition.android.recipereverse.adapters.RecipesAdapter;
import com.cognition.android.recipereverse.models.Recipe;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private CollectionReference recipesCollectionRef;
    private List<Recipe> recipes;
    private List<Recipe> filteredRecipes;
    private List<String> ingredients;
    private List<String> selectedIngredients;
    private RecipesAdapter mRecipesAdapter;
    private ArrayAdapter<String> mIngredientsAdapter;

    @BindView(R.id.search_text)
    AppCompatAutoCompleteTextView searchText;

    @BindView(R.id.selected_ingredients_chip_group)
    ChipGroup selectedIngredientsChipGroup;

    @BindView(R.id.refresh_recipes)
    SwipeRefreshLayout refreshRecipes;

    @BindView(R.id.list_recipes)
    RecyclerView listRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(MainActivity.this);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.recipesCollectionRef = db.collection("recipes");
        this.recipes = new ArrayList<>();
        this.filteredRecipes = new ArrayList<>();
        this.ingredients = new ArrayList<>();
        this.selectedIngredients = new ArrayList<>();

        mRecipesAdapter = new RecipesAdapter(MainActivity.this,
                this.filteredRecipes);
        mIngredientsAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1, this.ingredients);

        searchText.setThreshold(1);
        searchText.setOnItemClickListener((parent, view, position, id) -> {
            String ingredient = mIngredientsAdapter.getItem(position);

            if (!selectedIngredients.contains(ingredient)) {
                selectedIngredients.add(ingredient);
                filterRecipes();

                Chip ingredientChip = new Chip(MainActivity.this);
                ingredientChip.setText(ingredient);
                ingredientChip.setCloseIconVisible(true);

                ingredientChip.setOnCloseIconClickListener(v -> {
                    selectedIngredients.remove(ingredient);
                    selectedIngredientsChipGroup.removeView(ingredientChip);

                    filterRecipes();
                });

                ingredientChip.setOnClickListener(v -> {
                    selectedIngredients.remove(ingredient);
                    selectedIngredientsChipGroup.removeView(ingredientChip);
                    searchText.setText(ingredient);
                    searchText.selectAll();
                });

                selectedIngredientsChipGroup.addView(ingredientChip);
            }

            searchText.setText("");
        });
        searchText.setAdapter(mIngredientsAdapter);

        refreshRecipes.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
                R.color.colorPrimaryDark);
        refreshRecipes.setOnRefreshListener(() -> refreshRecipes.setRefreshing(false));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        listRecipes.setLayoutManager(mLayoutManager);
        listRecipes.setItemAnimator(new DefaultItemAnimator());
        listRecipes.setAdapter(mRecipesAdapter);


        fetchRecipes();
    }

    private void fetchRecipes() {
        refreshRecipes.setRefreshing(true);
        this.recipesCollectionRef
                .get()
                .addOnCompleteListener(MainActivity.this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        int recipesLength = filteredRecipes.size();
                        recipes.clear();
                        filteredRecipes.clear();
                        ingredients.clear();
                        mRecipesAdapter.notifyItemRangeRemoved(0, recipesLength);
                        mIngredientsAdapter.notifyDataSetChanged();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Recipe recipe = document.toObject(Recipe.class);
                            recipe.setId(document.getId());
                            recipes.add(recipe);
                            filteredRecipes.add(recipe);
                            mRecipesAdapter.notifyItemInserted(filteredRecipes.size() - 1);

                            for (String tempIngredient : recipe.getIngredients())
                                if (!ingredients.contains(tempIngredient))
                                    ingredients.add(tempIngredient);
                            mIngredientsAdapter.notifyDataSetChanged();
                        }
                    }

                    refreshRecipes.setRefreshing(false);
                });
    }

    private void filterRecipes() {
        refreshRecipes.setRefreshing(true);

        int recipesLength = filteredRecipes.size();
        filteredRecipes.clear();
        mRecipesAdapter.notifyItemRangeRemoved(0, recipesLength);

        if (selectedIngredients.size() > 0) {
            for (Recipe recipe : this.recipes) {
                if (recipe.getIngredients().containsAll(selectedIngredients))
                    if (!filteredRecipes.contains(recipe)) {
                        filteredRecipes.add(recipe);
                        mRecipesAdapter.notifyItemInserted(filteredRecipes.size() - 1);
                    }
            }
        } else {
            filteredRecipes.addAll(recipes);
            mRecipesAdapter.notifyItemRangeInserted(0, recipes.size());
        }

        refreshRecipes.setRefreshing(false);
        Log.d(TAG, RecipesAdapter.implode(" and ", selectedIngredients));
    }
}
