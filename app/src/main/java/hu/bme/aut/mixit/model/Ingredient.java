package hu.bme.aut.mixit.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName ="ingredient")
public class Ingredient
{

    /**Id of the ingredient*/
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true)
    private long id;

    /**name of the ingredient*/
    @ColumnInfo(name="name")
    private String name;

    /**alcohol contained in percentage*/
    @ColumnInfo(name="alcohol")
    private double alc;

    /**price of on liter in huf*/
    @ColumnInfo(name="price")
    private int price;

    @Ignore
    public Ingredient(String name, double alc, final long id, int price) {
        this.name = name;
        this.alc = alc;
        this.id = id;
        this.price = price;
    }

    @Ignore
    public Ingredient(String name, double alc, int price) {
        this.name = name;
        this.alc = alc;
        this.price = price;
    }

    public Ingredient() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAlc() {
        return alc;
    }

    public void setAlc(double alc) {
        this.alc = alc;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
