package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.Random;

import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.mCellGridView;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.mPasteGrid;

public class CellGridView extends View {
    //Added Kevin Set values for initial (non-randomized) alive cells
    private final Paint mAliveCellPaint;
    private final Paint mNewCellPaint;

    //Added Kevin Flag to indicate whether or not the grid has been initialized and is now at a drawable state
    public boolean initFlag;

    //Added Kevin Value for any void space (should use background setting instead of painting
    // as opposed to paint each individual cell with this)
    private final Paint mDeadCellPaint;

    //Added Kevin Only one of these should be loaded at any given time.
    // Attach any given one of them to the mHandler
    public final OnTouchListener mTouchSelectionHandler;
    public final OnTouchListener mTouchPaintHandler;
    public final OnTouchListener mTouchPasteHandler;

    //Added Kevin Raw View dimensions
    public int mViewSizeX, mViewSizeY;

    //Added Kevin Adjusted grid sizes, as making the grid equivalent to screen size is far too
    // expensive to iterate through
    public static int mGridSizeX, mGridSizeY;

    //Added Kevin Adjustment factors, must divide screen x and y size evenly
    public static int xAdjust, yAdjust;

    //Added Kevin Cell radius (half an adjustment)
    public static int mCellRadius;

    //Added Kevin Actual grid containing 0 or 1's indicating dead or alive cell
    public boolean [][] mCellGrid;

    public boolean [][] mPrevCellGrid;

    //Added Kevin Grid containing painting results
    public boolean [][] mPaintGrid;

    //Added Kevin Delay in milliseconds between each simulation step
    public int mDelay;

    public int x1=0;
    public int x2=0;
    public int y1=0;
    public int y2=0;

    //Added Kevin Handler for running simulation loop
    public final Handler mHandler = new Handler();

    //Added Kevin Runnable to attach to handler
    final Runnable mRunnable = new Runnable() {
        public void run() {
            mHandler.removeCallbacks(this);
            step();
            DrawGrid();
            mHandler.postDelayed(this, mDelay);
        }
    };

    private Bitmap mCurrentBg;
    private Bitmap mPreviewBitmap;

    public CellGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // Initially set to 1 second between each step
        mDelay = 1000;

        xAdjust = 10;
        yAdjust = 10;
        mCellRadius = xAdjust / 2;

        mAliveCellPaint = new Paint();
        mAliveCellPaint.setColor(Color.GREEN);
        mAliveCellPaint.setStrokeWidth(2);
        mAliveCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDeadCellPaint = new Paint();
        mDeadCellPaint.setColor(Color.BLACK);
        mDeadCellPaint.setStrokeWidth(2);
        mDeadCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mNewCellPaint = new Paint();
        mNewCellPaint.setColor(Color.BLUE);
        mNewCellPaint.setStrokeWidth(2);
        mNewCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);


        // Added by James 11/10 - Sets the x and y box for selected area
        mTouchSelectionHandler = new View.OnTouchListener() {
        // Modulus operations "clip" selection box to a grid location for easy translation later on
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pause();
                        x1 = (int) (event.getX());
                        while(x1 % xAdjust != 0) {
                            x1 -= 1;
                        }
                        y1 = (int) (event.getY());
                        while(y1 % yAdjust != 0) {
                            y1 -= 1;
                        }
                        if (x1 < 0) {x1 = 0;}
                        if (y1 < 0) {y1 = 0;}
                        x2 = x1;
                        y2 = y1;

                        break;
                    case MotionEvent.ACTION_MOVE:
                        x2 = (int) (event.getX());
                        y2 = (int) (event.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        while(x2 % xAdjust != 0) {
                            x2 -= 1;
                        }
                        while(y2 % yAdjust != 0) {
                            y2 -= 1;
                        }
                        if (x2 < 0) {x2 = 0;}
                        if (y2 < 0) {y2 = 0;}

                        if (!selected()){
                            resume();
                        }

                        v.performClick();
                        break;
                    default:
                        break;
                }
                Paint paint = new Paint();
                Bitmap tempBg = Bitmap.createBitmap(mCurrentBg);
                Canvas canvas = new Canvas(tempBg);
                if (selected()) {
                    paint.setColor(Color.rgb(100, 100, 100));
                    paint.setStrokeWidth(10);
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawRect(x1, y1, x2 - xAdjust, y2 - yAdjust, paint);
                    MainActivity.SetState(false, true, false);
                } else {
                    MainActivity.SetState(false, false, false);
                }
                BitmapDrawable bd = new BitmapDrawable(tempBg);
                setBackgroundDrawable(bd);
                return true;
            }
        };
        //Added Kevin - Allows finger painting on canvas
        mTouchPaintHandler = new View.OnTouchListener() {
            // Modulus operations "clip" selection box to a grid location for easy translation later on
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = 0;
                int y = 0;
                Bitmap tempBg = Bitmap.createBitmap(mCurrentBg);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(mPaintGrid == null) {
                            mPaintGrid = new boolean[mGridSizeX][mGridSizeY];
                        }
                        x = (int) (event.getX());
                        while(x % xAdjust != 0) {
                            x -= 1;
                        }
                        y = (int) (event.getY());
                        if(y > mViewSizeY) y = mViewSizeY;
                        while(y % yAdjust != 0) {
                            y -= 1;
                        }
                        if (x <= 0) x = 1;
                        if (y <= 0) y = 1;
                        if (x > mViewSizeX) x = mViewSizeX;
                        if (y > mViewSizeY) y = mViewSizeY;
                        mPaintGrid[(x / xAdjust) - 1][(y / yAdjust) - 1] = true;
                        pause();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        x = (int) (event.getX());
                        while(x % xAdjust != 0) {
                            x -= 1;
                        }
                        y = (int) (event.getY());
                        if(y > mViewSizeY) y = mViewSizeY;
                        while(y % yAdjust != 0) {
                            y -= 1;
                        }
                        if (x < 0) {x = 0;}
                        if (y < 0) {y = 0;}
                        if (x > mViewSizeX) { x = mViewSizeX; }
                        int adjustedX = x / xAdjust - 1;
                        int adjustedY = y / yAdjust - 1;
                        if(adjustedX < 0) adjustedX = 0;
                        if(adjustedX > mGridSizeX) adjustedX = mGridSizeX - 1;
                        if(adjustedY < 0) adjustedY = 0;
                        if(adjustedY > mGridSizeY) adjustedY = mGridSizeY - 1;
                        mPaintGrid[adjustedX][adjustedY] = true;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        break;
                    default:
                        break;
                }
                Paint paint = new Paint();
                Canvas canvas = new Canvas(tempBg);
                paint.setColor(Color.rgb(0, 255, 200));
                paint.setStrokeWidth(20);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(x, y, mCellRadius, mAliveCellPaint);
                // canvas.drawPoint(x, y, paint);
                BitmapDrawable bd = new BitmapDrawable(tempBg);
                setBackgroundDrawable(bd);
                mCurrentBg = bd.getBitmap();
                return true;
            }
        };
        //Added Kevin & James - Pasting to grid
        mTouchPasteHandler = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mPreviewBitmap == null) throw new Error("No Preview Bitmap found!");
                int x = 0;
                int y = 0;

                Bitmap tempBg = Bitmap.createBitmap(mPreviewBitmap);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pause();
                        x1 = (int) (event.getX());
                        while(x1 % xAdjust != 0) {
                            x1 -= 1;
                        }
                        y1 = (int) (event.getY());
                        while(y1 % yAdjust != 0) {
                            y1 -= 1;
                        }
                        if (x1 < 0) {x1 = 0;}
                        if (y1 < 0) {y1 = 0;}
                        x2 = x1;
                        y2 = y1;

                        break;
                    case MotionEvent.ACTION_MOVE:
                        x2 = (int) (event.getX());
                        y2 = (int) (event.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        while(x2 % xAdjust != 0) {
                            x2 -= 1;
                        }
                        while(y2 % yAdjust != 0) {
                            y2 -= 1;
                        }

                        if (x2 < 0) {x2 = 0;}
                        if (y2 < 0) {y2 = 0;}

                        v.performClick();
                        break;
                    default:
                        break;
                }
                tempBg = overlay(mCurrentBg, tempBg, x2-(tempBg.getWidth()/2), y2-(tempBg.getHeight()/2));
                BitmapDrawable bd = new BitmapDrawable(tempBg);
                setBackgroundDrawable(bd);
                return true;
            }
        };
    }

    public void setPreview(){
        Bitmap tempBg = Bitmap.createBitmap(mPreviewBitmap);
        double xNewAdjust = (double)mPasteGrid.mCreatedGridSizeX / (double)mGridSizeX;
        double yNewAdjust = (double)mPasteGrid.mCreatedGridSizeY / (double)mGridSizeY;
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(tempBg, (int)(tempBg.getWidth() * xNewAdjust), (int)(tempBg.getHeight() * yNewAdjust), false);
        x2 = (mViewSizeX/2)-(resizedBitmap.getWidth()/2);
        y2 = (mViewSizeY/2)-(resizedBitmap.getHeight()/2);
        mPreviewBitmap = Bitmap.createBitmap(resizedBitmap);
        Bitmap bg = overlay(mCurrentBg, resizedBitmap, x2, y2);
        BitmapDrawable bd = new BitmapDrawable(bg);

        setBackgroundDrawable(bd);
    }

    //Added Kevin - Transfer the pasting grid onto active grid
    public void transferCellsFromPaste(boolean[][] cells, int xOffset, int yOffset) {
        for(int i = 0; i < cells.length; i++) {
            for(int j = 0; j < cells[0].length; j++) {
                if(i + xOffset + 2 >= mCellGrid.length ||
                        xOffset + i < 0 ||
                        yOffset + j < 0 ||
                        j + yOffset + 2 >= mCellGrid[0].length) continue;
                mCellGrid[i + xOffset + 2][j + yOffset + 2] = cells[i][j];
            }
        }
    }

    //Added Kevin - initialize the grid with random cells
    public void initRandomGrid() {
        mViewSizeY = this.getHeight();
        mViewSizeX = this.getWidth();

        // If either of the dimension don't get populated, can't begin grid init.
        if(mViewSizeX == 0 || mViewSizeY == 0) {
            return;
        }

        // Actual adjusted grid dimensions
        mGridSizeX = mViewSizeX / xAdjust;
        mGridSizeY = mViewSizeY / yAdjust;

        setOnTouchListener(mTouchSelectionHandler);

        // Create a grid of cells and a grid of colors for those cells
        mCellGrid = new boolean[mGridSizeX][mGridSizeY];

        // Randomize at alive cells and colors
        RandomizeGrid();
        // RandomizeColors();

        mPrevCellGrid = mCellGrid;

        // Begin simulation
        mHandler.postDelayed(mRunnable, 1000);
        initFlag = true;
        MainActivity.initialized = true;
    }

    //Added Kevin - Initialize grid for finger-painting
    public void initBlankGrid() {
        mViewSizeY = this.getHeight();
        mViewSizeX = this.getWidth();

        // If either of the dimension don't get populated, can't begin grid init.
        if(mViewSizeX == 0 || mViewSizeY == 0) {
            return;
        }

        // Actual adjusted grid dimensions
        mGridSizeX = mViewSizeX / xAdjust;
        mGridSizeY = mViewSizeY / yAdjust;

        setOnTouchListener(mTouchSelectionHandler);

        // Create a grid of cells and a grid of colors for those cells
        mCellGrid = new boolean[mGridSizeX][mGridSizeY];

        // Randomize at alive cells and colors
        // RandomizeColors();
        mPrevCellGrid = mCellGrid;

        // Begin simulation
        mHandler.postDelayed(mRunnable, 1000);
        initFlag = true;
        MainActivity.initialized = true;
    }

    //Added Kevin - Force draws on background of container
    public void DrawGrid() {
        Paint paint = new Paint();
        mCurrentBg = Bitmap.createBitmap(mViewSizeX, mViewSizeY, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mCurrentBg);
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);
        // Draw background
        canvas.drawRect(0, 0, mViewSizeX, mViewSizeY, mDeadCellPaint);

        // Use colors from grid, coloring each segment that has a "1" value in the CellGrid
        for(int i = 0; i < mGridSizeX; i++) {
            for(int j = 0; j < mGridSizeY; j++) {
                if(mCellGrid[i][j]) {
                    // paint.setColor(mColorGrid[i][j]);
                    if(mPrevCellGrid[i][j])
                        canvas.drawCircle(i * xAdjust, j * yAdjust, mCellRadius, mAliveCellPaint);
                    else
                        canvas.drawCircle(i * xAdjust, j * yAdjust, mCellRadius, mNewCellPaint);
                }
            }
        }

        setBackground(null);
        setBackgroundDrawable(new BitmapDrawable(mCurrentBg));
    }

    public void RandomizeGrid() {
        Random rand = new Random();
        for(int i = 0; i < mGridSizeX; i++) {
            for(int j = 0; j < mGridSizeY; j++) {
                int n = rand.nextInt(10)+ 1;
                if(n == 1) {
                    mCellGrid[i][j] = true;
                }
                else {
                    mCellGrid[i][j] = false;
                }
            }
        }
    }

    //Added Kevin - one step in the simulation
    public void step() {
        boolean[][] future = new boolean[mGridSizeX][mGridSizeY];

        // Loop through every cell
        for (int l = 1; l < mGridSizeX - 1; l++)
        {
            for (int m = 1; m < mGridSizeY - 1; m++)
            {
                // finding no Of Neighbours that are alive
                int aliveNeighbours = 0;
                for (int i = -1; i <= 1; i++)
                    for (int j = -1; j <= 1; j++)
                        aliveNeighbours += mCellGrid[l + i][m + j] ? 1 : 0;

                // The cell needs to be subtracted from
                // its neighbours as it was counted before
                aliveNeighbours -= mCellGrid[l][m] ? 1 : 0;

                // Implementing the Rules of Life

                // Cell is lonely and dies
                if ((mCellGrid[l][m] == true) && (aliveNeighbours < 2))
                    future[l][m] = false;

                    // Cell dies due to over population
                else if ((mCellGrid[l][m] == true) && (aliveNeighbours > 3))
                    future[l][m] = false;

                    // A new cell is born
                else if ((mCellGrid[l][m] == false) && (aliveNeighbours == 3))
                    future[l][m] = true;

                    // Remains the same
                else
                    future[l][m] = mCellGrid[l][m];
            }
        }

        mPrevCellGrid = mCellGrid;
        mCellGrid = future;
    }

    //Added Kevin - Runnable handling
    public void pause() {
        mHandler.removeCallbacks(mRunnable);
    }
    public void resume() {
        mHandler.postDelayed(mRunnable, mDelay);
    }

    // James - needed so android studio wont yell at me for doing the touch listener
    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    // James - allows the selected box to appear only if it is large enough
    public boolean selected(){
        return (Math.abs(x1-x2) > 20 || Math.abs(y1-y2) > 20);
    }

    // James - un-select the box
    public void deselect(){
        x1 = 0;
        x2 = 0;
        y1 = 0;
        y2 = 0;
    }

    //James - Delete what is in the selected box
    public void deleteSelected(){

        //Create temp variables to adjust
        int x1c = x1/xAdjust;
        int x2c = x2/xAdjust;
        int y1c = y1/yAdjust;
        int y2c = y2/yAdjust;

        //Swap if the box is dragged backwards
        if (x1c > x2c){
            int temp = x1c;
            x1c = x2c;
            x2c = temp;
        }

        if (y1c > y2c){
            int temp = y1c;
            y1c = y2c;
            y2c = temp;
        }

        //Make sure the bounds are in the box
        if (x1c < 0){x1c = 0;}
        if (y1c < 0){y1c = 0;}
        if (x2c > mGridSizeX-1){x2c = mGridSizeX-1;}
        if (y2c > mGridSizeY-1){y2c = mGridSizeY-1;}

        // Make the cells in the selected box dead
        for(int i = x1c; i < x2c; i++) {
            for(int j = y1c; j < y2c; j++) {
                mCellGrid[i][j] = false;
            }
        }
    }
    //Added James - Good to be used for copy and cut functions
    public boolean[][] copySelected() {

        int x1c = x1/xAdjust;
        int x2c = x2/xAdjust;
        int y1c = y1/yAdjust;
        int y2c = y2/yAdjust;

        //Swap if the box is dragged backwards
        if (x1c > x2c){
            int temp = x1c;
            x1c = x2c;
            x2c = temp;
        }

        if (y1c > y2c){
            int temp = y1c;
            y1c = y2c;
            y2c = temp;
        }

        //Make sure the bounds are in the box
        if (x1c < 0){x1c = 0;}
        if (y1c < 0){y1c = 0;}
        if (x2c > mGridSizeX-1){x2c = mGridSizeX-1;}
        if (y2c > mGridSizeY-1){y2c = mGridSizeY-1;}

        boolean[][] selectedArray = new boolean[x2c-x1c][y2c-y1c];
        int [][] selectedArrayColors = new int[x2c-x1c][y2c-y1c];

        // Copy the selected cells
        for(int i = 0; i < x2c-x1c - 1; i++) {
            for(int j = 0; j < y2c-y1c - 1; j++) {
                selectedArray[i][j] = mCellGrid[x1c+i][y1c+j];
            }
        }
        return selectedArray;
    }

    public void setPreviewBitmap(Bitmap mPreviewBitmap) {
        this.mPreviewBitmap = mPreviewBitmap;
    }

    public Bitmap getPreviewBitmap() {
        return this.mPreviewBitmap;
    }

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2, int x, int y) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);

        // double xNewAdjust = (double)mPasteGrid.mCreatedGridSizeX / (double)mGridSizeX;
        // double yNewAdjust = (double)mPasteGrid.mCreatedGridSizeY / (double)mGridSizeY;

        // Bitmap resizedBitmap = Bitmap.createScaledBitmap(bmp2, (int)(bmp2.getWidth() * xNewAdjust), (int)(bmp2.getHeight() * yNewAdjust), false);

        BitmapDrawable bd = new BitmapDrawable(bmp2);
        bd.setAlpha(50);
        canvas.drawBitmap(bd.getBitmap(), x, y, null);
        return bmOverlay;
    }
}
