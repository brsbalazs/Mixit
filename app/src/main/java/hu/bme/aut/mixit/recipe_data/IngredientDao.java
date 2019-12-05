package hu.bme.aut.mixit.recipe_data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import hu.bme.aut.mixit.model.Ingredient;

@Dao
public interface IngredientDao {

    @Query("SELECT * FROM ingredient")
    List<Ingredient> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Ingredient item);

    @Update
    void update(Ingredient item);

    @Delete
    void deleteItem(Ingredient item);

    @Query("SELECT * FROM ingredient WHERE id=:id LIMIT 1")
    Ingredient findIngredientByID(final long id);

    @Query("SELECT * FROM ingredient ORDER BY name")
    List<Ingredient> getAllAZ();

    @Query("SELECT * FROM ingredient ORDER BY name DESC")
    List<Ingredient> getAllZA();

    @Query("SELECT * FROM ingredient WHERE name LIKE :search ")
    List<Ingredient> searchForString(final String search);
}
