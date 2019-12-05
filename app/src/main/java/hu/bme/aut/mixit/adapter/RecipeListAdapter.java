package hu.bme.aut.mixit.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.mixit.R;
import hu.bme.aut.mixit.model.Ingredient;
import hu.bme.aut.mixit.model.Recipe;
import hu.bme.aut.mixit.model.RecipeItem;
import hu.bme.aut.mixit.recipes_activities.RecipeDetialsViewActivity;

public class RecipeListAdapter
        extends RecyclerView.Adapter<RecipeListAdapter.RecipeListViewHolder> {

    private final List<Recipe> items;

    private RecipeClickListener listener;
    private Context context;

    private FloatingActionButton fab;

    public void setFab(FloatingActionButton fab){this.fab = fab;}

    public void setContext(Context context) {
        this.context = context;
    }

    public interface RecipeClickListener {
        void onItemChanged(Recipe item);
        void onItemDeleted(Recipe item);
        List<Ingredient> getIngredients();
        List<RecipeItem> getRecipeItems();
        void setListItemCounter(int numberOfListItems);
    }



    public RecipeListAdapter(RecipeClickListener listener) {

        this.listener = listener;
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecipeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeListViewHolder(itemView, context);
    }

    /**Creating the appearance of the listelement*/
    @Override
    public void onBindViewHolder(@NonNull RecipeListViewHolder holder, int position) {

        Recipe item = items.get(position);

        holder.nameText.setText(item.getName());

        holder.priceText.setText(item.getPrice()+" HUF/ " + item.getAmount() + " ml");
        holder.alcoholText.setText((item.getAlc()) + " %");

        holder.image.setImageResource(getImageResource(item));
        holder.dollar_sign.setImageResource(R.drawable.dollar_sign_grey);
        holder.alcohol_sing.setImageResource(R.drawable.measure_icon_grey);

        holder.item = item;
    }

    public double recipeAlcPercent(Recipe recipe)
    {
        int counter = 0;
        double sumalc = 0;
        List<RecipeItem> recipeItems = listener.getRecipeItems();
        List<Ingredient> ingredients = listener.getIngredients();

        for(int i = 0; i < recipeItems.size(); i++)
        {
            if(recipeItems.get(i).getRecipeId() == recipe.getId())
            {
                counter++;

                for(int y = 0; y < ingredients.size(); y++)
                {
                    if(ingredients.get(y).getId() == recipeItems.get(i).getIngredient_id())
                    {
                        sumalc += ingredients.get(y).getAlc() * recipeItems.get(i).getAmount();
                    }
                }

            }
        }

        return Math.round((counter/sumalc)*100.0);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    public void addItem(Recipe item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void update(List<Recipe> newitems) {

        listener.setListItemCounter(newitems.size());
        items.clear();
        items.addAll(newitems);

        notifyDataSetChanged();
    }


    public void deleteItem(Recipe item)
    {
        items.remove(item);
        listener.onItemDeleted(item);
        notifyDataSetChanged();
    }

    private @DrawableRes
    int getImageResource(Recipe item) {

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


    class RecipeListViewHolder extends RecyclerView.ViewHolder {

        private TextView nameText;
        private TextView alcoholText;
        private TextView priceText;
        private ImageButton removeButton;
        private ImageView image;
        private CardView card;
        private ImageView dollar_sign;
        private ImageView alcohol_sing;
        private Context context;


        Recipe item;


        RecipeListViewHolder(final View itemView, final Context context) {
            super(itemView);
            nameText = itemView.findViewById(R.id.RecipeListNameText);
            alcoholText = itemView.findViewById(R.id.RecipeListAlcText);
            priceText = itemView.findViewById(R.id.RecipeListPriceText);
            removeButton = itemView.findViewById(R.id.RecipeListRemoveButton);
            image = itemView.findViewById(R.id.RecipeListItemImage);
            dollar_sign = itemView.findViewById(R.id.price_icon);
            alcohol_sing = itemView.findViewById(R.id.alcohol_icon);



            card = itemView.findViewById(R.id.recipeListCard);

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteItem(item);
                }
            });

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, RecipeDetialsViewActivity.class);
                    intent.putExtra("RECIPE_ID", item.getId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });


        }
    }





}
