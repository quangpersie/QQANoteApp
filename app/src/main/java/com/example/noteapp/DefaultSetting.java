package com.example.noteapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "settings")
public class DefaultSetting implements Serializable {
    @PrimaryKey(autoGenerate = true)
    int id = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ColumnInfo(name = "delete_default")
    String delete_default = "1 phút";

    public String getDelete_default() {
        return delete_default;
    }

    public void setDelete_default(String delete_default) {
        this.delete_default = delete_default;
    }

    @ColumnInfo(name = "sound_default")
    String sound_default = "Default";

    public String getSound_default() {
        return sound_default;
    }

    public void setSound_default(String sound_default) {
        this.sound_default = sound_default;
    }

    @ColumnInfo(name = "font_default")
    String font_default = "Mặc định";

    public String getFont_default() {
        return font_default;
    }

    public void setFont_default(String font_default) {
        this.font_default = font_default;
    }

    @ColumnInfo(name = "size_default")
    String size_default = "Bình thường";

    public String getSize_default() {
        return size_default;
    }

    public void setSize_default(String size_default) {
        this.size_default = size_default;
    }
}
