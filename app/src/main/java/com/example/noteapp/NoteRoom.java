package com.example.noteapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "MyNotes")
public class NoteRoom implements Serializable {
    @PrimaryKey(autoGenerate = true)
    int id = 0;

    @ColumnInfo(name = "title_note")
    String title_note = "";

    @ColumnInfo(name = "desc_note")
    String desc_note = "";

    @ColumnInfo(name = "date_create")
    String date_create = "";

    @ColumnInfo(name = "pinned")
    boolean pinned = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle_note() {
        return title_note;
    }

    public void setTitle_note(String title_note) {
        this.title_note = title_note;
    }

    public String getDesc_note() {
        return desc_note;
    }

    public void setDesc_note(String desc_note) {
        this.desc_note = desc_note;
    }

    public String getDate_create() {
        return date_create;
    }

    public void setDate_create(String date_create) {
        this.date_create = date_create;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
}
