package hu.bme.aut.mixit.settings_activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import hu.bme.aut.mixit.R;
import hu.bme.aut.mixit.recipes_activities.AddRecipesDialogFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SharedPreferences sharedPrefs = getSharedPreferences("user_settings_sp1.0", MODE_PRIVATE);

        EditText nameEditText = (EditText) findViewById(R.id.settings_name_edittext);
        nameEditText.setText(sharedPrefs.getString("name", ""));

        EditText ageEditText = (EditText) findViewById(R.id.settings_age_edittext);
        ageEditText.setText(sharedPrefs.getString("age", ""));


        NumberPicker weightNumberPicker = (NumberPicker) findViewById(R.id.WeightNumberPicker);
        NumberPicker heightNumberPicker = (NumberPicker) findViewById(R.id.HeightNumberPicker);

        weightNumberPicker.setMinValue(0);
        weightNumberPicker.setMaxValue(200);
        weightNumberPicker.setValue(70);
        weightNumberPicker.setValue(sharedPrefs.getInt("weight", 0));

        heightNumberPicker.setMaxValue(230);
        heightNumberPicker.setMinValue(100);
        heightNumberPicker.setValue(160);
        heightNumberPicker.setValue(sharedPrefs.getInt("height", 0));

        RadioButton genderFemale = (RadioButton) findViewById(R.id.radio_female);
        RadioButton genderMale = (RadioButton) findViewById(R.id.radio_male);

        if(sharedPrefs.getString("gender","").equals("female")) genderFemale.setChecked(true);
        else genderMale.setChecked(true);


        ImageView arrow_back = (ImageView) findViewById(R.id.settings_back_btn);

        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SettingsDialogFragment().show(getSupportFragmentManager(), SettingsDialogFragment.TAG);

            }
        });

        final Button saveChanges = (Button) findViewById(R.id.save_changes);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onSaveChangesClicked();
            }
        });

    }


    public void onSaveChangesClicked()
    {

        SharedPreferences sharedPrefs = getSharedPreferences("user_settings_sp1.0", MODE_PRIVATE);
        SharedPreferences.Editor ed;
        ed = sharedPrefs.edit();

        EditText nameEditText = (EditText) findViewById(R.id.settings_name_edittext);
        ed.putString("name", nameEditText.getText().toString());

        EditText ageEditText = (EditText) findViewById(R.id.settings_age_edittext);
        ed.putString("age", ageEditText.getText().toString());


        NumberPicker weightNumberPicker = (NumberPicker) findViewById(R.id.WeightNumberPicker);
        ed.putInt("weight", weightNumberPicker.getValue());

        NumberPicker heightNumberPicker = (NumberPicker) findViewById(R.id.HeightNumberPicker);
        ed.putInt("height", heightNumberPicker.getValue());

        RadioButton genderFemale = (RadioButton) findViewById(R.id.radio_female);
        RadioButton genderMale = (RadioButton) findViewById(R.id.radio_male);

        if(genderFemale.isChecked()) ed.putString("gender", "female");
        else if(genderMale.isChecked()) ed.putString("gender", "male");

        ed.apply();

        Snackbar.make(findViewById(R.id.fab)
                , "Changes saved.", Snackbar.LENGTH_LONG)
                .setAction("", null).show();

    }


}