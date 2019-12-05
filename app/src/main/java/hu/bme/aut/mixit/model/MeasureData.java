package hu.bme.aut.mixit.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName ="measure_table")
public class MeasureData
{

    /**Id of the ingredient*/
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true)
    private long id;

    /**Stores date and time when user consumed beverage*/
    @ColumnInfo(name="datetime")
    private String datetime;

    /**Stores date name of the consumed beverage*/
    @ColumnInfo(name="beveragename")
    private String beverage_name;

    /**Stores the amount of the consumed beverage*/
    @ColumnInfo(name="amount")
    private int amount;

    /**Stores the absolute alcohol content of the consumed beverage*/
    @ColumnInfo(name="alcohol_content")
    private int alcohol_content;

    @Ignore
    public MeasureData(long id, String datetime, String beverage_name, int amount) {
        this.id = id;
        this.datetime = datetime;
        this.beverage_name = beverage_name;
        this.amount = amount;
    }

    public MeasureData() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getBeverage_name() {
        return beverage_name;
    }

    public void setBeverage_name(String beverage_name) {
        this.beverage_name = beverage_name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAlcohol_content() {
        return alcohol_content;
    }

    public void setAlcohol_content(int alcohol_content) {
        this.alcohol_content = alcohol_content;
    }
}
