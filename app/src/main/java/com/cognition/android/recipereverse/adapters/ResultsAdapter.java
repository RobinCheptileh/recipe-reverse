package com.cognition.android.recipereverse.adapters;

import android.view.View;

import com.cognition.android.recipereverse.R;
import com.cognition.android.recipereverse.models.Recipe;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultsAdapter {
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

        public void bind(Recipe recipe, int position) {

        }
    }
}
