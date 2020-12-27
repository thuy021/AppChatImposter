package com.example.appchat.Helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.load.engine.Resource;

public class MyButton {
    private String text;
    private int imageResID, textSize, color, pos;
    private RectF clickRegion;
    private MyButtonClickListener listener;
    private Context context;
    private Resources resources;

    public MyButton(Context context, String text, int textSize, int imageResID, int color, MyButtonClickListener listener) {
        this.text = text;
        this.imageResID = imageResID;
        this.textSize = textSize;
        this.color = color;
        this.listener = listener;
        this.context = context;
        resources = context.getResources();
    }

    public boolean onClick(float x, float y) {
        if (clickRegion != null && clickRegion.contains(x, y)) {
            listener.onClick(pos);
            return true;
        }
        return false;
    }

    public void onDraw(Canvas c, RectF rectF, int pos) {
        Paint p = new Paint();
        p.setColor(color);
        c.drawRect(rectF, p);
        p.setColor(Color.WHITE);
        p.setTextSize(textSize);

        Rect r = new Rect();
        float cHeight = rectF.height();
        float cWidth = rectF.width();
        p.setTextAlign(Paint.Align.LEFT);
        p.getTextBounds(text, 0, text.length(), r);

        float x = 0, y = 0;
        if (imageResID == 0) {
            x = cWidth / 2f - r.width() / 2f - r.left;
            y = cHeight / 2f + r.height() / 2f - r.bottom;
            c.drawText(text, rectF.left + x, rectF.top + y, p);
        } else {
            Drawable d = ContextCompat.getDrawable(context, imageResID);
            Bitmap bitmap = drawableToBitmap(d);
            x = (cWidth / 2f - r.width() / 2f) / 1.5f;
            y = (cHeight / 2f + r.height() / 2f) / 1.5f;
            c.drawBitmap(bitmap, (rectF.left + x), (rectF.top + y), p);
        }
        clickRegion = rectF;
        this.pos = pos;
    }

    private Bitmap drawableToBitmap(Drawable d) {
        if (d instanceof BitmapDrawable) {
            return ((BitmapDrawable) d).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        return bitmap;
    }

}
