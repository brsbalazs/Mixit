package hu.bme.aut.mixit.recipes_activities;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.room.Room;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.mixit.R;
import hu.bme.aut.mixit.recipe_data.RecipeDatabase;
import hu.bme.aut.mixit.model.Ingredient;
import hu.bme.aut.mixit.model.Recipe;
import hu.bme.aut.mixit.model.RecipeItem;

public class RecipeDetialsViewActivity extends AppCompatActivity {


    long recipeid;
    RecipeDatabase database;

    Recipe recipe;
    List<RecipeItem> recipeItems = new ArrayList<RecipeItem>();
    List<Ingredient> ingredients = new ArrayList<Ingredient>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detials_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Snackbar.make(view, "From now you like this recipe.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ImageView back = findViewById(R.id.arrow_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        recipeid = getIntent().getLongExtra("RECIPE_ID",-1);

        database = Room.databaseBuilder(
                getApplicationContext(),
                RecipeDatabase.class,
                "mixitdatabase1.2"
        ).build();

        loadItemInBackground();





    }

    private void loadItemInBackground() {
        new AsyncTask<Void, Void, Recipe>() {

            @Override
            protected Recipe doInBackground(Void... voids) {

                recipe = database.recipeDao().getById(recipeid);
                recipeItems = database.recipeItemDao().findRecipeItemsForRecipe(recipeid);

                for(int i = 0; i < recipeItems.size(); i++)
                {
                    ingredients.add(database.ingredientDao().findIngredientByID(recipeItems.get(i).getIngredient_id()));
                }
                ingredients = database.ingredientDao().getAll();

                return recipe;
            }

            @Override
            protected void onPostExecute(Recipe items)
            {

                TextView toolbar_text = (TextView) findViewById(R.id.toolbar_text);
                toolbar_text.setText(recipe.getName());

                TextView recipe_name_text = (TextView) findViewById(R.id.recipe_name_text);
                recipe_name_text.setText(recipe.getName());
                initIngredientListView();

            }

        }.execute();
    }

    public void initIngredientListView()
    {
        LinearLayout listOfRows = (LinearLayout)findViewById(R.id.list_of_rows);

        CardView row_item;

        for(int i = 0; i < recipeItems.size(); i++)
        {
            row_item = (CardView) getLayoutInflater().inflate(R.layout.recipe_details_view_ingredient_item, null);

            TextView ingredient_name = (TextView) row_item.findViewById(R.id.ingredient_name_text);
            TextView ingredient_amount = (TextView) row_item.findViewById(R.id.ingredient_amount_text);


            for(int y = 0; y < ingredients.size(); y++)
            {
                if(ingredients.get(y).getId() == recipeItems.get(i).getIngredient_id())
                {
                    ingredient_name.setText(ingredients.get(y).getName());

                }
            }



            int amount = (int)recipeItems.get(i).getAmount();
            ingredient_amount.setText(amount + "ml");
            listOfRows.addView(row_item);

        }

        TextView amounttext = (TextView)findViewById(R.id.recipe_measure_liquid);
        amounttext.setText(recipe.getAmount() + " ml");

        TextView pricetext = (TextView)findViewById(R.id.recipe_price);
        pricetext.setText(recipe.getPrice() + " HUF");

        TextView alctext = (TextView)findViewById(R.id.recipe_percent);
        alctext.setText(recipe.getAlc() + " %");

    }

}
