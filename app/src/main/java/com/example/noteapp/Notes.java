package com.example.noteapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "note")
public class Notes implements Serializable {
    @PrimaryKey(autoGenerate = true)
    int id = 0;

    @ColumnInfo(name = "title")
    String title = "";

    @ColumnInfo(name = "user")
    String user = "";

    @ColumnInfo(name = "content")
    String content = "";

    @ColumnInfo(name = "date")
    String date_create = "";

    @ColumnInfo(name = "pinned")
    boolean pinned = false;

    @ColumnInfo(name = "delete")
    boolean delete = false;

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @ColumnInfo(name = "order")
    int order = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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