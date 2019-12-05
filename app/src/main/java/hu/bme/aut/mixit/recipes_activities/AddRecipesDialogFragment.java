package hu.bme.aut.mixit.recipes_activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.mixit.R;
import hu.bme.aut.mixit.model.Ingredient;
import hu.bme.aut.mixit.model.Recipe;
import hu.bme.aut.mixit.model.RecipeItem;

public class AddRecipesDialogFragment extends DialogFragment
{

    private EditText nameEditText;
    private NumberPicker alcoholNumberPicker;
    private TextView priceEditText;
    private LinearLayout listOfIngredients;
    private LayoutInflater inflater;
    private View contentView;

    public static final String TAG = "AddRecipesDialogFragment";

    public interface AddRecipesDialogListener {
        void onRecipeCreated(Recipe newItem, List<RecipeItem> recipeItems);
        List<Ingredient> getIngredients();
        void showSnackbarWithMessage(String message, String actionName, View.OnClickListener listener);
    }

    private AddRecipesDialogFragment.AddRecipesDialogListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity instanceof AddRecipesDialogFragment.AddRecipesDialogListener) {

            listener = (AddRecipesDialogFragment.AddRecipesDialogListener) activity;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  new AlertDialog.Builder(requireContext())
                .setTitle("Adding recipe")
                .setView(getContentView())
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isValid())
                        {
                            //getting the recipe details
                            Recipe recipe = new Recipe();
                            TextView nameTextView = (TextView)contentView.findViewById(R.id.recipe_name_edit_text);
                            recipe.setName(nameTextView.getText().toString());

                            List<RecipeItem> recipeItems = new ArrayList<RecipeItem>();


                            //getting the ingredient details
                            LinearLayout linearLayout = (LinearLayout)contentView.findViewById(R.id.list_of_rows);

                            for(int j = 0; j < linearLayout.getChildCount(); j++)
                            {

                                RecipeItem item = new RecipeItem();

                                CardView card = (CardView) linearLayout.getChildAt(j);
                                Spinner ingredientSpinner = (Spinner)card.findViewById(R.id.choose_ingredient_spinner);
                                List<Ingredient> ingredients = listener.getIngredients();

                                for (int y = 0; y < ingredients.size(); y++)
                                {
                                    if(ingredients.get(y).getName().equals(ingredientSpinner.getSelectedItem().toString()))
                                    {
                                        item.setIngredient_id(ingredients.get(y).getId());
                                    }
                                }

                                if(item.getIngredient_id() != 0)
                                {
                                    EditText amountEditText = (EditText) card.findViewById(R.id.ingredient_amount_edittext);
                                    item.setAmount(Double.valueOf(amountEditText.getText().toString()));
                                    recipeItems.add(item);
                                }



                            }

                            for(int y = 0; y < recipeItems.size(); y++)
                            {
                                System.out.println(recipeItems.get(y).getAmount());
                            }


                            recipe.setAlc(recipeAlcPercent(recipeItems));
                            recipe.setPrice(recipePrice(recipeItems, recipe));

                            listener.onRecipeCreated(recipe, recipeItems);
                        }
                    }
                })
                .setNegativeButton("cancel", null)
                .create();

        return dialog;

    }

    public double recipeAlcPercent(List<RecipeItem> recipeItems)
    {
        int counter = 0;
        double sumalccontent = 0;
        double sumamount = 0.0;


        List<Ingredient> ingredients = listener.getIngredients();

        for(int i = 0; i < recipeItems.size(); i++)
        {

                counter++;

                for(int y = 0; y < ingredients.size(); y++)
                {
                    if(ingredients.get(y).getId() == recipeItems.get(i).getIngredient_id())
                    {
                        sumalccontent += ingredients.get(y).getAlc() * recipeItems.get(i).getAmount();
                        sumamount += recipeItems.get(i).getAmount();
                    }
                }


        }

        return Math.round(((sumalccontent/sumamount)));
    }

    public int recipePrice(List<RecipeItem> recipeItems, Recipe recipe)
    {
        int counter = 0;
        int sumrice = 0;
        int sumamount = 0;

        List<Ingredient> ingredients = listener.getIngredients();

        for(int i = 0; i < recipeItems.size(); i++)
        {

            counter++;

            for(int y = 0; y < ingredients.size(); y++)
            {
                if(ingredients.get(y).getId() == recipeItems.get(i).getIngredient_id())
                {
                    sumrice += (ingredients.get(y).getPrice()/1000.0) * recipeItems.get(i).getAmount();

                }
            }

            sumamount += recipeItems.get(i).getAmount();

        }

        recipe.setAmount(sumamount);

        return Math.round(sumrice);

    }


    private View getContentView() {


        contentView = LayoutInflater.from(getContext()).inflate(R.layout.add_recipes_dialog, null);

        Button addIngredientButton = (Button) contentView.findViewById(R.id.add_ingredient_button);
        listOfIngredients = (LinearLayout) contentView.findViewById(R.id.list_of_rows);

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final CardView row_item = (CardView) getLayoutInflater().inflate(R.layout.add_ingredient_list_element, null);

                Spinner spinner = row_item.findViewById(R.id.choose_ingredient_spinner);

                List<Ingredient> list = listener.getIngredients();
                List<String> spinnerItems = new ArrayList<String>();

                spinnerItems.add("Choose item...");

                for(int i = 0; i < list.size();i++)
                {
                    spinnerItems.add(list.get(i).getName());
                }

                row_item.findViewById(R.id.delete_ingredient).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listOfIngredients.removeView(row_item);
                    }
                });


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(row_item.getContext(), R.layout.spinner_item, spinnerItems);
                spinner.setAdapter(adapter);

                listOfIngredients.addView(row_item);

            }
        });



        return contentView;


    }

    //TODO validating form - checking ingredients
    private boolean isValid()
    {
        boolean valid = true;

        TextView nameTextView = (TextView)contentView.findViewById(R.id.recipe_name_edit_text);

        if(nameTextView.getText().length() == 0)
        {
            listener.showSnackbarWithMessage("Recipe name can not be null, recipe has not been saved.", null, null);
            return false;
        }

        LinearLayout linearLayout = (LinearLayout)contentView.findViewById(R.id.list_of_rows);

        for(int j = 0; j < linearLayout.getChildCount(); j++)
        {

            RecipeItem item = new RecipeItem();

            CardView card = (CardView) linearLayout.getChildAt(j);
            Spinner ingredientSpinner = (Spinner)card.findViewById(R.id.choose_ingredient_spinner);

            if(ingredientSpinner.getSelectedItem() == "Choose item...")
            {
                listener.showSnackbarWithMessage("Ingredient name can not be null, recipe has not been added.", null, null);
                return false;
            }


            EditText amountEditText = (EditText) card.findViewById(R.id.ingredient_amount_edittext);

            if(amountEditText.getText().toString().length() == 0)
            {
                listener.showSnackbarWithMessage("Ingredient amount can not be null, recipe has not been added.", null, null);
                return false;
            }


        }

        return true;
    }




}
