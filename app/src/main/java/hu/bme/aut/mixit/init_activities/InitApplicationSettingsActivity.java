package hu.bme.aut.mixit.init_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

import hu.bme.aut.mixit.R;
import hu.bme.aut.mixit.menu.MenuActivity;
import hu.bme.aut.mixit.settings_activities.SettingsDialogFragment;

public class InitApplicationSettingsActivity extends AppCompatActivity {


    FloatingActionButton fab;

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
}
