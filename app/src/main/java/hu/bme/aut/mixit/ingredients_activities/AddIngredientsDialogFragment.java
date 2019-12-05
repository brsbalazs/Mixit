package hu.bme.aut.mixit.ingredients_activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Database;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import hu.bme.aut.mixit.R;
import hu.bme.aut.mixit.model.Ingredient;

public class AddIngredientsDialogFragment extends DialogFragment {

    private EditText nameEditText;
    private NumberPicker alcoholNumberPicker;
    private TextView priceEditText;
    private View contentView;



    public static final String TAG = "AddIngredientDialogFragment";

    public interface AddIngredientsDialogListener {
        void onIngredientCreated(Ingredient newItem);
        void showSnackbarWithMessage(String message, String actionName, View.OnClickListener listener);
    }

    private AddIngredientsDialogListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity instanceof AddIngredientsDialogListener) {
            listener = (AddIngredientsDialogListener) activity;
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {



        Dialog dialog =  new AlertDialog.Builder(requireContext())
                .setTitle("Adding ingredient")
                .setView(getContentView())
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isValid())
                        {
                            listener.onIngredientCreated(getIngredient());
                        }
                        else
                            {
                                listener.showSnackbarWithMessage("Invalid data, ingredient has not been saved.", "", null);
                            }
                    }
                })
                .setNegativeButton("cancel", null)
                .create();



        return dialog;

    }


    private View getContentView() {


        contentView = LayoutInflater.from(getContext()).inflate(R.layout.add_ingredient, null);
        nameEditText = contentView.findViewById(R.id.IngredientEditText);
        alcoholNumberPicker = contentView.findViewById(R.id.AlcoholNumberPicker);

        ArrayList<String> values = new ArrayList<>();



        alcoholNumberPicker.setMaxValue(100);
        alcoholNumberPicker.setMinValue(0);


        priceEditText = contentView.findViewById(R.id.priceEditText);

        return contentView;


    }

    private boolean isValid()
    {
        boolean valid = true;
        if(nameEditText.getText().length()==0) valid = false;
        if(priceEditText.getText().equals("")) valid = false;

        if(valid) return true;
        else return false;

    }

    private Ingredient getIngredient() {

        Ingredient item = new Ingredient();
        item.setName(nameEditText.getText().toString());
        item.setAlc(alcoholNumberPicker.getValue());

        String text = priceEditText.getText().toString();
        String[] split = text.split(" ");
        int price = Integer.parseInt(split[0]);

        item.setPrice(price);

        return item;

    }


}

