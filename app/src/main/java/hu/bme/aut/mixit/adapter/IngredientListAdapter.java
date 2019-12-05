package hu.bme.aut.mixit.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.mixit.R;
import hu.bme.aut.mixit.model.Ingredient;
import hu.bme.aut.mixit.model.Recipe;
import hu.bme.aut.mixit.recipes_activities.RecipeDetialsViewActivity;

public class IngredientListAdapter
        extends RecyclerView.Adapter<IngredientListAdapter.IngredientsListViewHolder> {

    private final List<Ingredient> items;
    private Context context;
    private IngredientClickListener listener;
    private View currentView;
    private Activity activity;

    public void setActivity(Activity activity){this.activity = activity;}

    public void setContext(Context context){ this.context = context;}

    public void setCurrentView(View view){this.currentView = view;}

    public interface IngredientClickListener {
        void onItemChanged(Ingredient item);
        void onItemDeleted(Ingredient item);
        void showSnackbarWithMessage(String s, String actionName, View.OnClickListener listener);
        void setListItemCounter(int numberOfListItems);
    }



    public IngredientListAdapter(IngredientClickListener listener) {

        this.listener = listener;
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public IngredientsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.ingredient_list_item, parent, false);
        return new IngredientsListViewHolder(itemView, currentView);
    }

    /**Creating the appearance of the listelement*/
    @Override
    public void onBindViewHolder(@NonNull IngredientsListViewHolder holder, int position) {
        Ingredient item = items.get(position);
        holder.nameText.setText(item.getName());
        holder.priceText.setText(item.getPrice() + " HUF/L");
        holder.alcoholText.setText(String.valueOf(item.getAlc()) + " %");
        holder.image.setImageResource(getImageResource(item));
        holder.dollar_sign.setImageResource(R.drawable.dollar_sign_grey);
        holder.alcohol_sing.setImageResource(R.drawable.measure_icon_grey);

        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    public void addItem(Ingredient item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void update(List<Ingredient> newitems) {
        listener.setListItemCounter(newitems.size());
        items.clear();
        items.addAll(newitems);

        for(int i = 0; i < items.size(); i++)
        {
            System.out.println(items.get(i).getName() + " " + items.get(i).getId());
        }

        notifyDataSetChanged();
    }



    public void deleteItem(Ingredient item)
    {
        items.remove(item);
        listener.onItemDeleted(item);
        notifyDataSetChanged();
    }

    private @DrawableRes
    int getImageResource(Ingredient item) {
        @DrawableRes int ret;
        double percent = item.getAlc();


        //icons from https://www.flaticon.com/authors/dinosoftlabs from www.flaticon.com
        if(percent == 0.0) ret = R.drawable.drink_non;
        else if (percent > 0.0 && percent <=15.0) ret = R.drawable.drink_alcoholic;
        else if (percent > 15.0 && percent <= 50.0) ret = R.drawable.drink_strong;
        else if (percent > 50.0) ret = R.drawable.drink_xstrong;

        else ret = 0;

        return ret;
    }



    class IngredientsListViewHolder extends RecyclerView.ViewHolder {

        private TextView nameText;
        private TextView alcoholText;
        private TextView priceText;
        private ImageButton removeButton;
        private ImageView image;
        private CardView card;
        private ImageView dollar_sign;
        private ImageView alcohol_sing;


        Ingredient item;


        IngredientsListViewHolder(View itemView, final View view) {
            super(itemView);
            nameText = itemView.findViewById(R.id.IngredientListNameText);
            alcoholText = itemView.findViewById(R.id.IngredientListAlcText);
            priceText = itemView.findViewById(R.id.IngredientListPriceText);
            removeButton = itemView.findViewById(R.id.IngredientListRemoveButton);
            image = itemView.findViewById(R.id.IngredientListItemImage);
            dollar_sign = itemView.findViewById(R.id.price_icon);
            alcohol_sing = itemView.findViewById(R.id.alcohol_icon);



            card = itemView.findViewById(R.id.ingredientListCard);

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.addbtn);

                    deleteItem(item);

                    View.OnClickListener action_listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            //TODO undo deleting
                            listener.showSnackbarWithMessage("Undo ready", null, null);


                        }
                    };

                    listener.showSnackbarWithMessage("Item has been successfully deleted.", "", null);
                }
            });
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });




        }
    }





}
