package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Random;

import static mobileappdevelopment.kevinholmes.gameoflife.CellGridView.mCellGrid;

public class MainActivity extends AppCompatActivity {
    private Paint mAliveCellPaint;
    private Paint mDeadCellPaint;
    private Paint mBackgroundPaint;
    private SurfaceView mCellGridView;
    public static int mScreenSizeX;
    public static int mScreenSizeY;
    public static final String TAG = "main";
    private SurfaceHolder holder;
    private Surface surface;
    private int [][] mCellGrid;
    private boolean gridInitialized;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAliveCellPaint = new Paint();
        mAliveCellPaint.setColor(Color.GREEN);
        mAliveCellPaint.setStrokeWidth(2);
        mAliveCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDeadCellPaint = new Paint();
        mDeadCellPaint.setColor(Color.BLACK);
        mDeadCellPaint.setStrokeWidth(2);
        mDeadCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        setContentView(R.layout.activity_main);
        mCellGridView = (SurfaceView) findViewById(R.id.surface);
        // drawingView = (SurfaceView) findViewById(R.id.surface);

        holder = mCellGridView.getHolder();

        holder.addCallback(callback);

        /*Surface surface = holder.getSurface();
        surface.unlockCanvasAndPost(null);
        Canvas canvas = surface.lockCanvas(null);
        mScreenSizeX = canvas.getWidth();
        mScreenSizeY = canvas.getHeight();
        surface.unlockCanvasAndPost(canvas);*/

        // initGrid();
        // RandomizeGrid();
    }

    public void initGrid() {

    }

    public void RandomizeGrid() {
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

    SurfaceHolder.Callback2 callback = new SurfaceHolder.Callback2() {
        @Override
        public void surfaceRedrawNeeded(SurfaceHolder holder) {

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if(gridInitialized == false) {
                gridInitialized = true;
                Surface surface = holder.getSurface();
                Canvas canvas = surface.lockCanvas(null);
                mScreenSizeX = canvas.getWidth();
                mScreenSizeY = canvas.getHeight();
                mCellGrid = new int[mScreenSizeX][mScreenSizeY];
                RandomizeGrid();
            }
            startPaint();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            stopPaint();
            startPaint();

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stopPaint();
        }
    };

    private void startPaint() {

        mCellGridView.setOnTouchListener(touchListener);


    }

    private void stopPaint() {
        surface = null;

        mCellGridView.setOnTouchListener(null);
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                paintStartDot(event.getX(), event.getY());
                mCellGridView.setOnTouchListener(null);

            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                paintEndDot(event.getX(), event.getY());
                mCellGridView.setOnTouchListener(null);
            }

            return true;
        }

    };

    private int paintColor = 0xffff0000;
    private Paint drawPaint = new Paint();

    {
        drawPaint.setColor(paintColor);
//        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    float lastX, lastY;

    private void paintStartDot(float x, float y) {


        lastX = x;
        lastY = y;
    }

    private void paintEndDot(float x, float y) {
        Canvas canvas = surface.lockCanvas(null);

        // canvas.drawLine(lastX, lastY, x, y, drawPaint);

        for(int i = 0; i < mCellGrid.length; i++) {
            for(int j = 0; j < mCellGrid[i].length; j++) {
                if(mCellGrid[i][j] == 1){
                    canvas.drawPoint(i, j, mAliveCellPaint);
                }
                else {
                    canvas.drawPoint(i, j, mDeadCellPaint);
                }
            }
        }

        surface.unlockCanvasAndPost(canvas);

        lastX = x;
        lastY = y;
    }
}

