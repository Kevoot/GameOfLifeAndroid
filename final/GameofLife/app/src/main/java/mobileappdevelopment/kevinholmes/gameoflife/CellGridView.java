package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.mScreenSizeX;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.mScreenSizeY;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.xAdjust;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.xGridSize;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.yAdjust;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.yGridSize;

/**
 * Created by Kevin on 11/9/2017.
 */

public class CellGridView extends View
{
    Paint paint;
    public CellGridView(Context context)
    {
        super(context);
        paint = new Paint();
        mScreenSizeX = getWidth();
        xGridSize = mScreenSizeX / xAdjust;
        mScreenSizeY = getHeight();
        yGridSize = mScreenSizeY / yAdjust;
        // mCellGrid = new int[xGridSize][yGridSize];
        // RandomizeGrid();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int x = getWidth();
        int y = getHeight();
        int radius;
        radius = 100;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
        // Use Color.parseColor to define HTML colors
        paint.setColor(Color.parseColor("#CD5C5C"));
        canvas.drawCircle(x / 2, y / 2, radius, paint);
    }
}
