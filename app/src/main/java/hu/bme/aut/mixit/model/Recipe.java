package hu.bme.aut.mixit.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity
public class Recipe
{
    /**the name of the beverage*/
    @ColumnInfo(name="name")
    private String name;

    /**the ingredients of the beverage*/
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true)
    private long id;

    /**the alcohol content of the beverage*/
    @ColumnInfo(name="alc")
    private double alc;

    /**price of 1 liter*/
    @ColumnInfo(name="price")
    private int price;

    /**amount of the beverage*/
    @ColumnInfo(name="amount")
    private int amount;

    public Recipe(String name, long id) {
        this.name = name;
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Ignore
    public Recipe() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
