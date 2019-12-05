package hu.bme.aut.mixit.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity=Recipe.class, parentColumns = "id", childColumns = "recipeId", onDelete = CASCADE),
        tableName = "recipe_item",
        indices = {@Index("recipeId")}
)
public class RecipeItem
{


    /**the ingredient needed for the recipe*/
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true)
    private long id;

    /**the ingredient needed for the recipe*/
    @ColumnInfo(name="ingredient_id")
    private long ingredient_id;

    /**the amount of the ingredient neede for the recipe - in ml*/
    @ColumnInfo(name="amount")
    private double amount;

    private long recipeId;

    @Ignore
    public RecipeItem()
    {

    }

    public RecipeItem(long id, long ingredient_id, double amount, long recipeId) {
        this.id = id;
        this.ingredient_id = ingredient_id;
        this.amount = amount;
        this.recipeId = recipeId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIngredient_id() {
        return ingredient_id;
    }

    public void setIngredient_id(long ingredient_id) {
        this.ingredient_id = ingredient_id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }
}
