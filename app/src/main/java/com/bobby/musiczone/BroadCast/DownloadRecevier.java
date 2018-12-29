package com.bobby.musiczone.BroadCast;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.bobby.musiczone.util.ID3Tags.ID3TagUtils;
import com.bobby.musiczone.util.ID3Tags.ID3Tags;

import java.io.File;

public class DownloadRecevier extends BroadcastReceiver {
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        String action=intent.getAction();
        switch (action)
        {
            case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                Toast.makeText(context.getApplicationContext(), "下载已完成", Toast.LENGTH_SHORT).show();
                long id=intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(id);
                Cursor cursor = manager.query(query);
                String TAG="downloadRecevier";
                if (!cursor.moveToFirst()) {
                    Log.e(TAG, "cursor is null");
                    cursor.close();
                    return;
                }
                String musicPath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                File musicFile = new File(musicPath);
                ID3Tags id3Tags=new ID3Tags.Builder().build();
                ID3TagUtils.setID3Tags(musicFile, id3Tags, false);
        }
    }
}
