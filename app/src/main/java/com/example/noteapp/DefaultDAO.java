package com.example.noteapp;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DefaultDAO {
    @Insert(onConflict = REPLACE)
    void insert(DefaultSetting df);

    @Query("SELECT COUNT(id) FROM settings")
    int getCount();

    @Query("SELECT * FROM settings")
    List<DefaultSetting> getAllDF();

    @Query("SELECT * FROM settings WHERE id = :id")
    DefaultSetting getSettingById(int id);

    @Query("UPDATE settings SET delete_default = :time")
    void updateTimeDelAuto(String time);

    @Query("UPDATE settings SET sound_default = :sound")
    void updateDefaultSound(String sound);

    @Query("UPDATE settings SET font_default = :font")
    void updateDefaultFont(String font);

    @Query("UPDATE settings SET size_default = :size")
    void updateDefaultSize(String size);

    @Delete
    void delete(DefaultSetting df);
}
