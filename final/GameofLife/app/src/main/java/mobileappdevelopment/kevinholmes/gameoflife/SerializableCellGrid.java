package mobileappdevelopment.kevinholmes.gameoflife;

import android.graphics.Bitmap;
import android.media.Image;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.graphics.BitmapCompat;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Kevin on 11/12/2017.
 */

public class SerializableCellGrid implements Serializable {
    private String mCreationDateTime;
    // x dimension of grid
    private int mWidth;
    // y dimension of grid
    private int mHeight;
    // number of alive cells
    private int mNumAliveCells;
    // internal grid
    private boolean[][] mCellGrid;
    // color grid
    private int[][] mColorGrid;
    // Preview bitmap for fragments
    Bitmap mPreviewBitmap;

    public SerializableCellGrid() {
        mCreationDateTime = DateFormat.getDateTimeInstance().format(new Date());
        mWidth = 0;
        mHeight = 0;
        mNumAliveCells = 0;
        mCellGrid = new boolean[0][0];
    }

    public SerializableCellGrid(@NonNull boolean[][] cellGrid, @NonNull int[][] colorGrid) {
        if(ValidateCellGrids(cellGrid, colorGrid)) {
            mCellGrid = cellGrid;
            mColorGrid = colorGrid;
            mCreationDateTime = DateFormat.getDateTimeInstance().format(new Date());
            mWidth = cellGrid.length;
            mHeight = cellGrid[0].length;
            mNumAliveCells = 0;

            for (boolean[] aCellGrid : cellGrid) {
                for (boolean anACellGrid : aCellGrid) {
                    if(anACellGrid) mNumAliveCells++;
                }
            }
            Bitmap bmp = Bitmap.createBitmap(colorGrid.length, colorGrid[0].length, Bitmap.Config.ARGB_8888);
            for(int i = 0; i < colorGrid.length; i++) {
                for(int j = 0; j < colorGrid[0].length; j++) {
                    bmp.setPixel(i, j, colorGrid[i][j]);
                }
            }
            mPreviewBitmap = bmp;
        } else throw new Error("Invalid Cell Grid passed to Serializable Cell Constructor");
    }

    private boolean ValidateCellGrids(@NonNull boolean[][] cellGrid, @NonNull int[][] colorGrid) {
        return !((cellGrid.length == 0 || cellGrid[0].length == 0) &&
                (colorGrid.length == 0 || colorGrid[0].length == 0));
    }

    public String getCreationDateTime() {
        return mCreationDateTime;
    }

    public void setCreationDate(String mCreationDateTime) {
        this.mCreationDateTime = mCreationDateTime;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getNumAliveCells() {
        return mNumAliveCells;
    }

    public void setNumAliveCells(int mNumAliveCells) {
        this.mNumAliveCells = mNumAliveCells;
    }

    public boolean[][] getCellGrid() {
        return mCellGrid;
    }

    public void setCellGrid(boolean[][] mCellGrid) {
        this.mCellGrid = mCellGrid;
    }

    public int[][] getColorGrid() { return mColorGrid; }

    public void setColorGrid(int[][] grid) { this.mColorGrid = grid; }
}
