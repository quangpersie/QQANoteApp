package com.example.noteapp;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LabelDAO {
    @Insert(onConflict = REPLACE)
    void insert(Label label);

    @Delete
    void deleteLabel(Label label);

    @Query("SELECT * FROM label ORDER BY id DESC")
    List<Label> getAllLabel();

    @Query("SELECT COUNT(id) FROM label")
    int getCountLabel();

    @Query("DELETE FROM label")
    void deleteAllLabel();

    @Query("UPDATE label SET name = :new_name WHERE id = :id")
    void updateName(int id, String new_name);

    @Query("UPDATE label SET checked = :check WHERE id = :id")
    void updateCheck(int id, boolean check);

    /*@Query("UPDATE label SET checked = :check")
    void updateDeleteAll(int id, boolean check);*/
}
