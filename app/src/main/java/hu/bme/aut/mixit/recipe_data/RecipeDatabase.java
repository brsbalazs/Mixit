package hu.bme.aut.mixit.recipe_data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import hu.bme.aut.mixit.model.Ingredient;
import hu.bme.aut.mixit.model.Recipe;
import hu.bme.aut.mixit.model.RecipeItem;

@Database(entities = { RecipeItem.class, Recipe.class, Ingredient.class},
          version = 1,
          exportSchema = false)
public abstract class RecipeDatabase extends RoomDatabase {


    public abstract IngredientDao ingredientDao();
    public abstract RecipeDao recipeDao();
    public abstract RecipeItemDao recipeItemDao();

}
