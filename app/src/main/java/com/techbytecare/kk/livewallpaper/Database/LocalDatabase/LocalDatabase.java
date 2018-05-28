package com.techbytecare.kk.livewallpaper.Database.LocalDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.techbytecare.kk.livewallpaper.Database.Recents;

import static com.techbytecare.kk.livewallpaper.Database.LocalDatabase.LocalDatabase.DATABASE_VERSION;

@Database(entities = Recents.class,version = DATABASE_VERSION)
public abstract class LocalDatabase extends RoomDatabase {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "TBC_LIVE_WALLPAPER";

    public abstract RecentDAO recentDAO();

    public static LocalDatabase instance;

    public static LocalDatabase getInstance(Context context)    {

        if (instance == null)   {
            instance = Room.databaseBuilder(context,LocalDatabase.class,DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return instance;
    }
}
