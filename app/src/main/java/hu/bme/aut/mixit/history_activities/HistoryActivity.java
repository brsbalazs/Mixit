package hu.bme.aut.mixit.history_activities;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.room.Room;

import android.view.View;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import org.threeten.bp.LocalDate;
import java.util.List;

import hu.bme.aut.mixit.R;
import hu.bme.aut.mixit.measure_data.MeasureDatabase;
import hu.bme.aut.mixit.model.MeasureData;
import hu.bme.aut.mixit.model.Recipe;

public class HistoryActivity extends AppCompatActivity {

    MaterialCalendarView calendarView;
    List<MeasureData> displayableMeasureData = new ArrayList<MeasureData>();

    MeasureDatabase measureDatabase;
    List<MeasureData> measureDataList;

    LinearLayout measureDataListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        measureDatabase = Room.databaseBuilder(
                getApplicationContext(),
                MeasureDatabase.class,
                "measure_data_database1.0"
        ).build();



        calendarView = findViewById(R.id.calendar);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected)
            {
                System.out.println("Date changed!");
                //TODO selecting measuredata from the selected day, and display

                getDisplayableMeasureData();
                initList();

            }




        });

        measureDataListView = (LinearLayout) findViewById(R.id.history_list_of_rows);


        //TODO marking the days with nightout
        //calendarView.addDecorator();


        calendarView.setSelectedDate(CalendarDay.today());
        loadItemsInBackground();

    }


    public void backClicked(View view){ onBackPressed();}

    private void loadItemsInBackground() {
        new AsyncTask<Void, Void, List<MeasureData>>() {

            @Override
            protected List<MeasureData> doInBackground(Void... voids) {

                measureDataList = measureDatabase.measureDataDao().getAll();

                return measureDataList;
            }

            @Override
            protected void onPostExecute(List<MeasureData> measureDataList) {

                getDisplayableMeasureData();
                initList();

            }
        }.execute();
    }

    /**Selects the measure datas for the selected date*/
    public void getDisplayableMeasureData() {

        displayableMeasureData.clear();



        for (int i = 0; i < measureDataList.size(); i++) {


            int calendarYear = calendarView.getSelectedDate().getYear();
            int calendarMonth = calendarView.getSelectedDate().getMonth();
            int calendarDay = calendarView.getSelectedDate().getDay();


            String[] measureSplit = measureDataList.get(i).getDatetime().toString().split(" ");
            String[] measureDate = measureSplit[0].split("-");


            int measureYear = Integer.valueOf(measureDate[0]);
            int measureMonth = Integer.valueOf(measureDate[1]);
            int measureDay = Integer.valueOf(measureDate[2]);

            System.out.println(calendarDay + "  " + measureDay + "   " + calendarMonth + " " + measureMonth + "   " + calendarYear + " " + measureYear);


            if (calendarDay == measureDay && calendarMonth == measureMonth && calendarYear == measureYear) {

                displayableMeasureData.add(measureDataList.get(i));
            }

        }
    }

    /**Inits list at history and inflates list items*/
    public void initList()
    {
        measureDataListView.removeAllViews();
        CardView row_item;


        for(int i = 0; i < displayableMeasureData.size(); i++)
        {

            row_item = (CardView) getLayoutInflater().inflate(R.layout.list_item_history, null);

            //setting image
            ImageView image = (ImageView) row_item.findViewById(R.id.measureListItemImage);
            image.setImageResource(getImageResource(displayableMeasureData.get(i).getAlcohol_content()));

            //setting beverage name
            TextView nameText = (TextView) row_item.findViewById(R.id.beverageNameText);
            nameText.setText(displayableMeasureData.get(i).getBeverage_name());

            //setting icons
            ImageView alcohol_icon = (ImageView) row_item.findViewById(R.id.measure_alcohol_icon);
            alcohol_icon.setImageResource(R.drawable.measure_icon_grey);
            ImageView amount = (ImageView) row_item.findViewById(R.id.amount_icon);
            amount.setImageResource(R.drawable.measure_liquid);

            //setting alcohol
            TextView percentText = (TextView) row_item.findViewById(R.id.beverageAlcText);
            percentText.setText(String.valueOf(displayableMeasureData.get(i).getAlcohol_content()) + " %");

            //setting amount
            TextView amountText = (TextView) row_item.findViewById(R.id.beverageAmountText);
            amountText.setText(String.valueOf(displayableMeasureData.get(i).getAmount()) + " ml");

            ImageView clockicon = (ImageView) row_item.findViewById(R.id.measure_calendar_icon);
            clockicon.setImageResource(R.drawable.clock);


            //setting time
            TextView timetext = (TextView) row_item.findViewById(R.id.measureDate);
            timetext.setText(String.valueOf(displayableMeasureData.get(i).getDatetime().split(" ")[1]));

            row_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


            measureDataListView.addView(row_item);
        }


        if(displayableMeasureData.size() == 0)
        {
            LinearLayout emptylistview = (LinearLayout) getLayoutInflater().inflate(R.layout.empty_list, null);
            measureDataListView.addView(emptylistview);
        }

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

}
