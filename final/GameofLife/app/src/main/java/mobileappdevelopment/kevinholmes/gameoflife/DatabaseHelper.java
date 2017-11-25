package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.Pair;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;


import mobileappdevelopment.kevinholmes.gameoflife.SaveContract.SaveEntry;

/**
 * Created by Alex on 11/9/2017.
 */

// TODO: (Alex): During SerializableCellGrid creation, use some kind of inner property here
    // to assign each saved segment an id.
public class DatabaseHelper extends SQLiteOpenHelper{

    // WILL BE DELETED. Use this to test paste functionality
    public static byte[] tempPasteData;

    private static final String DATABASE_NAME = "gameoflife.db";

    private static final int DATABASE_VERSION = 1;

    private static ArrayList<Pair<Long, BitmapDataObject>> savePreviews;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String SQL_CREATE_SAVE_TABLE = "CREATE TABLE " + SaveEntry.TABLE_NAME + " ("
                + SaveEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SaveEntry.COLUMN_SAVE_DATA + " BLOB NOT NULL); ";

        db.execSQL(SQL_CREATE_SAVE_TABLE);

        savePreviews = new ArrayList<>();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //do stuff
    }

    //Save function for saving an amount of the grid
    public boolean saveGrid(boolean[][] grid) {
        // Using this class ensures all values are in valid range
        SerializableCellGrid saveGrid = new SerializableCellGrid(grid);
        byte[] bytes = serializeCellGrid(saveGrid);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SaveEntry.COLUMN_SAVE_DATA, bytes);

        long rowId = db.insert(SaveEntry.TABLE_NAME, null, values);
        Pair<Long, BitmapDataObject> preview = new Pair<>(rowId, saveGrid.mPreviewBitmap);
        savePreviews.add(preview);

        if(rowId == -1) {
            return false;
        }
        else return true;
    }

    //Allows
    public SerializableCellGrid requestGrid(Long id){
        // Execute SQL to retrieve thing with proper name
        // the new byte array will be replaced by actual data once this is working

        SQLiteDatabase db = this.getReadableDatabase();
        String requestString = "SELECT * FROM " + SaveEntry.TABLE_NAME + " WHERE id =" + id;
        Cursor result = db.rawQuery(requestString, null);
        result.moveToFirst();

        // This will be the actual one to use once DB is up and running
        byte[] resultArray = result.getBlob(1);

        SerializableCellGrid serializableCellGrid = deserializeCellGrid(resultArray);

        assert serializableCellGrid != null;

        return serializableCellGrid;
    }

    //Returns the list of all the names of the saves.
    public ArrayList<Pair<Long, BitmapDataObject>> getPreviewImages(){
        return savePreviews;
    }

    //Clears a specific save from the database
    public boolean clearSave(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        String deleteString = "DELETE FROM " + SaveEntry.TABLE_NAME + " WHERE id = " + id + ";";

        try{
            db.execSQL(deleteString);
        }catch (Exception e){
            return false;
        }

        return true;
    }

    //Clears all saves from the database
    public boolean clearAllSaves(){
        SQLiteDatabase db = this.getWritableDatabase();

        String sqlString = "DELETE FROM " + SaveEntry.TABLE_NAME + ";";

        try {
            db.execSQL(sqlString);
        }catch (Exception e){
            return false;
        }

        return true;
    }

    // Allows us the ability to convert the entire grid and stats to a bytestream for saving to sql
    public static byte[] serializeCellGrid(SerializableCellGrid o) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(o);
            out.close();

            // Get the bytes of the serialized object
            return bos.toByteArray();
        } catch (IOException ioe) {
            Log.e("serializeObject", "error", ioe);

            return null;
        }
    }

    // Gets bytestream from sql and deserializes (hopefully) into a SerializableCellGrid object
    // TODO: Test this!
    public static SerializableCellGrid deserializeCellGrid(byte[] b) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
            Object object = in.readObject();
            in.close();

            // TODO: Validate this gives us back a valid grid, otherwise will have to iteratively
            // TODO: populate a new boolean[][] object manually
            return (SerializableCellGrid)object;
        } catch (ClassNotFoundException cnfe) {
            Log.e("deserializeObject", "class not found error", cnfe);

            return null;
        } catch (IOException ioe) {
            Log.e("deserializeObject", "io error", ioe);

            return null;
        }
    }

}
