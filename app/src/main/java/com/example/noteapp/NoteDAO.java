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
    List<Notes> getAllNote();

    @Query("SELECT * FROM note WHERE id = :id")
    Notes getNoteById(int id);

    @Query("SELECT * FROM note WHERE user = :user AND `delete` = 1 ORDER BY id DESC")
    List<Notes> getAllDeletedNote(String user);

    @Query("SELECT * FROM note WHERE user = :user AND `delete` = 1 ORDER BY id ASC")
    List<Notes> getAllDeletedNoteAsc(String user);

    @Query("SELECT * FROM note WHERE user = :user  AND `delete` = 0 ORDER BY id DESC")
    List<Notes> getAllUserNote(String user);

    @Query("SELECT * FROM note WHERE user = :user  AND `delete` = 0 ORDER BY id ASC")
    List<Notes> getAllUserNoteInUse(String user);

    @Query("SELECT * FROM note WHERE user = :user ORDER BY id DESC")
    List<Notes> getAllUserNoteAndRBin(String user);

    @Query("SELECT * FROM note WHERE pinned = :tru AND user = :user  AND `delete` = 0 ORDER BY `order` DESC")
    List<Notes> getNoteHasPin(boolean tru, String user);

    @Query("SELECT * FROM note WHERE pinned = :fal AND user = :user AND `delete` = 0 ORDER BY id DESC")
    List<Notes> getNoteNoPin(boolean fal, String user);

    @Query("SELECT MAX(`order`) FROM note WHERE user = :userMail")
    int maxOrderForPin(String userMail);

    @Query("UPDATE note SET `order` = 1 + :maxOrder WHERE id = :id")
    void updateOrder(int id, int maxOrder);

    @Query("UPDATE note SET `order` = 0 WHERE id = :id")
    void unPin(int id);

    @Query("DELETE FROM note")
    void deleteAllNote();

    @Query("DELETE FROM note WHERE user = :user")
    void deleteAllUserNote(String user);

    @Query("UPDATE note SET title = :title, content = :content WHERE id = :id")
    void update(int id, String title, String content);

    @Query("UPDATE note SET label = :label WHERE id = :id")
    void updateCheckLabel(int id, String label);

    @Query("UPDATE note SET password = :pw WHERE id = :id")
    void updatePassNote(int id, String pw);

    @Query("SELECT COUNT(id) FROM note")
    int getCount();

    @Query("SELECT COUNT(id) FROM note WHERE user = :user AND `delete` = 0")
    int getCountNoteUser(String user);

    @Query("UPDATE note SET pinned = :pinned WHERE id = :id")
    void pin(int id, boolean pinned);

    @Query("UPDATE note SET `delete` = 1 WHERE id = :id")
    void deletedNote(int id);

    @Query("UPDATE note SET `delete` = 1")
    void deletedAllNoteToTrash();

    @Query("UPDATE note SET `delete` = 0 WHERE id = :id")
    void recoverNoteDel(int id);

    @Query("UPDATE note SET img_path = :path WHERE id = :id")
    void updateImgPath(int id, String path);

    @Query("UPDATE note SET color_code = :code WHERE id = :id")
    void updateColorNote(int id, String code);

    @Query("UPDATE note SET date_remind = :date WHERE id = :id")
    void updateDateRemind(int id, String date);

    @Query("UPDATE note SET time_remind = :time WHERE id = :id")
    void updateTimeRemind(int id, String time);

    @Query("UPDATE note SET align = :align WHERE id = :id")
    void updateAlign(int id, String align);

    @Query("UPDATE note SET color_text = :color WHERE id = :id")
    void updateColorText(int id, String color);

    @Query("SELECT MAX(orderNoteDel) FROM note WHERE user = :userMail")
    int getMaxOrderDel(String userMail);

    //requestCode for each Note
    @Query("SELECT MAX(request_code) FROM note")
    int getMaxRequestCode();

    @Query("SELECT * FROM note WHERE orderNoteDel = 0")
    List<Notes> noteInUse();

    @Query("SELECT * FROM note WHERE orderNoteDel > 0")
    List<Notes> noteInTrash();

    @Query("SELECT * FROM note WHERE orderNoteDel != 0 AND user = :userMail ORDER BY orderNoteDel DESC")
    List<Notes> showNoteDelInOrder(String userMail);

    @Query("UPDATE note SET orderNoteDel = :orderNoteDel WHERE id = :id")
    void updateNoteOrderDel(int id,int orderNoteDel);

    @Query("UPDATE note SET date_delete = :date WHERE id = :id")
    void updateDateDel(int id, String date);

    @Query("DELETE FROM note WHERE orderNoteDel > 0 AND user = :userMail")
    void deleteAllDelNote(String userMail);

    @Delete
    void delete(Notes notes);
}
