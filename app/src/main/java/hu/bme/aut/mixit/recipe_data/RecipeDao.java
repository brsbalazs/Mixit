package hu.bme.aut.mixit.recipe_data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import hu.bme.aut.mixit.model.Ingredient;
import hu.bme.aut.mixit.model.Recipe;

@Dao
public interface RecipeDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    long insert(Recipe recipe);

    @Update
    void update(Recipe recipe);

    @Delete
    void delete(Recipe recipe);

    @Delete
    void deleteItem(Recipe item);

    @Query("SELECT * FROM recipe")
    List<Recipe> getAll();

    @Query("SELECT * FROM recipe WHERE id = :id LIMIT 1 ")
    Recipe getById(long id);

    @Query("SELECT * FROM recipe ORDER BY name")
    List<Recipe> getAllAZ();

    @Query("SELECT * FROM recipe ORDER BY name DESC")
    List<Recipe> getAllZA();

    @Query("SELECT * FROM recipe WHERE name LIKE :search ")
    List<Recipe> searchForString(final String search);
}
