package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import org.w3c.dom.Attr;

import java.util.Random;

import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.mScreenSizeX;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.mScreenSizeY;

/**
 * Created by Kevin on 11/7/2017.
 */

public class CellGridView extends SurfaceView implements SurfaceHolder.Callback {
    Paint mAliveCellPaint;
    Paint mDeadCellPaint;
    private int mCellSize;
    private long beginTime;
    private long endTime;
    public static int[][] mCellGrid;
    private boolean running;
    private Thread mainloop;
    private int frameCount;
    private long lastTime;
    private int realFPS;
    private int differenceTime;
    private int sleepTime;
    private int framePeriod;
    private int numberOfFramesSkipped;
    private Surface surface;


    public CellGridView(Context context) {
        super(context);
        init();
    }

    public CellGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CellGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    // Initialize the grid
    private void init(){
        mAliveCellPaint = new Paint();
        mAliveCellPaint.setColor(Color.GREEN);
        mAliveCellPaint.setStrokeWidth(2);
        mAliveCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDeadCellPaint = new Paint();
        mDeadCellPaint.setColor(Color.BLACK);
        mDeadCellPaint.setStrokeWidth(2);
        mDeadCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCellGrid = new int[mScreenSizeX][mScreenSizeY];
        RandomizeGrid();
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

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        for(int i = 0; i < mCellGrid.length; i++) {
            for(int j = 0; j < mCellGrid[i].length; j++) {
                if(mCellGrid[i][j] == 1){
                    canvas.drawCircle(i * 8, j * 8, 100, mAliveCellPaint);
                    }
                    else {
                    canvas.drawCircle(i * 8, j * 8, 100, mDeadCellPaint);
                    }
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        surface = surfaceHolder.getSurface();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
