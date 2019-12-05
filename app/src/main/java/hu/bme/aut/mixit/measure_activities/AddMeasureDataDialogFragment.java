package hu.bme.aut.mixit.measure_activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hu.bme.aut.mixit.R;
import hu.bme.aut.mixit.model.MeasureData;
import hu.bme.aut.mixit.model.Recipe;

public class AddMeasureDataDialogFragment extends DialogFragment
{
    public static final String TAG = "AddMeasureDataDialogFragment";

    private View contentView;
    private Spinner drinknamespinner;
    private EditText amountEditText;
    private NumberPicker minutesSpentNumberPicker;


    public interface AddMeasureDataDialogListener {
        void onMeasureDataCreated(MeasureData newItem);
        void showSnackbarWithMessage(String message, String actionName, View.OnClickListener listener);
        List<Recipe> getRecipeList();
    }

    private AddMeasureDataDialogFragment.AddMeasureDataDialogListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity instanceof AddMeasureDataDialogFragment.AddMeasureDataDialogListener) {
            listener = (AddMeasureDataDialogFragment.AddMeasureDataDialogListener) activity;
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {



        Dialog dialog =  new AlertDialog.Builder(requireContext())
                .setTitle("Adding consumed beverage")
                .setView(getContentView())
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isValid())
                        {
                            listener.onMeasureDataCreated(getMeasureData());
                        }
                        else
                        {
                            listener.showSnackbarWithMessage("Invalid data, consumed beverage has not been saved.", "", null);
                        }
                    }
                })
                .setNegativeButton("cancel", null)
                .create();



        return dialog;

    }

    //TODO implementing the method, + parameters
    private String getSubstractedDateTime(int minutesSpent) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(

                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Date date = new Date(System.currentTimeMillis() - (1000*60*minutesSpent));

        return dateFormat.format(date);

    }


    /**Creates the content view during initialization and setts component values*/
    private View getContentView() {


        contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_measure_data, null);

        drinknamespinner = (Spinner) contentView.findViewById(R.id.beverage_name_spinner);

        List<Recipe> recipeList = listener.getRecipeList();
        List<String> spinnerItems = new ArrayList<String>();

        spinnerItems.add("Choose beverage...");

        for(int i = 0; i < recipeList.size(); i++)
        {
            spinnerItems.add(recipeList.get(i).getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(contentView.getContext(), R.layout.spinner_item, spinnerItems);
        drinknamespinner.setAdapter(adapter);

        amountEditText = (EditText) contentView.findViewById(R.id.amount_edit_text);

        minutesSpentNumberPicker = (NumberPicker) contentView.findViewById(R.id.minutes_spent_numberpicker);

        minutesSpentNumberPicker.setMinValue(0);
        minutesSpentNumberPicker.setMaxValue(480);

        return contentView;


    }

    private boolean isValid()
    {

        if(amountEditText.getText().toString().length() <= 0) return false;
        if(drinknamespinner.getSelectedItem().toString().equals("Choose beverage...")) return false;

        return true;
    }

    /**Returns the data back from the dialog and makes MeasureData object*/
    private MeasureData getMeasureData()
    {


        MeasureData data = new MeasureData();

        if(drinknamespinner.getSelectedItem().toString().length() > 0)
        {
            data.setBeverage_name(drinknamespinner.getSelectedItem().toString());

        }


        if(amountEditText.getText().toString().length() > 0)
        {
            data.setAmount(Integer.valueOf(amountEditText.getText().toString()));

        }

        List<Recipe> recipeList = listener.getRecipeList();

        for(int i = 0; i < recipeList.size(); i++)
        {
            if(recipeList.get(i).getName().equals(drinknamespinner.getSelectedItem().toString()))
            {
                data.setAlcohol_content((int)recipeList.get(i).getAlc());
                System.out.println((int)recipeList.get(i).getAlc());
            }
        }

        data.setDatetime(getSubstractedDateTime(minutesSpentNumberPicker.getValue()));

        return data;

    }

}
