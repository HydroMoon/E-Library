package com.hydro.library;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.net.URISyntaxException;

/**
 * Created by mohand on 08/06/2017.
 */
class FileUtils {
    static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())){
            String[] projection = {"_data"};
            Cursor cursor;

            try {
                cursor = context.getContentResolver().query(uri,projection,null,null,null);
                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (!cursor.moveToFirst()){
                    return cursor.getString(column_index);
                }
            }catch (Exception e){
                Log.e("i", "OOPs" + e.toString());
            }
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            return uri.getPath();
        }
        return null;
    }
}
