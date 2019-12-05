package hu.bme.aut.mixit.filter_activities;

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

public class FilterActivity extends AppCompatActivity {


    RecipeDatabase database;
    List<Ingredient> ingredients = new ArrayList<Ingredient>();
    List<Recipe> recipes = new ArrayList<Recipe>();
    List<RecipeItem> recipe_items = new ArrayList<RecipeItem>();


    FloatingActionButton fab;
    FloatingActionButton readybtn;

    Snackbar currentlyShowingSnackbar;

    View view;


    /**Inits the activity view, sets onclick listeners, and calls loadItemsInBackgorund*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        database = Room.databaseBuilder(
                getApplicationContext(),
                RecipeDatabase.class,
                "mixitdatabase1.2"
        ).build();

        ImageView back_btn = (ImageView) findViewById(R.id.filter_back_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { onBackPressed(); }
        });

        loadItemsInBackground();

    }


    /**Loads images, recipe items, and recipes and sets ready btn onclick listener*/
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

                fab = (FloatingActionButton) findViewById(R.id.fab);
                readybtn = (FloatingActionButton) findViewById(R.id.ready);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                  createContraintCard(view);

                    }
                });

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


    /**Inflate-s a constraint card and setts spinner list + onclick listeners*/
    private void createContraintCard(final View view) {

        final LinearLayout list_of_rows = (LinearLayout) findViewById(R.id.filter_list_of_rows);

        currentlyShowingSnackbar = Snackbar.make(view, "New constraint has been added.", Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        currentlyShowingSnackbar.show();


        final CardView row_item = (CardView) getLayoutInflater().inflate(R.layout.filter_constraint_list_element, null);

        //setting ingredient pinner items
        Spinner ingredient_spinner = row_item.findViewById(R.id.choose_ingredient_spinner);

        List<String> spinnerItems = new ArrayList<String>();

        spinnerItems.add("Choose ingredient...");

        for(int i = 0; i < ingredients.size();i++)
        {
            spinnerItems.add(ingredients.get(i).getName());
        }

        row_item.findViewById(R.id.delete_constraint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_of_rows.removeView(row_item);
                currentlyShowingSnackbar = Snackbar.make(view, "Constraint has been deleted successfully.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                currentlyShowingSnackbar.show();
            }
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(row_item.getContext(), R.layout.spinner_item, spinnerItems);
        ingredient_spinner.setAdapter(adapter);


        //creating constraint spinner

        Spinner constraint_spinner = row_item.findViewById(R.id.choose_constraint_spinner);

        List<String> constraintSpinnerItems = new ArrayList<String>();

        constraintSpinnerItems.add("Choose type...");
        constraintSpinnerItems.add("Contains");
        constraintSpinnerItems.add("Contains no");

        ArrayAdapter<String> constraint_adapter = new ArrayAdapter<String>(row_item.getContext(), R.layout.spinner_item, constraintSpinnerItems);
        constraint_spinner.setAdapter(constraint_adapter);

        list_of_rows.addView(row_item);
    }


    /**Evaluates the constraints, filters recipes and finally displays the result + starts animations*/
    private void changeViewToRecipeList() {

        //getting the linearlayout of filter_view
        final LinearLayout constraint_list = (LinearLayout) findViewById(R.id.filter_list_of_rows);

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

                //filtering recipes for the usable ingredients
                final List<Recipe> matching_recipes = new ArrayList<Recipe>();

                for(int j = 0; j < recipes.size(); j++)
                {

                    List<RecipeItem> recipeItemsOfCurrentRecipe = new ArrayList<RecipeItem>();

                    //finding the recipeitems for the current recipe
                    for(int r = 0; r < recipe_items.size(); r++)
                    {
                        if(recipes.get(j).getId() == recipe_items.get(r).getRecipeId())
                        {
                            recipeItemsOfCurrentRecipe.add(recipe_items.get(r));
                        }
                    }


                    //finding ingredients for the recipes

                    List<String> ingredientsNamesOfCurrentRecipe = new ArrayList<>();

                    for(int y = 0; y < recipeItemsOfCurrentRecipe.size(); y++)
                    {
                        for(int o = 0; o < ingredients.size(); o++)
                        {
                            if(recipeItemsOfCurrentRecipe.get(y).getIngredient_id() == ingredients.get(o).getId())
                            {
                                ingredientsNamesOfCurrentRecipe.add(ingredients.get(o).getName());
                            }
                        }
                    }
                    //items for the current recipe ready


                    //evaluating every contraints on the recipes


                    boolean everyConstraintTrue = true;

                    for(int i = 0; i < constraint_list.getChildCount(); i++)
                    {
                        Spinner ingredient_spinner = (Spinner)constraint_list.getChildAt(i).findViewById(R.id.choose_ingredient_spinner);
                        String ingredient_name = ingredient_spinner.getSelectedItem().toString();

                        Spinner constraint_spinner = (Spinner)constraint_list.getChildAt(i).findViewById(R.id.choose_constraint_spinner);
                        String  constraint_type = constraint_spinner.getSelectedItem().toString();



                        //if the constraint type is positive
                        if(constraint_type.equals("Contains") && !(ingredientsNamesOfCurrentRecipe.contains(ingredient_name)))
                        {
                            everyConstraintTrue = false;

                        }
                        //if the constraint type is negative
                        if(constraint_type.equals("Contains no") && ingredientsNamesOfCurrentRecipe.contains(ingredient_name))
                        {
                          everyConstraintTrue = false;
                        }
                    }

                    if(everyConstraintTrue) matching_recipes.add(recipes.get(j));

                }


                //if any recipes found
                if(matching_recipes.size() > 0)
                {

                    System.out.println("Found Recipes: ");
                    for(int i = 0; i < matching_recipes.size(); i++)
                    {
                        System.out.println(matching_recipes.get(i).getName());
                    }

                    //remove contraints to display recipes
                    constraint_list.removeAllViews();

                    //init the list with recipe items
                    createAndAddRecipeCards(matching_recipes, constraint_list);

                    currentlyShowingSnackbar = Snackbar.make(findViewById(R.id.filter_view)
                            , "Recipes filtered successfully.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    currentlyShowingSnackbar.show();

                }
                else
                {

                    //floating action buttons -  zoom in again and show snackbar with informations
                    Animation zoom_in_animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_animation);

                    readybtn.setEnabled(true);
                    fab.setEnabled(true);

                    readybtn.startAnimation(zoom_in_animation);
                    fab.startAnimation(zoom_in_animation);

                    currentlyShowingSnackbar = Snackbar.make(getWindow().getDecorView().findViewById(R.id.filter_view)
                            , "Filtering by the specified constrains did not result any kind of recipes.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    currentlyShowingSnackbar.show();

                }


                Animation zoom_in_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_animation);

                constraint_list.startAnimation(zoom_in_animation);
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

        //disable buttons for the time of animations - will be enabled agan in case of no result
        readybtn.setEnabled(false);
        fab.setEnabled(false);

        constraint_list.startAnimation(zoom_out_animation);
        readybtn.startAnimation(zoom_out_ready_animation);
        fab.startAnimation(zoom_out_fab_animation);

    }

    /**From the available recipes inflates recipe cards and fills the ingredient list with the result*/
    public void createAndAddRecipeCards(final List<Recipe> available_recipes, final LinearLayout constraint_list)
    {
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

            constraint_list.addView(row_item);

        }


    }


    /**Hides the currently showing snackbar*/
    void hideSnackbar(){
        if(this.currentlyShowingSnackbar !=null && this.currentlyShowingSnackbar.isShown()){
            this.currentlyShowingSnackbar.dismiss();
        }
    }

    /**Return image resource for the current racipe by the recipes alcohol attribute*/
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
