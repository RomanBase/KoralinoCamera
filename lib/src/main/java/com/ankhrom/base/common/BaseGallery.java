package com.ankhrom.base.common;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ankhrom.base.GlobalCode;

public class BaseGallery {

    private final Activity activity;

    private BaseGallery(Activity activity) {
        this.activity = activity;
    }

    public static BaseGallery with(@NonNull Activity activity) {

        return new BaseGallery(activity);
    }

    public BaseGallery open() {

        activity.startActivityForResult(
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                GlobalCode.GALLERY_REQUEST
        );

        return this;
    }

    @Nullable
    public Bitmap getOnActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GlobalCode.GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null) {

            try {
                Uri uri = data.getData();
                String[] file = new String[]{MediaStore.Images.Media.DATA};

                Cursor cursor = activity.getContentResolver().query(uri, file, null, null, null);

                if (cursor != null) {
                    cursor.moveToFirst();

                    int index = cursor.getColumnIndex(file[0]);
                    String path = cursor.getString(index);

                    cursor.close();

                    return BitmapFactory.decodeFile(path);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
