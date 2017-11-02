package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private float mCenterX;
    private float mCenterY;
    private Paint mAliveCellPaint;
    private Paint mDeadCellPaint;
    private Paint mBackgroundPaint;
    private Bitmap mBackgroundBitmap;
    private int mScreenSizeX;
    private int mScreenSizeY;
    private int mCellDrawRadius;
    private View drawingView;

    private int[][] mCellGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawingView = (View) findViewById(R.id.surface);
        setDisplayOptions();
        initGrid();
        initGrayBackgroundBitmap();
    }

    private void setDisplayOptions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenSizeY = displayMetrics.heightPixels;
        mScreenSizeX = displayMetrics.widthPixels;
    }

    private void initGrid() {
        mCellGrid = new int[mScreenSizeX][mScreenSizeY];
    }

    private void randomizeGrid() {
        Random rand = new Random();

        for(int i = 0; i < mCellGrid.length; i++) {
            for(int j = 0; j < mCellGrid.length; j++) {
                int n = rand.nextInt(10)+ 1;
                if(n == 1) {
                    mCellGrid[i][j] = 1;
                }
                else {
                    mCellGrid[i][j] = 0;
                }
            }
        }
    }

    private void initGrayBackgroundBitmap() {
        mBackgroundBitmap = Bitmap.createBitmap(
                mScreenSizeX,
                mScreenSizeY,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBackgroundBitmap);
        Paint blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        canvas.drawBitmap(mBackgroundBitmap, 0, 0, blackPaint);
        View view = (View) findViewById(R.id.surface);
        view.draw(canvas);
    }
}
