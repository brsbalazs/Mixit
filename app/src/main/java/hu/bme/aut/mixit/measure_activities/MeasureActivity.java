package hu.bme.aut.mixit.measure_activities;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.room.Room;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hu.bme.aut.mixit.R;
import hu.bme.aut.mixit.ingredients_activities.AddIngredientsDialogFragment;
import hu.bme.aut.mixit.measure_data.MeasureDatabase;
import hu.bme.aut.mixit.model.Ingredient;
import hu.bme.aut.mixit.model.MeasureData;
import hu.bme.aut.mixit.model.Recipe;
import hu.bme.aut.mixit.model.RecipeItem;
import hu.bme.aut.mixit.recipe_data.RecipeDatabase;
import hu.bme.aut.mixit.settings_activities.SettingsDialogFragment;

public class MeasureActivity extends AppCompatActivity implements AddMeasureDataDialogFragment.AddMeasureDataDialogListener {


    MeasureDatabase measureDatabase;
    RecipeDatabase recipeDatabase;

    LinearLayout listofmeasuredata;
    private PieChart chart;


    List<MeasureData> measureDataList = new ArrayList<MeasureData>();
    List<Recipe> recipeList = new ArrayList<Recipe>();
    List<RecipeItem> recipeItems = new ArrayList<RecipeItem>();
    List<Ingredient> ingredients = new ArrayList<Ingredient>();


    public List<Recipe> getRecipeList(){return recipeList;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        measureDatabase = Room.databaseBuilder(
                getApplicationContext(),
                MeasureDatabase.class,
                "measure_data_database1.0"
        ).build();


        recipeDatabase = Room.databaseBuilder(
                getApplicationContext(),
                RecipeDatabase.class,
                "mixitdatabase1.2"
        ).build();

        chart = findViewById(R.id.percentChart);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new InfoMeasureDataDialogFragment().show(getSupportFragmentManager(), InfoMeasureDataDialogFragment.TAG);

            }
        });



        FloatingActionButton addbtn = findViewById(R.id.addbtn);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AddMeasureDataDialogFragment().show(getSupportFragmentManager(), AddMeasureDataDialogFragment.TAG);

            }
        });

        loadItemsInBackground();


    }

    @Override
    public void onMeasureDataCreated(final MeasureData newItem)
    {
        new AsyncTask<Void, Void, MeasureData>() {

            @Override
            protected MeasureData doInBackground(Void... voids) {
                newItem.setId(measureDatabase.measureDataDao().insert(newItem));

                System.out.println("measuredata added to database: " + newItem.getBeverage_name() + "  " + newItem.getDatetime() + "\n");

                return newItem;
            }

            @Override
            protected void onPostExecute(MeasureData item)
            {
                loadItemsInBackground();
                initRecentBeveragesList();
            }

        }.execute();

        Snackbar.make(getWindow().getDecorView().findViewById(R.id.addbtn)
                , "Consumed beverage successfully added", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void showSnackbarWithMessage(String message, String actionName, View.OnClickListener listener)
    {
        Snackbar.make(findViewById(R.id.addbtn)
                , message, Snackbar.LENGTH_LONG)
                .setAction(actionName, listener).show();
    }


    private void loadItemsInBackground() {
        new AsyncTask<Void, Void, List<Recipe>>() {

            @Override
            protected List<Recipe> doInBackground(Void... voids) {
                recipeList = recipeDatabase.recipeDao().getAll();
                recipeItems = recipeDatabase.recipeItemDao().getAllRecipeItems();
                ingredients = recipeDatabase.ingredientDao().getAll();
                measureDataList = measureDatabase.measureDataDao().getAll();

                return recipeList;
            }

            @Override
            protected void onPostExecute(List<Recipe> recipeList) {

                initRecentBeveragesList();
                initChart();

            }
        }.execute();
    }


    public List<MeasureData> getDrinksFromLast12Hours()
    {
        List<MeasureData> recentDrinks = new ArrayList<MeasureData>();

        SimpleDateFormat dateFormat = new SimpleDateFormat(

                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Date date = new Date(System.currentTimeMillis() - (1000*60*60*12));

        String lastMinute = dateFormat.format(date);

        for(int i = 0; i < measureDataList.size(); i++)
        {

            if(measureDataList.get(i).getDatetime().compareTo(lastMinute) > 0)
            {
                recentDrinks.add(measureDataList.get(i));
            }

        }

        return recentDrinks;

    }


    public void initRecentBeveragesList()
    {

        final List<MeasureData> recentDrinks = getDrinksFromLast12Hours();
        listofmeasuredata = (LinearLayout) findViewById(R.id.measure_list_of_rows);

        listofmeasuredata.removeAllViews();
        CardView row_item;


        for(int i = 0; i < recentDrinks.size(); i++)
        {

                row_item = (CardView) getLayoutInflater().inflate(R.layout.list_item_measure, null);

                //setting image
                ImageView image = (ImageView) row_item.findViewById(R.id.measureListItemImage);
                image.setImageResource(getImageResource(recentDrinks.get(i).getAlcohol_content()));

                //setting beverage name
                TextView nameText = (TextView) row_item.findViewById(R.id.beverageNameText);
                nameText.setText(recentDrinks.get(i).getBeverage_name());

                //setting icons
                ImageView alcohol_icon = (ImageView) row_item.findViewById(R.id.measure_alcohol_icon);
                alcohol_icon.setImageResource(R.drawable.measure_icon_grey);
                ImageView amount = (ImageView) row_item.findViewById(R.id.amount_icon);
                amount.setImageResource(R.drawable.measure_liquid);

                //setting alcohol
                TextView percentText = (TextView) row_item.findViewById(R.id.beverageAlcText);
                percentText.setText(String.valueOf(recentDrinks.get(i).getAlcohol_content()) + " %");

                //setting amount
                TextView amountText = (TextView) row_item.findViewById(R.id.beverageAmountText);
                amountText.setText(String.valueOf(recentDrinks.get(i).getAmount()) + " ml");

                row_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                ImageButton delete = (ImageButton) row_item.findViewById(R.id.deleteMeasureData);

                final int index = i;
                final CardView item = row_item;

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        deleteMeasureData(recentDrinks.get(index).getId());

                    }
                });

                listofmeasuredata.addView(row_item);

            }

        if(recentDrinks.size() == 0)
        {
            LinearLayout emptylistview = (LinearLayout) getLayoutInflater().inflate(R.layout.empty_list, null);
            listofmeasuredata.addView(emptylistview);
        }


    }

    private void deleteMeasureData(final long id) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {

                measureDatabase.measureDataDao().deleteByID(id);
                System.out.println("Item deleted");

                return true;
            }

            @Override
            protected void onPostExecute(Boolean value) {

                loadItemsInBackground();

            }
        }.execute();
    }


    private @DrawableRes
    int getImageResource(int percent) {

        @DrawableRes int ret;

        //icons from https://www.flaticon.com/authors/dinosoftlabs from www.flaticon.com
        if(percent == 0) ret = R.drawable.drink_non;
        else if (percent > 0 && percent <=15.0) ret = R.drawable.drink_alcoholic;
        else if (percent > 15 && percent <= 50) ret = R.drawable.drink_strong;
        else if (percent > 50) ret = R.drawable.drink_xstrong;

        else ret = 0;

        return ret;
    }

    public void backClicked(View view)
    {
        onBackPressed();
    }

    private void initChart() {


        double bloodAlcohol = calculateBloodAlcohol();

        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry((float)bloodAlcohol, "current level"));

        boolean getDrunkvisible = false;

        if(0.06 - bloodAlcohol > 0.0)
        {
            entries.add(new PieEntry((float)(0.06-bloodAlcohol), "to get drunk"));

        }

        if(0.2 - bloodAlcohol > 0.0)
        {
            entries.add(new PieEntry((float)(0.2-bloodAlcohol), "to blackout"));

        }





        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);
        chart.setData(data);
        Description desc = new Description();

        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);

        desc.setText("Current BAC: " + df.format(bloodAlcohol) + " g/dl");
        chart.setDescription(desc);
        chart.setEntryLabelColor(R.color.black);
        chart.setCenterTextColor(R.color.black);
        chart.setDrawCenterText(false);
        //chart.setDrawEntryLabels(false); eltűnteti a magyarázatot
        chart.invalidate();

    }

    private double calculateBloodAlcohol()
    {

        SharedPreferences sharedPrefs = getSharedPreferences("user_settings_sp1.0", MODE_PRIVATE);


        int weight = sharedPrefs.getInt("weight", 0);

        String gender = sharedPrefs.getString("gender", "");


        List<MeasureData> measureData = getDrinksFromLast12Hours();

        long maxmillis = 0;

        for(int i = 0; i < measureData.size(); i++)
        {

            SimpleDateFormat dateFormat = new SimpleDateFormat(

                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            Date drinkDate;

            try {

                drinkDate = dateFormat.parse(measureData.get(i).getDatetime());

                Long current = System.currentTimeMillis();

                if(current - drinkDate.getTime() > maxmillis)
                {
                    maxmillis = current - drinkDate.getTime();
                }

            }catch ( Exception e){e.printStackTrace();}

        }


        double drinkingPeriodHours   = ((maxmillis / (1000.0*60.0*60.0)) % 24.0);

        System.out.println("drinking period: " + drinkingPeriodHours);


        double standardDrinks = 0.0;

        for(int i = 0; i < measureData.size(); i++)
        {

            double alcoholContentInML = (measureData.get(i).getAlcohol_content() / 100.0) * (double)measureData.get(i).getAmount();

            standardDrinks += alcoholContentInML/16.1;
            System.out.println("standard drinks: " + standardDrinks);
            System.out.println("weight" + (double)weight);

        }


        double resultLevel;

        if(gender.equals("male"))
        {
            resultLevel = (((0.806 * standardDrinks * 1.2) / (0.58 * (double)weight)) - (0.015 * (double)drinkingPeriodHours)/10.0);
        }
        else
        {
            resultLevel = (((0.806 * standardDrinks * 1.2) / (0.49 * (double)weight)) - (0.017 * (double)drinkingPeriodHours))/10.0;

        }


        return resultLevel;

    }


}
