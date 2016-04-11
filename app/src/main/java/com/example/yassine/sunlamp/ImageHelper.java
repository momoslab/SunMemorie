package com.example.yassine.sunlamp; /**
 * Created by YassIne on 25/08/2015.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;

public class ImageHelper {
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /*
    Prende il percorso della foto e l'imageView e adatta l'immagine alla imageView

    public static void setFullImageFromFilePath(String imagePath, ImageView imageView) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    public static void setFullImageFromFilePath(final String imagePath, final ImageView imageView) {
        // Get the dimensions of the View

        imageView.post(new Runnable() {
            @Override
            public void run() {
                int targetW = imageView.getWidth();
                int targetH = imageView.getHeight();

                /*
                Test using picasso to resize an image

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imagePath, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
                imageView.setImageBitmap(bitmap);
            }
        });

    }*/

    public static void setFullImageFromFilePath(final Context context,final String imagePath, final ImageView imageView) {
        // Get the dimensions of the View

        imageView.post(new Runnable() {
            @Override
            public void run() {
                final int targetW = imageView.getWidth();
                final int targetH = imageView.getHeight();
                /*
                 * using picasso to resize an image
                 */

                if (targetH > 0 && targetW > 0) {
                    Picasso.with(context)
                            .load(new File(imagePath))
                            .resize(targetW, targetH)
                            .centerCrop()
                            .into(imageView);
                    }
                }
            });
    }

    public static void setRoundedImageFromFilePath(final Context context,final String imagePath, final ImageView imageView) {
        // Get the dimensions of the View

        imageView.post(new Runnable() {
            @Override
            public void run() {
                final int targetW = imageView.getWidth();
                final int targetH = imageView.getHeight();
                /*
                 * using picasso to resize an image
                 */
                if (targetH > 0 && targetW > 0) {
                    Picasso.with(context)
                            .load(new File(imagePath))
                            .resize(targetW, targetH)
                            .transform(new CircleTransform(10,0))
                            .centerCrop()
                            .into(imageView);
                    }
                }
            });
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void blur(final Bitmap bkg, final View view, final Context context) {

        if(bkg == null)
            return;

        view.post( new Runnable() {
            @Override
            public void run() {
                float scaleFactor = 7;
                float radius = 15;

                Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth()/scaleFactor),
                        (int) (view.getMeasuredHeight()/scaleFactor), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(overlay);
                canvas.translate(-view.getLeft()/scaleFactor, -view.getTop()/scaleFactor);
                canvas.scale(1 / scaleFactor, 1 / scaleFactor);
                Paint paint = new Paint();
                paint.setFlags(Paint.FILTER_BITMAP_FLAG);
                canvas.drawBitmap(bkg, 0, 0, paint);

                overlay = Blur.fastblur(context, overlay, (int)radius);

                view.setBackground(new BitmapDrawable(context.getResources(), overlay));
            }
        });

    }
}