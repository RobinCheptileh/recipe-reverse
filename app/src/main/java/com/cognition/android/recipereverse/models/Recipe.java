package com.cognition.android.recipereverse.models;

import java.util.List;

public class Recipe {
    private String id;
    private String name;
    private String url;
    private String image;
    private List<String> ingredients;

    public Recipe() {
    }

    public Recipe(String name, String url, String image, List<String> ingredients) {
        this.name = name;
        this.url = url;
        this.image = image;
        this.ingredients = ingredients;
    }

    public Recipe(String id, String name, String url, String image, List<String> ingredients) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.image = image;
        this.ingredients = ingredients;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
