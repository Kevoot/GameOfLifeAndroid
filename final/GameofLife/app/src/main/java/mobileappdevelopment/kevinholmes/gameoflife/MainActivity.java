package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Paint mAliveCellPaint;
    private Paint mDeadCellPaint;
    private Paint mBackgroundPaint;
    public static int mScreenSizeX;
    public static int mScreenSizeY;
    public static int xGridSize;
    public static int yGridSize;
    public static int xAdjust;
    public static int yAdjust;
    public Handler mHandler;
    public static final String TAG = "main";
    private int [][] mCellGrid;
    private boolean gridInitialized;
    private CellGridView mCellGridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        xAdjust = 10;
        yAdjust = 10;

        mAliveCellPaint = new Paint();
        mAliveCellPaint.setColor(Color.GREEN);
        mAliveCellPaint.setStrokeWidth(2);
        mAliveCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDeadCellPaint = new Paint();
        mDeadCellPaint.setColor(Color.BLACK);
        mDeadCellPaint.setStrokeWidth(2);
        mDeadCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        setContentView(R.layout.activity_main);

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#CD5C5C"));
        Bitmap bg = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);
        canvas.drawRect(50, 50, 200, 200, paint);
        LinearLayout ll = (LinearLayout) findViewById(R.id.cellGridView);
        ll.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // Preventing extra work because method will be called many times.
                if(mScreenSizeY == (bottom - top) || mScreenSizeX == (right - left))
                    return;

                mScreenSizeY = (bottom - top);
                mScreenSizeX = (right - left);
                xGridSize = mScreenSizeX / xAdjust;
                yGridSize = mScreenSizeY / yAdjust;
            }
        });

        ImageButton randomizeButton = (ImageButton) findViewById(R.id.randomizeButton);
        randomizeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                initRandomGrid();
            }
        });

        ll.setBackgroundDrawable(new BitmapDrawable(bg));

        // mCellGridView = (CellGridView) findViewById(R.id.cellGridView);


        // holder = mCellGridView.getHolder();

        // holder.addCallback(callback);

        /*Surface surface = holder.getSurface();
        surface.unlockCanvasAndPost(null);
        Canvas canvas = surface.lockCanvas(null);
        mScreenSizeX = canvas.getWidth();
        mScreenSizeY = canvas.getHeight();
        surface.unlockCanvasAndPost(canvas);*/

        // initGrid();
        // RandomizeGrid();
    }

    private void initRandomGrid() {
        mCellGrid = new int[xGridSize][yGridSize];

        mHandler = new Handler();
        final int delay = 2000; //milliseconds

        mHandler.postDelayed(new Runnable(){
            public void run(){
                RandomizeGrid();
                DrawGrid();
                mHandler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void DrawGrid() {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        Bitmap bg = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);
        for(int i = 0; i < xGridSize; i++) {
            for(int j = 0; j < yGridSize; j++) {
                if(mCellGrid[i][j] == 1) {
                    canvas.drawCircle(i * xAdjust, j * yAdjust, 10, mAliveCellPaint);
                }
                else {
                    canvas.drawCircle(i * xAdjust, j * yAdjust, 10, mDeadCellPaint);
                }
            }
        }
        LinearLayout ll = (LinearLayout) findViewById(R.id.cellGridView);
        ll.setBackgroundDrawable(new BitmapDrawable(bg));
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

    public void step() {
        // Do one step in simulation
    }
}

