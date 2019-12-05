package hu.bme.aut.mixit.measure_data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import hu.bme.aut.mixit.model.MeasureData;

@Database(entities = { MeasureData.class},
        version = 1,
        exportSchema = false)
public abstract class MeasureDatabase extends RoomDatabase {


    public abstract MeasureDataDao measureDataDao();


}
