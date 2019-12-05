package hu.bme.aut.mixit.measure_data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import hu.bme.aut.mixit.model.Ingredient;
import hu.bme.aut.mixit.model.MeasureData;

@Dao
public interface MeasureDataDao
{
    @Query("SELECT * FROM measure_table")
    List<MeasureData> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MeasureData item);

    @Update
    void update(MeasureData item);

    @Delete
    void deleteItem(MeasureData item);

    @Query("DELETE FROM measure_table WHERE id=:id")
    void deleteByID(final long id);

    @Query("SELECT * FROM measure_table WHERE id=:id LIMIT 1")
    MeasureData findMeasureDataByID(final long id);

    @Query("SELECT * FROM measure_table ORDER BY datetime")
    List<MeasureData> getAllOrderedByDatetime();

    @Query("SELECT * FROM measure_table ORDER BY datetime DESC")
    List<MeasureData> getAllOrderedByDatettimeDesc();
}
