package com.example.noteapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

<<<<<<< HEAD
@Database(entities = {Notes.class, Label.class}, version = 2, exportSchema = false)
=======
@Database(entities = {Notes.class, Label.class}, version = 7, exportSchema = false)
>>>>>>> 1f6a31e7af89080d2be3beedc495a2476d26a21f
public abstract class RoomDB extends RoomDatabase {
    private static RoomDB database;
    private static String DATABASE_NAME = "QQANote";

    public synchronized static RoomDB getInstance(Context context) {
        if(database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(),
                    RoomDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    public abstract NoteDAO noteDAO();
    public abstract LabelDAO labelDAO();
}
