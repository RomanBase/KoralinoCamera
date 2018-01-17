package com.ankhrom.base.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ankhrom.base.GlobalCode;

public class BaseCamera {

    private static final String BITMAP_DATA = "data";

    private Uri uri;

    BaseCamera(Uri uri) {
        this.uri = uri;
    }

    public static boolean isCameraAvailable(Context context) {

        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static BaseCamera.Builder with(@NonNull Activity activity) {

        return new Builder(activity);
    }

    @Nullable
    public Bitmap getOnActivityResult(int requestCode, int resultCode, Intent data) {

        return uri == null ? onActivityResult(requestCode, resultCode, data) : onActivityResult(requestCode, resultCode, data, uri);
    }

    void openCamera(Activity activity) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intent, GlobalCode.CAMERA_REQUEST);
        }
    }

    void openCamera(Activity activity, Uri uri) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intent, GlobalCode.CAMERA_REQUEST);
        }
    }

    @Nullable
    private Bitmap onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GlobalCode.CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();

                return (Bitmap) extras.get(BITMAP_DATA);
            }
        }

        return null;
    }

    @Nullable
    private Bitmap onActivityResult(int requestCode, int resultCode, Intent data, Uri uri) {

        if (requestCode == GlobalCode.CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getExtras() != null) {

                return (Bitmap) data.getExtras().get(BITMAP_DATA);
            } else {

                return BitmapFactory.decodeFile(uri.getPath());
            }
        }

        return null;
    }

    public static class Builder {

        private final Activity activity;
        private Uri uri;

        Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder output(Uri uri) {

            this.uri = uri;
            return this;
        }

        public BaseCamera open() {

            BaseCamera camera = new BaseCamera(uri);

            if (isCameraAvailable(activity)) {

                if (uri != null) {
                    camera.openCamera(activity, uri);
                } else {
                    camera.openCamera(activity);
                }
            }

            return camera;
        }
    }
}
