package hu.bme.aut.mixit.mixing_activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.room.Room;

import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.mixit.R;
import hu.bme.aut.mixit.recipe_data.RecipeDatabase;
import hu.bme.aut.mixit.model.Ingredient;
import hu.bme.aut.mixit.model.Recipe;
import hu.bme.aut.mixit.model.RecipeItem;
import hu.bme.aut.mixit.recipes_activities.RecipeDetialsViewActivity;

public class MixingActivity extends AppCompatActivity {

    RecipeDatabase database;
    List<Ingredient> ingredients = new ArrayList<Ingredient>();
    List<Recipe> recipes = new ArrayList<Recipe>();
    List<RecipeItem> recipe_items = new ArrayList<RecipeItem>();


    FloatingActionButton fab;
    FloatingActionButton readybtn;

    Snackbar currentlyShowingSnackbar;

    View view;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mixing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = Room.databaseBuilder(
                getApplicationContext(),
                RecipeDatabase.class,
                "mixitdatabase1.2"
        ).build();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView back_btn = (ImageView) findViewById(R.id.mixing_back_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { onBackPressed(); }
        });

        loadItemsInBackground();

    }


    private void loadItemsInBackground() {
        new AsyncTask<Void, Void, List<Ingredient>>() {

            @Override
            protected List<Ingredient> doInBackground(Void... voids) {

                ingredients = database.ingredientDao().getAll();
                recipes = database.recipeDao().getAll();
                recipe_items = database.recipeItemDao().getAllRecipeItems();

                return ingredients;
            }


            @Override
            protected void onPostExecute(List<Ingredient> items) {

                final LinearLayout list_of_rows = (LinearLayout) findViewById(R.id.mixing_list_of_rows);



                fab = (FloatingActionButton) findViewById(R.id.fab);
                readybtn = (FloatingActionButton) findViewById(R.id.ready);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        currentlyShowingSnackbar = Snackbar.make(view, "New ingredient has been added.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        currentlyShowingSnackbar.show();


                        final CardView row_item = (CardView) getLayoutInflater().inflate(R.layout.mixing_ingredient_list_element, null);

                        Spinner spinner = row_item.findViewById(R.id.choose_ingredient_spinner);

                        List<String> spinnerItems = new ArrayList<String>();

                        spinnerItems.add("Choose item");

                        for(int i = 0; i < ingredients.size();i++)
                        {
                            spinnerItems.add(ingredients.get(i).getName());
                        }

                        row_item.findViewById(R.id.delete_ingredient).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                list_of_rows.removeView(row_item);
                            }
                        });


                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(row_item.getContext(), R.layout.spinner_item, spinnerItems);
                        spinner.setAdapter(adapter);

                        list_of_rows.addView(row_item);

                    }
                });

                FloatingActionButton readybtn = (FloatingActionButton) findViewById(R.id.ready);
                readybtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        changeViewToRecipeList();
                        hideSnackbar();

                    }
                });

            }


        }.execute();


    }

    private void changeViewToRecipeList()
    {
        final LinearLayout ingredient_list = (LinearLayout) findViewById(R.id.mixing_list_of_rows);



        Animation zoom_out_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out_animation);
        Animation zoom_out_fab_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out_floating_animation);
        Animation zoom_out_ready_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out_floating_animation);



        zoom_out_animation.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0)
            {
                //clear the list and fill it with the recipes

                List<Ingredient> available_ingredients = new ArrayList<Ingredient>();


                for(int i = 0; i < ingredient_list.getChildCount(); i++)
                {
                    Spinner spinner = (Spinner)ingredient_list.getChildAt(i).findViewById(R.id.choose_ingredient_spinner);

                    String ingredient_name = spinner.getSelectedItem().toString();

                    for(int y = 0; y < ingredients.size(); y++)
                    {
                        if(ingredient_name.equals(ingredients.get(y).getName()))
                        {
                            available_ingredients.add(ingredients.get(y));
                        }
                    }
                }
                //available ingredients are ready

                //filtering recipes for the available ingredients

                final List<Recipe> available_recipes = new ArrayList<Recipe>();

                for(int j = 0; j < recipes.size(); j++)
                {

                    List<RecipeItem> recipeItemsOfCurrentRecipe = new ArrayList<RecipeItem>();

                    for(int r = 0; r < recipe_items.size(); r++)
                    {
                        if(recipes.get(j).getId() == recipe_items.get(r).getRecipeId())
                        {
                            recipeItemsOfCurrentRecipe.add(recipe_items.get(r));
                        }
                    }
                    //megvannak a recipe itemek amik kellenek

                    boolean every_ingredient_is_available = true;


                    for(int i = 0; i < recipeItemsOfCurrentRecipe.size(); i++)
                    {
                        boolean ingredientIsAvailable = false;

                        for(int y = 0; y < available_ingredients.size(); y++)
                        {
                            if(available_ingredients.get(y).getId() == recipeItemsOfCurrentRecipe.get(i).getIngredient_id())
                            {
                                ingredientIsAvailable = true;
                            }
                        }


                        if(ingredientIsAvailable == false) every_ingredient_is_available = false;

                    }


                    if(every_ingredient_is_available){ available_recipes.add(recipes.get(j));}
                }

                System.out.println("Found Recipes: ");
                for(int i = 0; i < available_recipes.size(); i++)
                {
                    System.out.println(available_recipes.get(i).getName());
                }


                //if any recipes found
                if(available_recipes.size() > 0)
                {
                    ingredient_list.removeAllViews();

                    //init the list with recipe items


                    for(int i = 0; i < available_recipes.size(); i++)
                    {

                        final CardView row_item = (CardView) getLayoutInflater().inflate(R.layout.recipe_list_item, null);

                        final int index = i;


                        row_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(getApplicationContext(), RecipeDetialsViewActivity.class);
                                intent.putExtra("RECIPE_ID", available_recipes.get(index).getId());
                                startActivity(intent);

                            }
                        });

                        //setting name
                        TextView recipe_listitem_name_text = (TextView) row_item.findViewById(R.id.RecipeListNameText);
                        recipe_listitem_name_text.setText(available_recipes.get(i).getName());

                        //setting image
                        ImageView image = (ImageView) row_item.findViewById(R.id.RecipeListItemImage);
                        image.setImageResource(getImageResource(available_recipes.get(i)));

                        image.setOnClickListener(null);

                        //setting action button
                        ImageView button = (ImageView)row_item.findViewById(R.id.RecipeListRemoveButton);
                        @DrawableRes int ret;
                        ret = R.drawable.eye_icon;
                        button.setImageResource(ret);


                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(getApplicationContext(), RecipeDetialsViewActivity.class);
                                intent.putExtra("RECIPE_ID", available_recipes.get(index).getId());
                                startActivity(intent);
                            }
                        });

                        ImageView dollarsign = (ImageView)row_item.findViewById(R.id.price_icon);
                        dollarsign.setImageResource(R.drawable.dollar_sign_grey);


                        ImageView measureicon = (ImageView)row_item.findViewById(R.id.alcohol_icon);
                        measureicon.setImageResource(R.drawable.measure_icon_grey);

                        TextView alcholtext = (TextView) row_item.findViewById(R.id.RecipeListAlcText);
                        double alc = available_recipes.get(i).getAlc();
                        alcholtext.setText(alc+" %");

                        TextView pricetext = (TextView) row_item.findViewById(R.id.RecipeListPriceText);
                        int price = available_recipes.get(i).getPrice();
                        int amountml = available_recipes.get(i).getAmount();
                        pricetext.setText(price+" HUF/ " + amountml + " ml");

                        ingredient_list.addView(row_item);


                    }
                }
                else
                    {

                        Animation zoom_in_animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_animation);

                        readybtn.setEnabled(true);
                        fab.setEnabled(true);

                        readybtn.startAnimation(zoom_in_animation);
                        fab.startAnimation(zoom_in_animation);

                        Snackbar.make(getWindow().getDecorView().findViewById(R.id.mixing_view)
                                , "You did not chose enought ingredients for any kind of recipe.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();


                    }






                Animation zoom_in_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_animation);

                ingredient_list.startAnimation(zoom_in_animation);




            }
        });

        zoom_out_fab_animation.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            @SuppressLint("RestrictedApi")
            public void onAnimationEnd(Animation arg0)
            {
                fab.setVisibility(View.GONE);
            }
        });

        zoom_out_ready_animation.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            @SuppressLint("RestrictedApi")
            public void onAnimationEnd(Animation arg0)
            {


                readybtn.setVisibility(View.GONE);
            }
        });

        readybtn.setEnabled(false);
        fab.setEnabled(false);

        ingredient_list.startAnimation(zoom_out_animation);
        readybtn.startAnimation(zoom_out_ready_animation);
        fab.startAnimation(zoom_out_fab_animation);

    }

    void hideSnackbar(){
        if(this.currentlyShowingSnackbar !=null && this.currentlyShowingSnackbar.isShown()){
            this.currentlyShowingSnackbar.dismiss();
        }
    }

    private @DrawableRes
    int getImageResource(Recipe item) {


        @DrawableRes int ret;
        double percent = item.getAlc();


        //icons from https://www.flaticon.com/authors/dinosoftlabs from www.flaticon.com
        if(percent == 0.0) ret = R.drawable.drink_non;
        else if (percent > 0.0 && percent <=15.0) ret = R.drawable.drink_alcoholic;
        else if (percent > 15.0 && percent <= 50.0) ret = R.drawable.drink_strong;
        else if (percent > 50.0) ret = R.drawable.drink_xstrong;
        else ret = 0;

        return ret;
    }
}
