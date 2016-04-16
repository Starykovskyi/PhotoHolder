package com.starikovskiy_cr.scope_8.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v4.content.AsyncTaskLoader;

import com.starikovskiy_cr.scope_8.util.FileInfo;

/**
 * Created by starikovskiy_cr on 12.03.16.
 */
public class FileDBDownLoader extends AsyncTaskLoader<Bitmap> {
    private FileInfo info;

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    private FileDBDownLoader(Context context) {
        super(context);
    }

    public FileDBDownLoader(Context context, FileInfo info) {
        this(context);
        this.info = info;
    }


    @Override
    public Bitmap loadInBackground() {
        return decodeSampledBitmap(info.getPath(), info.getWidth(), info.getHeight());
    }


    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Bitmap decodeSampledBitmap(String pathName, int reqWidth, int reqHeight) {

        // first decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        // calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap sourceBitmap = BitmapFactory.decodeFile(pathName, options);

        //rotate image if it need
        try {
            ExifInterface exif = new ExifInterface(pathName);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            sourceBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true); // rotating bitmap
        } catch (Exception ignore) {/*wrong file*/}
        return sourceBitmap;
    }
}