package DataBase.gmapspot;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Gmapspot.class, exportSchema = false, version = 1)
public abstract class GmapDatabase extends RoomDatabase {
    private static GmapDatabase database;
    private static final String DB_NAME = "gmapspot";

    public static synchronized GmapDatabase getInstance(Context context){
        if(database == null){
            database = Room.databaseBuilder(context.getApplicationContext(), GmapDatabase.class, DB_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return database;
    }
    public abstract GmapspotDao GmapspotDao();
}
