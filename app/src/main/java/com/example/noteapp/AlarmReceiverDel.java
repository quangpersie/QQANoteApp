package com.example.noteapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiverDel extends BroadcastReceiver {
    Context mContext;
    RoomDB db;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Log.e("MS","ReceivedDel");

        int idDelNote = intent.getIntExtra("idNoteDelAuto",0);
        db = RoomDB.getInstance(context);
        db.noteDAO().delete(db.noteDAO().getNoteById(idDelNote));
        Log.e("MS","Deleted");
    }
}