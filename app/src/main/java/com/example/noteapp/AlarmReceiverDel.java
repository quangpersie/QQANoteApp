package com.example.noteapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiverDel extends BroadcastReceiver {
    Context mContext;
    RoomDB db;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        int idDelNote = intent.getIntExtra("idNoteDelAuto",0);
        db = RoomDB.getInstance(context);
        db.noteDAO().delete(db.noteDAO().getNoteById(idDelNote));
    }
}