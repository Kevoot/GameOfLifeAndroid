package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import java.util.Random;

/**
 * Created by Kevin on 11/10/2017.
 */

public class CellGridView extends View {
    // Set values for initial (non-randomized) alive cells
    private final Paint mAliveCellPaint;

    // Value for any void space (should use background setting instead of painting
    // as opposed to paint each individual cell with this)
    private final Paint mDeadCellPaint;

    // Raw View dimensions
    public int mScreenSizeX, mScreenSizeY;

    // Adjusted grid sizes, as making the grid equivalent to screen size is far too
    // expensive to iterate through
    public int mGridSizeX, mGridSizeY;

    // Adjustment factors, must divide screen x and y size evenly
    public int xAdjust, yAdjust;

    // Actual grid containing 0 or 1's indicating dead or alive cell
    private int [][] mCellGrid;

    // Color grid, must match exact dimensions of mCellGrid
    private int[][] mColorGrid;

    // Delay in milliseconds between each simulation step
    public int delay;

    public boolean highlight;
    public int x1=0;
    public int x2=0;
    public int y1=0;
    public int y2=0;

    // Handler for running simulation loop
    public final Handler mHandler = new Handler();

    // Runnable to attach to handler
    final Runnable mRunnable = new Runnable() {
        public void run() {
            mHandler.removeCallbacks(this);
            step();
            DrawGrid();
            mHandler.postDelayed(this, delay);
        }
    };

    public CellGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // Initially set to 1 second between each step
        delay = 1000;

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
    }

    public void initRandomGrid() {
        mScreenSizeY = this.getHeight();
        mScreenSizeX = this.getWidth();

        // If either of the dimension don't get populated, can't begin grid init.
        if(mScreenSizeX == 0 || mScreenSizeY == 0) {
            return;
        }

        // Actual adjusted grid dimensions
        mGridSizeX = mScreenSizeX / xAdjust;
        mGridSizeY = mScreenSizeY / yAdjust;

        // Create a grid of cells and a grid of colors for those cells
        mCellGrid = new int[mGridSizeX][mGridSizeY];
        mColorGrid = new int[mGridSizeX][mGridSizeY];

        // Randomize at alive cells and colors
        RandomizeGrid();
        RandomizeColors();

        // Begin simulation
        mHandler.postDelayed(mRunnable, 1000);
    }

    private void DrawGrid() {
        Paint paint = new Paint();
        Bitmap bg = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);

        // Draw background
        canvas.drawRect(0, 0, mScreenSizeX, mScreenSizeY, mDeadCellPaint);

        // Use colors from grid, coloring each segment that has a "1" value in the CellGrid
        for(int i = 0; i < mGridSizeX; i++) {
            for(int j = 0; j < mGridSizeY; j++) {
                if(mCellGrid[i][j] == 1) {
                    paint.setColor(mColorGrid[i][j]);
                    canvas.drawCircle(i * xAdjust, j * yAdjust, 5, paint);
                }
            }
        }

        setBackground(null);
        setBackgroundDrawable(new BitmapDrawable(bg));

        // Added by James 11/10 - This will draw the selected box
        //Todo: Change the colors and maybe make it if it is only selected
        if (true) {
            paint.setColor(Color.rgb(100, 100, 100));
            paint.setStrokeWidth(10);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(x1, y1, x2, y2, paint);
        }
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
        int[][] future = new int[mGridSizeX][mGridSizeY];

        // Loop through every cell
        for (int l = 1; l < mGridSizeX - 1; l++)
        {
            for (int m = 1; m < mGridSizeY - 1; m++)
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

    public void pause() {
        mHandler.removeCallbacks(mRunnable);
    }

    public void resume() {
        mHandler.postDelayed(mRunnable, delay);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
}
