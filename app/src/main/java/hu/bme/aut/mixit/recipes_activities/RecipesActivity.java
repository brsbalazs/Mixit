package hu.bme.aut.mixit.recipes_activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import hu.bme.aut.mixit.R;
import hu.bme.aut.mixit.adapter.RecipeListAdapter;
import hu.bme.aut.mixit.recipe_data.RecipeDatabase;
import hu.bme.aut.mixit.menu.MenuActivity;
import hu.bme.aut.mixit.model.Ingredient;
import hu.bme.aut.mixit.model.Recipe;
import hu.bme.aut.mixit.model.RecipeItem;

public class RecipesActivity extends AppCompatActivity implements RecipeListAdapter.RecipeClickListener, AddRecipesDialogFragment.AddRecipesDialogListener {


    private RecipeDatabase database;
    private RecipeListAdapter adapter;
    private RecyclerView recyclerView;
    private List<Ingredient> ingredients;
    private List<RecipeItem> recipeItems;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AddRecipesDialogFragment().show(getSupportFragmentManager(), AddRecipesDialogFragment.TAG);

            }
        });

        database = Room.databaseBuilder(
                getApplicationContext(),
                RecipeDatabase.class,
                "mixitdatabase1.2"
        ).build();


        //setting onclicks of searchview
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                loadItemsInBackground();
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearch();
                RadioButton radio = findViewById(R.id.radio_none);
                radio.setChecked(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



        initRecyclerView();


    }

    public List<Ingredient> getIngredients()
    {
        return ingredients;
    }

    public List<RecipeItem> getRecipeItems() { return recipeItems;}


    @Override
    public void onRecipeCreated(final Recipe newItem, final List<RecipeItem> recipeItems)
    {

        new AsyncTask<Void, Void, Recipe>() {

            @Override
            protected Recipe doInBackground(Void... voids) {

                newItem.setId(database.recipeDao().insert(newItem));

                System.out.println("item added to database: " + newItem.getName() + "  Id: " + newItem.getId() + " with ingredients: \n");

                //setting the recipe id for all od the ingredients
                for(int i = 0; i < recipeItems.size(); i++)
                {
                    recipeItems.get(i).setRecipeId(newItem.getId());
                    recipeItems.get(i).setId(database.recipeItemDao().insert(recipeItems.get(i)));

                    System.out.println("id: " + recipeItems.get(i).getId() + " recipeId: " + recipeItems.get(i).getRecipeId() + " ingredientId: " + recipeItems.get(i).getIngredient_id() + " amount: " + recipeItems.get(i).getAmount() + "ml");
                }


                return newItem;
            }

            @Override
            protected void onPostExecute(Recipe item) {

                adapter.addItem(item);
            }
        }.execute();

        Snackbar.make(getWindow().getDecorView().findViewById(R.id.fab)
                , "Recipe successfully created", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();


    }



    /**inflates the settings menu and other menu options*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**If the setting menu item has been selected*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //A recycler view inicializalasahoz kell
    private void initRecyclerView() {

        //View itemView = getLayoutInflater().inflate(R.layout.content_recyclerview, null);

        recyclerView = findViewById(R.id.RecipesRecyclerView);
        adapter = new RecipeListAdapter(this);
        adapter.setContext(getApplicationContext());
        adapter.setFab(fab);
        loadItemsInBackground();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    //AsyncTask - koltseges adatbazis muveletek ne az ui szalat blokkoljak
    //ezek masok szalban fussanak, hogy az alkalmazas reszponziv maradjon
    private void loadItemsInBackground() {
        new AsyncTask<Void, Void, List<Recipe>>() {

            @Override
            protected List<Recipe> doInBackground(Void... voids) {

                List<Recipe> list = database.recipeDao().getAll();

                for(int i = 0; i < list.size(); i++)
                {
                    System.out.println(list.get(i).getId() + " " + list.get(i).getName());
                }

                recipeItems = database.recipeItemDao().getAllRecipeItems();
                ingredients = database.ingredientDao().getAll();

                setListItemCounter(list.size());

                return list;
            }

            @Override
            protected void onPostExecute(List<Recipe> items) {

                System.out.println("\nonPostExecute:");
                for(int i = 0; i < items.size(); i++)
                {
                    System.out.println(items.get(i).getId() + " " + items.get(i).getName());
                }

                adapter.update(items);
            }
        }.execute();
    }


    //AsyncTask
    @Override
    public void onItemChanged(final Recipe item) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                database.recipeDao().update(item);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSuccessful) {
                Log.d("Recipeactivity", "Recipe updated");
            }
        }.execute();
    }

    /**Being called when a list item has been deleted*/
    @Override
    public void onItemDeleted(final Recipe item)
    {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids)
            {
                database.recipeDao().deleteItem(item);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSuccessful) {
                initRecyclerView();
                showSnackbarWithMessage("Recipe deleted successfully.", "", null);
                Log.d("RecipeActivity", "Recipe deleting was successful");
            }
        }.execute();

    }

    /**If the back button was pressed redirects user to the main menu*/
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(intent);
    }

    /**Reloads item ordered by name - Z-A */
    public void loadItemsInAZOrder(View view) {
        new AsyncTask<Void, Void, List<Recipe>>() {

            @Override
            protected List<Recipe> doInBackground(Void... voids) {

                List<Recipe> list = database.recipeDao().getAllAZ();

              return list;
            }

            @Override
            protected void onPostExecute(List<Recipe> items) {

                adapter.update(items);
            }
        }.execute();
    }

    /**Reloads item ordered by name - A-Z */
    public void loadItemsInZAOrder(View view) {
        new AsyncTask<Void, Void, List<Recipe>>() {

            @Override
            protected List<Recipe> doInBackground(Void... voids) {

                List<Recipe> list = database.recipeDao().getAllZA();

                return list;
            }

            @Override
            protected void onPostExecute(List<Recipe> items) {

                adapter.update(items);
            }
        }.execute();
    }

    /**Reloads item in not specified order*/
    public void loadItemsInNoneOrder(View view){loadItemsInBackground();}

    /**Shows a snackbar with the given message*/
    @Override
    public void showSnackbarWithMessage(String message, String actionName, View.OnClickListener listener)
    {

        Snackbar.make(findViewById(R.id.fab)
                , message, Snackbar.LENGTH_LONG)
                .setAction(actionName, listener).show();
    }


    /**Sets the recipe list item counter*/
    public void setListItemCounter(int numberOfListItems)
    {
        TextView itemListCounter = (TextView) findViewById(R.id.recipeListItemCounter);

        if(numberOfListItems <2) itemListCounter.setText(numberOfListItems + " item");
        else itemListCounter.setText(numberOfListItems + " items");


    }


    public void loadSearchedInBackground(final String searchText) {
        new AsyncTask<Void, Void, List<Recipe>>() {

            @Override
            protected List<Recipe> doInBackground(Void... voids) {




                List<Recipe> list = database.recipeDao().searchForString("%"+ searchText +"%");

                return list;
            }

            @Override
            protected void onPostExecute(List<Recipe> items) {


                adapter.update(items);
            }
        }.execute();
    }


    public void onSearch()
    {
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        String searchText = searchView.getQuery().toString();
        loadSearchedInBackground(searchText);
    }





}
