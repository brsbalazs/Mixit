package hu.bme.aut.mixit.init_activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.mixit.R;
import hu.bme.aut.mixit.menu.MenuActivity;
import hu.bme.aut.mixit.model.Ingredient;
import hu.bme.aut.mixit.model.Recipe;
import hu.bme.aut.mixit.model.RecipeItem;
import hu.bme.aut.mixit.recipe_data.RecipeDatabase;
import hu.bme.aut.mixit.settings_activities.SettingsDialogFragment;

public class InitApplicationSettingsActivity extends AppCompatActivity {


    FloatingActionButton fab;

    RecipeDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_application_settings);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        NumberPicker weightNumberPicker = (NumberPicker) findViewById(R.id.initWeightNumberPicker);
        NumberPicker heightNumberPicker = (NumberPicker) findViewById(R.id.initHeightNumberPicker);

        weightNumberPicker.setMinValue(0);
        weightNumberPicker.setMaxValue(200);
        weightNumberPicker.setValue(70);

        heightNumberPicker.setMaxValue(230);
        heightNumberPicker.setMinValue(100);
        heightNumberPicker.setValue(160);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SettingsDialogFragment().show(getSupportFragmentManager(), SettingsDialogFragment.TAG);

            }
        });


        ImageView button = (ImageView) findViewById(R.id.submit_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onSubmit();
            }
        });


    }

    public void onSubmit()
    {
        if(isValid())
        {

            EditText nameEditText = (EditText) findViewById(R.id.initNameEditText);
            EditText ageEditText = (EditText) findViewById(R.id.initAgeEditText);

            NumberPicker initWeightNumberPicker = (NumberPicker) findViewById(R.id.initWeightNumberPicker);
            NumberPicker initHeightNumberPicker = (NumberPicker) findViewById(R.id.initHeightNumberPicker);


            RadioButton initGenderMale = (RadioButton) findViewById(R.id.initGenderMaleRadio);
            RadioButton initGenderFemale = (RadioButton) findViewById(R.id.initGenderFemaleRadio);


            SharedPreferences sharedPrefs = getSharedPreferences("user_settings_sp1.0", MODE_PRIVATE);
            SharedPreferences.Editor ed;

            if(!sharedPrefs.contains("initialized")){

                ed = sharedPrefs.edit();

                //Indicate that the default shared prefs have been set
                ed.putBoolean("initialized", true);

                //Set some default shared pref
                ed.putString("name", nameEditText.getText().toString());
                ed.putString("age", ageEditText.getText().toString());
                ed.putInt("weight", initWeightNumberPicker.getValue());
                ed.putInt("height", initHeightNumberPicker.getValue());

                if(initGenderFemale.isChecked()) ed.putString("gender", "female");
                else if(initGenderMale.isChecked()) ed.putString("gender", "male");

                ed.commit();


                loadDefaultData();


            }

            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
            startActivity(intent);

        }

    }

    public boolean isValid()
    {
        EditText nameEditText = (EditText) findViewById(R.id.initNameEditText);
        if(nameEditText.getText().length() == 0) {
            showSnackbarWithMessage("Name is empty. Please fill with your nickname.","", null);
            return false;
        }


        EditText ageEditText = (EditText) findViewById(R.id.initAgeEditText);
        if(ageEditText.getText().length() == 0) {
            showSnackbarWithMessage("Age is empty. Please fill with your age.", "", null);
            return false;
        }

        NumberPicker initWeightNumberPicker = (NumberPicker) findViewById(R.id.initWeightNumberPicker);
        NumberPicker initHeightNumberPicker = (NumberPicker) findViewById(R.id.initHeightNumberPicker);


        RadioButton initGenderMale = (RadioButton) findViewById(R.id.initGenderMaleRadio);
        RadioButton initGenderFemale = (RadioButton) findViewById(R.id.initGenderFemaleRadio);

        if(!initGenderFemale.isChecked() && !initGenderMale.isChecked())
        {

            showSnackbarWithMessage("Please also select your gender.", "", null);
            return false;
        }

        return true;

    }





    public void showSnackbarWithMessage(String message, String actionName, View.OnClickListener listener)
    {

        Snackbar.make(findViewById(R.id.fab)
                , message, Snackbar.LENGTH_LONG)
                .setAction(actionName, listener).show();
    }

    private void loadDefaultData()
    {
        database = Room.databaseBuilder(
                getApplicationContext(),
                RecipeDatabase.class,
                "mixitdatabase1.2"
        ).build();


        //creating and inserting ingredients
        Ingredient i1 = new Ingredient("Cola", 0.0, 200);
        Ingredient i2 = new Ingredient("Fanta", 0.0, 200);
        Ingredient i3 = new Ingredient("Jack Daniel's Black label whiskey", 40.0, 10000);
        Ingredient i4 = new Ingredient("Ballantine's whiskey", 40.0, 7000);
        Ingredient i5 = new Ingredient("Captain Morgan Dark rum", 40.0, 8000);
        Ingredient i6 = new Ingredient("Captain Morgan Spiced Gold rum", 35.0, 7500);
        Ingredient i7 = new Ingredient("Lemon juice", 0.0, 300);
        Ingredient i8 = new Ingredient("Orange juice", 0.0, 500);
        Ingredient i9 = new Ingredient("Bacardi Carta Blanca", 37.0, 7500);
        Ingredient i10 = new Ingredient("Lime juice", 0.0, 300);
        Ingredient i11 = new Ingredient("Soda", 0.0, 100);
        Ingredient i12 = new Ingredient("Gin Bombay Sapphire", 40.0, 9000);
        Ingredient i13 = new Ingredient("Tequila Sierra Silver", 38.0, 7000);
        Ingredient i14 = new Ingredient("Absolut vodka", 40.0, 7000);
        Ingredient i15 = new Ingredient("Absinthe Tunel Black", 80.0, 12000);
        Ingredient i16 = new Ingredient("Sprite", 0.0, 200);


        List<Ingredient> ingredientList = new ArrayList<Ingredient>();
        ingredientList.add(i1);
        ingredientList.add(i2);
        ingredientList.add(i3);
        ingredientList.add(i4);
        ingredientList.add(i5);
        ingredientList.add(i6);
        ingredientList.add(i7);
        ingredientList.add(i8);
        ingredientList.add(i9);
        ingredientList.add(i10);
        ingredientList.add(i11);
        ingredientList.add(i12);
        ingredientList.add(i13);
        ingredientList.add(i14);
        ingredientList.add(i15);
        ingredientList.add(i16);



        insertDefaultIngredientsToDatabase(ingredientList);

        //creating and inserting recipes
        Recipe r1 = new Recipe("Jack Coke");
        Recipe r2 = new Recipe("Mojito");
        Recipe r3 = new Recipe("Vodka orange");
        Recipe r4 = new Recipe("Cuba Libre");
        Recipe r5 = new Recipe("Vodka Sprite");

        List<Recipe> recipeList = new ArrayList<Recipe>();
        recipeList.add(r1);
        recipeList.add(r2);
        recipeList.add(r3);
        recipeList.add(r4);
        recipeList.add(r5);

        //insertDefaultRecipesToDatabase(recipeList);


        //creating recipe items

        //jack coke
        RecipeItem ri1 = new RecipeItem((long) 1, ingredientList.get(0).getId(), 300.0, recipeList.get(0).getId());
        RecipeItem ri2 = new RecipeItem((long) 2, ingredientList.get(2).getId(), 50.0, recipeList.get(0).getId());


        //mojito
        RecipeItem ri3 = new RecipeItem((long) 3,ingredientList.get(8).getId(), 40.0, recipeList.get(1).getId());
        RecipeItem ri4 = new RecipeItem((long) 4,ingredientList.get(6).getId(), 20.0, recipeList.get(1).getId());
        RecipeItem ri5 = new RecipeItem((long) 5,ingredientList.get(10).getId(), 240.0, recipeList.get(1).getId());


        //vodka orange
        RecipeItem ri6 = new RecipeItem((long) 6,ingredientList.get(13).getId(), 50.0, recipeList.get(2).getId());
        RecipeItem ri7 = new RecipeItem((long) 7,ingredientList.get(7).getId(), 250.0, recipeList.get(2).getId());


        //cuba libre
        RecipeItem ri8 = new RecipeItem((long) 8,ingredientList.get(5).getId(), 60.0, recipeList.get(3).getId());
        RecipeItem ri9 = new RecipeItem((long) 9,ingredientList.get(0).getId(), 200.0, recipeList.get(3).getId());
        RecipeItem ri10 = new RecipeItem((long) 10,ingredientList.get(9).getId(), 40.0, recipeList.get(3).getId());


        //vodka sprite
        RecipeItem ri11 = new RecipeItem((long) 11,ingredientList.get(15).getId(), 200.0, recipeList.get(4).getId());
        RecipeItem ri12 = new RecipeItem((long) 12,ingredientList.get(13).getId(), 50.0, recipeList.get(4).getId());
        RecipeItem ri13 = new RecipeItem((long) 13,ingredientList.get(9).getId(), 20.0, recipeList.get(4).getId());


        List<RecipeItem> recipeItemList = new ArrayList<RecipeItem>();
        recipeItemList.add(ri1);
        recipeItemList.add(ri2);
        recipeItemList.add(ri3);
        recipeItemList.add(ri4);
        recipeItemList.add(ri5);
        recipeItemList.add(ri6);
        recipeItemList.add(ri7);
        recipeItemList.add(ri8);
        recipeItemList.add(ri9);
        recipeItemList.add(ri10);
        recipeItemList.add(ri11);
        recipeItemList.add(ri12);
        recipeItemList.add(ri13);


        //insertDefaultRecipeItemsToDatabase(recipeItemList);

    }


    public void insertDefaultIngredientsToDatabase(final List<Ingredient> items)
    {

        new AsyncTask<Void, Void, List<Ingredient>>() {

            @Override
            protected List<Ingredient> doInBackground(Void... voids) {


                for(int i = 0; i < items.size();i++)
                {
                    items.get(i).setId(database.ingredientDao().insert(items.get(i)));
                }

                //System.out.println("item added to database: " + newItem.getName() + "  " + newItem.getId() + "\n");

                return null;
            }

        }.execute();

    }


    public void insertDefaultRecipesToDatabase(final List<Recipe> items)
    {

        new AsyncTask<Void, Void, List<Recipe>>() {

            @Override
            protected List<Recipe> doInBackground(Void... voids) {


                for(int i = 0; i < items.size();i++)
                {
                    items.get(i).setId(database.recipeDao().insert(items.get(i)));
                }

                //System.out.println("item added to database: " + newItem.getName() + "  " + newItem.getId() + "\n");

                return null;
            }

        }.execute();

    }


    public void insertDefaultRecipeItemsToDatabase(final List<RecipeItem> items)
    {

        new AsyncTask<Void, Void, List<RecipeItem>>() {

            @Override
            protected List<RecipeItem> doInBackground(Void... voids) {


                for(int i = 0; i < items.size();i++)
                {
                   long id = database.recipeItemDao().insert(items.get(i));
                }

                //System.out.println("item added to database: " + newItem.getName() + "  " + newItem.getId() + "\n");

                return null;
            }

        }.execute();

    }

}
