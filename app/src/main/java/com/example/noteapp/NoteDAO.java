package com.example.noteapp;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteDAO {

    @Insert(onConflict = REPLACE)
    void insert(Notes notes);

    @Query("SELECT * FROM note ORDER BY id DESC")
    List<Notes> getAll();

    @Query("UPDATE note SET title = :title, content = :content WHERE id = :id")
    void update(int id, String title, String content);

    @Query("SELECT COUNT(id) FROM note")
    int getCount();

    @Query("UPDATE note SET pinned = :pinned WHERE id = :id")
    void pin(int id, boolean pinned);

    @Delete
    void delete(Notes notes);
}
