package hu.bme.aut.mixit.recipe_data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import hu.bme.aut.mixit.model.RecipeItem;

@Dao
public interface RecipeItemDao
{
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    long insert(RecipeItem item);

    @Update
    void update(RecipeItem item);

    @Delete
    void delete(RecipeItem item);

    @Query("SELECT * FROM recipe_item")
    List<RecipeItem> getAllRecipeItems();

    @Query("SELECT * FROM recipe_item WHERE recipeId=:recipeId")
    List<RecipeItem> findRecipeItemsForRecipe(final long recipeId);

}
