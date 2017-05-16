package com.mg.mediaplayer.util;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;

import com.mg.utils.adapter.RecyclerViewCursorAdapter;

/**
 * Created by YJ on 2017/5/14.
 */

public class SimpleQueryHandler extends AsyncQueryHandler {
    public SimpleQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
//        CursorUtil.printCursor(cursor);
        if (cookie != null && cookie instanceof RecyclerViewCursorAdapter) {
            RecyclerViewCursorAdapter adapter = (RecyclerViewCursorAdapter) cookie;
            adapter.changeCursor(cursor);//  相当于notifyDatesetChange
        }
    }
}
