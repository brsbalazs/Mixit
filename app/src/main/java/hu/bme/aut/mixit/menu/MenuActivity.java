package hu.bme.aut.mixit.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import hu.bme.aut.mixit.R;
import hu.bme.aut.mixit.filter_activities.FilterActivity;
import hu.bme.aut.mixit.history_activities.HistoryActivity;
import hu.bme.aut.mixit.info_activities.InfosActivity;
import hu.bme.aut.mixit.ingredients_activities.IngredientsActivity;
import hu.bme.aut.mixit.measure_activities.MeasureActivity;
import hu.bme.aut.mixit.mixing_activities.MixingActivity;
import hu.bme.aut.mixit.recipes_activities.RecipesActivity;
import hu.bme.aut.mixit.settings_activities.SettingsActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);
        getSupportActionBar().hide();
    }

    @Override
    public void onBackPressed() { }


    public void ingredientsOnClick(View view)
    {
        Intent intent = new Intent(this, IngredientsActivity.class);
        startActivity(intent);
    }


    public void recipesOnClick(View view)
    {
        Intent intent = new Intent(this, RecipesActivity.class);
        startActivity(intent);
    }

    public void mixingOnClick(View view)
    {
        Intent intent = new Intent(this, MixingActivity.class);
        startActivity(intent);
    }

    public void filterOnClick(View view)
    {
        Intent intent = new Intent(this, FilterActivity.class);
        startActivity(intent);
    }

    public void measureOnClick(View view)
    {
        Intent intent = new Intent(this, MeasureActivity.class);
        startActivity(intent);
    }

    public void historyOnClick(View view)
    {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void settingsOnClicked(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void infosOnClicked(View view)
    {
        Intent intent = new Intent(this, InfosActivity.class);
        startActivity(intent);
    }


}
