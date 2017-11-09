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
import java.util.concurrent.RunnableFuture;

public class MainActivity extends AppCompatActivity {
    private Paint mAliveCellPaint;
    private Paint mDeadCellPaint;
    public static int mScreenSizeX;
    public static int mScreenSizeY;
    public static int xGridSize;
    private static int yGridSize;
    private int xAdjust;
    private int yAdjust;
    public final Handler mHandler = new Handler();
    private static int [][] mCellGrid;
    private static int[][] mColorGrid;
    final int delay = 1000; //milliseconds

    final Runnable mRunnable = new Runnable() {
        public void run() {
            mHandler.removeCallbacks(this);
            step();
            DrawGrid();
            mHandler.postDelayed(this, delay);
        }
    };

    public static boolean cutSelected = false;
    public static boolean copySelected = false;
    public static boolean saveSelected = false;
    public static boolean dbSelected = false;


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

        final ImageButton cutButton = (ImageButton) findViewById(R.id.cutButton);
        cutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pause simulation
                if(!cutSelected) {
                    cutSelected = true;
                    mHandler.removeCallbacks(mRunnable);
                    // TODO: Begin cut fragment

                } else {
                    cutSelected = false;
                    mHandler.postDelayed(mRunnable, delay);
                }
            }
        });

        final ImageButton copyButton = (ImageButton) findViewById(R.id.copyButton);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pause simulation
                if(!copySelected) {
                    copySelected = true;
                    mHandler.removeCallbacks(mRunnable);
                    // TODO: Begin copy fragment

                } else {
                    copySelected = false;
                    mHandler.postDelayed(mRunnable, delay);
                }
            }
        });

        final ImageButton saveButton = (ImageButton) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pause simulation
                if(!saveSelected) {
                    saveSelected = true;
                    mHandler.removeCallbacks(mRunnable);
                    // TODO: Begin save fragment

                } else {
                    saveSelected = false;
                    mHandler.postDelayed(mRunnable, delay);
                }
            }
        });

        final ImageButton dbButton = (ImageButton) findViewById(R.id.dbButton);
        dbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pause simulation
                if(!dbSelected) {
                    dbSelected = true;
                    mHandler.removeCallbacks(mRunnable);
                    // TODO: Begin db fragment

                } else {
                    dbSelected = false;
                    mHandler.postDelayed(mRunnable, delay);
                }
            }
        });

        final ImageButton randomizeButton = (ImageButton) findViewById(R.id.randomizeButton);
        randomizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.removeCallbacks(mRunnable);
                initRandomGrid();
            }
        });
    }

    private void initRandomGrid() {
        // Create a grid of cells and a grid of colors for those cells
        mCellGrid = new int[xGridSize][yGridSize];
        mColorGrid = new int[xGridSize][yGridSize];

        RandomizeGrid();
        RandomizeColors();

        mHandler.postDelayed(mRunnable, 1000);
    }

    private void DrawGrid() {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        Bitmap bg = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);

        // Draw background
        canvas.drawRect(0, 0, mScreenSizeX, mScreenSizeY, mDeadCellPaint);

        // Use colors from grid, coloring each segment that has a "1" value in the CellGrid
        for(int i = 0; i < xGridSize; i++) {
            for(int j = 0; j < yGridSize; j++) {
                if(mCellGrid[i][j] == 1) {
                    paint.setColor(mColorGrid[i][j]);
                    canvas.drawCircle(i * xAdjust, j * yAdjust, 5, paint);
                }
            }
        }
        LinearLayout ll = (LinearLayout) findViewById(R.id.cellGridView);
        ll.setBackground(null);
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

    public void RandomizeColors() {
        Random rand = new Random();

        for(int i = 0; i < mCellGrid.length; i++) {
            for (int j = 0; j < mCellGrid.length; j++) {
                int r = rand.nextInt(255);
                int g = rand.nextInt(255);
                int b = rand.nextInt(255);
                mColorGrid[i][j] = Color.argb(255, r, g, b);
            }
        }
    }

    public void step() {
        // Do one step in simulation
        int[][] future = new int[xGridSize][yGridSize];

        // Loop through every cell
        for (int l = 1; l < xGridSize - 1; l++)
        {
            for (int m = 1; m < yGridSize - 1; m++)
            {
                // finding no Of Neighbours that are alive
                int aliveNeighbours = 0;
                for (int i = -1; i <= 1; i++)
                    for (int j = -1; j <= 1; j++)
                        aliveNeighbours += mCellGrid[l + i][m + j];

                // The cell needs to be subtracted from
                // its neighbours as it was counted before
                aliveNeighbours -= mCellGrid[l][m];

                // Implementing the Rules of Life

                // Cell is lonely and dies
                if ((mCellGrid[l][m] == 1) && (aliveNeighbours < 2))
                    future[l][m] = 0;

                    // Cell dies due to over population
                else if ((mCellGrid[l][m] == 1) && (aliveNeighbours > 3))
                    future[l][m] = 0;

                    // A new cell is born
                else if ((mCellGrid[l][m] == 0) && (aliveNeighbours == 3))
                    future[l][m] = 1;

                    // Remains the same
                else
                    future[l][m] = mCellGrid[l][m];
            }
        }

        mCellGrid = future;
    }
}

