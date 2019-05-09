package com.cognition.android.recipereverse.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cognition.android.recipereverse.R;
import com.cognition.android.recipereverse.models.Recipe;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {

    private final String TAG = getClass().getSimpleName();

    private Context mContext;
    private List<Recipe> recipes;
    private StorageReference storageReference;

    public RecipesAdapter(Context mContext, List<Recipe> recipes) {
        this.mContext = mContext;
        this.recipes = recipes;

        FirebaseStorage storage = FirebaseStorage.getInstance();
        this.storageReference = storage.getReference();

        setHasStableIds(true);
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_card)
        MaterialCardView itemCard;

        @BindView(R.id.item_image)
        AppCompatImageView itemImage;

        @BindView(R.id.item_name)
        AppCompatTextView itemName;

        @BindView(R.id.item_description)
        AppCompatTextView itemDescription;

        @BindView(R.id.item_url)
        Chip itemUrl;

        @BindView(R.id.item_share)
        Chip itemShare;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(int position) {
            Recipe recipe = recipes.get(position);

            storageReference.child(recipe.getImage())
                    .getDownloadUrl()
                    .addOnCompleteListener((Activity) mContext, task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            GlideApp.with(mContext)
                                    .load(task.getResult().toString())
                                    .placeholder(R.drawable.placeholder)
                                    .centerCrop()
                                    .into(itemImage);
                        }
                    });

            itemName.setText(recipe.getName());
            itemDescription.setText(implode(", ", recipe.getIngredients()));

            Log.d(TAG, implode(" and ", recipe.getIngredients()));

            View.OnClickListener onClickListener = v -> {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(recipe.getUrl()));
                mContext.startActivity(Intent.createChooser(webIntent, recipe.getName()));
            };

            itemView.setOnClickListener(onClickListener);
            itemCard.setOnClickListener(onClickListener);
            itemImage.setOnClickListener(onClickListener);
            itemName.setOnClickListener(onClickListener);
            itemDescription.setOnClickListener(onClickListener);
            itemUrl.setOnClickListener(onClickListener);
            itemShare.setOnClickListener(v -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        String.format(Locale.getDefault(),
                                mContext.getString(R.string.share_string),
                                recipe.getName(), recipe.getUrl()));
                sendIntent.setType("text/plain");
                mContext.startActivity(Intent.createChooser(sendIntent, recipe.getName()));
            });
        }
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_result, parent, false);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecipeViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return this.recipes.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static String implode(String delimiter, List<String> elements) {
        StringBuilder builder = new StringBuilder();
        String prefix = "";
        for (String s : elements) {
            builder.append(prefix);
            prefix = delimiter;
            builder.append(s);
        }
        return builder.toString();
    }
}
