package com.cognition.android.recipereverse.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultViewHolder> {

    private Context mContext;
    private List<Recipe> recipes;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public ResultsAdapter(Context mContext, List<Recipe> recipes) {
        this.mContext = mContext;
        this.recipes = recipes;

        this.storage = FirebaseStorage.getInstance();
        this.storageReference = this.storage.getReference();
    }

    class ResultViewHolder extends RecyclerView.ViewHolder {

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

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
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
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_result, parent, false);

        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ResultViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return this.recipes.size();
    }


    private String implode(String delimeter, List<String> elements) {
        StringBuilder builder = new StringBuilder();
        for (String s : elements)
            builder.append(s).append(delimeter);
        builder.delete(-1, (-1 - delimeter.length()));
        return builder.toString();
    }
}
