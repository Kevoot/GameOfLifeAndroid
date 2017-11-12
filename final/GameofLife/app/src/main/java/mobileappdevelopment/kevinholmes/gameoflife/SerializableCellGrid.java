package mobileappdevelopment.kevinholmes.gameoflife;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    public SerializableCellGrid() {
        mCreationDateTime = DateFormat.getDateTimeInstance().format(new Date());
        mWidth = 0;
        mHeight = 0;
        mNumAliveCells = 0;
        mCellGrid = new boolean[0][0];
    }

    public SerializableCellGrid(@NonNull boolean[][] cellGrid) {
        if(ValidateCellGrid(cellGrid)) {
            mCellGrid = cellGrid;
            mCreationDateTime = DateFormat.getDateTimeInstance().format(new Date());
            mWidth = cellGrid.length;
            mHeight = cellGrid[0].length;
            mNumAliveCells = 0;

            for (boolean[] aCellGrid : cellGrid) {
                for (boolean anACellGrid : aCellGrid) {
                    if(anACellGrid) mNumAliveCells++;
                }
            }
        } else throw new Error("Invalid Cell Grid passed to Serializable Cell Constructor");
    }

    private boolean ValidateCellGrid(boolean[][] cellGrid) {
        return !(cellGrid.length == 0 || cellGrid[0].length == 0);
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
}
