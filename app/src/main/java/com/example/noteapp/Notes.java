package com.example.noteapp;

import android.graphics.Typeface;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "note")
public class Notes implements Serializable {
    @PrimaryKey(autoGenerate = true)
    int id = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ColumnInfo(name = "title")
    String title = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ColumnInfo(name = "user")
    String user = "";

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @ColumnInfo(name = "content")
    String content = "";

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @ColumnInfo(name = "date")
    String date_create = "";

    public String getDate_create() {
        return date_create;
    }

    public void setDate_create(String date_create) {
        this.date_create = date_create;
    }

    @ColumnInfo(name = "pinned")
    boolean pinned = false;

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    @ColumnInfo(name = "delete")
    boolean delete = false;

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    @ColumnInfo(name = "label")
    String label = "";

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @ColumnInfo(name = "order")
    int order = 0;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @ColumnInfo(name = "password")
    String password = "";

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public
    @ColumnInfo(name = "img_path")
    String img_path = "";

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    @ColumnInfo(name = "color_code")
    String color_code = "pink";

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    @ColumnInfo(name = "fontSize")
    String fontSize = "";

    public String getFontSize(){
        return fontSize;
    };

    public void setFontSize(String fontSize){
        this.fontSize = fontSize;
    }

    @ColumnInfo(name = "fontStyle")
    String fontStyle = "";

    public String getFontStyle(){
        return fontStyle;
    }

    public void setFontStyle(String fontStyle){
        this.fontStyle = fontStyle;
    }
}