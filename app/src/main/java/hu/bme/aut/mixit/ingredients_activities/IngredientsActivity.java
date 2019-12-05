package hu.bme.aut.mixit.ingredients_activities;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import hu.bme.aut.mixit.R;
import hu.bme.aut.mixit.adapter.IngredientListAdapter;
import hu.bme.aut.mixit.recipe_data.RecipeDatabase;
import hu.bme.aut.mixit.model.Ingredient;

public class IngredientsActivity extends AppCompatActivity implements AddIngredientsDialogFragment.AddIngredientsDialogListener, IngredientListAdapter.IngredientClickListener {


    private RecipeDatabase database;
    private IngredientListAdapter adapter;
    private RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        //full sceen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FloatingActionButton addbtn = findViewById(R.id.addbtn);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AddIngredientsDialogFragment().show(getSupportFragmentManager(), AddIngredientsDialogFragment.TAG);

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

    @Override
    public void onIngredientCreated(final Ingredient newItem)
    {

        new AsyncTask<Void, Void, Ingredient>() {

            @Override
            protected Ingredient doInBackground(Void... voids) {
                newItem.setId(database.ingredientDao().insert(newItem));

                System.out.println("item added to database: " + newItem.getName() + "  " + newItem.getId() + "\n");

                return newItem;
            }

            @Override
            protected void onPostExecute(Ingredient item) {
                adapter.addItem(item);
            }
        }.execute();

        Snackbar.make(getWindow().getDecorView().findViewById(R.id.addbtn)
                , "Ingredient successfully added", Snackbar.LENGTH_LONG)
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

        recyclerView = findViewById(R.id.RecyclerView);
        adapter = new IngredientListAdapter(this);
        adapter.setContext(getApplicationContext());
        adapter.setCurrentView(findViewById(R.id.ingredient_view));
        loadItemsInBackground();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    //AsyncTask - koltseges adatbazis muveletek ne az ui szalat blokkoljak
    //ezek masok szalban fussanak, hogy az alkalmazas reszponziv maradjon
    private void loadItemsInBackground() {
        new AsyncTask<Void, Void, List<Ingredient>>() {

            @Override
            protected List<Ingredient> doInBackground(Void... voids) {

                List<Ingredient> list = database.ingredientDao().getAll();

                for(int i = 0; i < list.size(); i++)
                {
                    System.out.println(list.get(i).getId() + " " + list.get(i).getName());
                }

                setListItemCounter(list.size());

                return list;
            }

            @Override
            protected void onPostExecute(List<Ingredient> items) {

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
    public void onItemChanged(final Ingredient item) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                database.ingredientDao().update(item);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSuccessful) {
                Log.d("Ingredientactivity", "Ingredient updated");
            }
        }.execute();
    }


    @Override
    public void onItemDeleted(final Ingredient item)
    {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                database.ingredientDao().deleteItem(item);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSuccessful) {
                initRecyclerView();
                Log.d("MainActivity", "ShoppingItem deleting was successful");
            }
        }.execute();

    }

    @Override
    public void showSnackbarWithMessage(String message, String actionName, View.OnClickListener listener)
    {

        Snackbar.make(findViewById(R.id.addbtn)
                , message, Snackbar.LENGTH_LONG)
                .setAction(actionName, listener).show();
    }


    public void loadItemsInAZOrder(View view) {
        new AsyncTask<Void, Void, List<Ingredient>>() {

            @Override
            protected List<Ingredient> doInBackground(Void... voids) {

                List<Ingredient> list = database.ingredientDao().getAllAZ();

                return list;
            }

            @Override
            protected void onPostExecute(List<Ingredient> items) {


                adapter.update(items);
            }
        }.execute();
    }


    public void loadItemsInZAOrder(View view) {
        new AsyncTask<Void, Void, List<Ingredient>>() {

            @Override
            protected List<Ingredient> doInBackground(Void... voids) {

                List<Ingredient> list = database.ingredientDao().getAllZA();

                return list;
            }

            @Override
            protected void onPostExecute(List<Ingredient> items) {


                adapter.update(items);
            }
        }.execute();
    }

    public void loadItemsInNoneOrder(View view){loadItemsInBackground();}

    public void setListItemCounter(int numberOfListItems)
    {
        TextView itemListCounter = (TextView) findViewById(R.id.ingredientListItemCounter);

        if(numberOfListItems <2) itemListCounter.setText(numberOfListItems + " item");
        else itemListCounter.setText(numberOfListItems + " items");


    }


    public void loadSearchedInBackground(final String searchText) {
        new AsyncTask<Void, Void, List<Ingredient>>() {

            @Override
            protected List<Ingredient> doInBackground(Void... voids) {

                List<Ingredient> list = database.ingredientDao().searchForString("%"+ searchText +"%");

                return list;
            }

            @Override
            protected void onPostExecute(List<Ingredient> items) {


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
