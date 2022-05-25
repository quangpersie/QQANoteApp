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

    @Query("SELECT * FROM note WHERE user = :user AND `delete` = 1 ORDER BY id DESC")
    List<Notes> getAllDeletedNote(String user);

    @Query("SELECT * FROM note WHERE user = :user  AND `delete` = 0 ORDER BY id DESC")
    List<Notes> getAllUserNote(String user);

    @Query("SELECT * FROM note WHERE pinned = :tru AND user = :user  AND `delete` = 0 ORDER BY `order` DESC")
    List<Notes> getNoteHasPin(boolean tru, String user);

    @Query("SELECT * FROM note WHERE pinned = :fal AND user = :user AND `delete` = 0 ORDER BY id DESC")
    List<Notes> getNoteNoPin(boolean fal, String user);

    @Query("UPDATE note SET `order` = 1 + :maxOrder WHERE id = :id")
    void updateOrder(int id, int maxOrder);

    @Query("UPDATE note SET `order` = 0 WHERE id = :id")
    void unPin(int id);

    @Query("DELETE FROM note")
    void deleteAll();

    @Query("DELETE FROM note WHERE user = :user")
    void deleteAllUserNote(String user);

    @Query("UPDATE note SET title = :title, content = :content WHERE id = :id")
    void update(int id, String title, String content);

    @Query("SELECT COUNT(id) FROM note")
    int getCount();

    @Query("UPDATE note SET pinned = :pinned WHERE id = :id")
    void pin(int id, boolean pinned);

    @Query("UPDATE note SET `delete` = 1 WHERE id = :id")
    void deletedNote(int id);

    @Query("UPDATE note SET `delete` = 0 WHERE id = :id")
    void recoverNoteDel(int id);

    /*@Query("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'note'")
    void clearPrimaryKey();*/

    @Delete
    void delete(Notes notes);
}
