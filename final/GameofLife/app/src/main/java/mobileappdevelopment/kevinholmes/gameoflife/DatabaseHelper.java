package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import mobileappdevelopment.kevinholmes.gameoflife.SaveContract.SaveEntry;

import static android.os.Debug.waitForDebugger;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.selectedGrid;

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "gameoflife.db";

    private static final int DATABASE_VERSION = 1;

    private static ArrayList<SerializableCellGrid> savePreviews;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        waitForDebugger();
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
        if(savePreviews == null) savePreviews = new ArrayList<>();
        // Using this class ensures all values are in valid range
        SerializableCellGrid saveGrid = new SerializableCellGrid(grid);
        byte[] bytes = serializeCellGrid(saveGrid);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SaveEntry.COLUMN_SAVE_DATA, bytes);

        long rowId = -1;

        try{
            rowId = db.insertOrThrow(SaveEntry.TABLE_NAME, null, values);
        }
        catch(Exception e) {
            Log.d(" ", e.toString());
        }
        saveGrid.id = rowId;
        savePreviews.add(saveGrid);

        if(rowId == -1) {
            return false;
        }
        else {
            // TODO: Remove this, present just for testing, still need paste fragment.
            selectedGrid = rowId;
            return true;
        }
    }

    //Allows
    public SerializableCellGrid requestGrid(Long id){
        // Execute SQL to retrieve thing with proper name
        // the new byte array will be replaced by actual data once this is working

        SerializableCellGrid serializableCellGrid;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String requestString = "SELECT * FROM " + SaveEntry.TABLE_NAME + " WHERE _id =" + id;
            Cursor result = db.rawQuery(requestString, null);
            result.moveToFirst();

            byte[] resultArray = result.getBlob(1);

            serializableCellGrid = deserializeCellGrid(resultArray);

            serializableCellGrid.id = id;

            assert serializableCellGrid != null;

            return serializableCellGrid;
        }
        catch(Exception e ) {
            Log.d(" ", e.toString());
        }

        return null;
    }

    //Returns the list of all the names of the saves.
    public ArrayList<SerializableCellGrid> getPreviewImages(){
        return savePreviews;
    }

    //Clears a specific save from the database
    public boolean clearSave(Long id){
        SQLiteDatabase db = this.getWritableDatabase();

        String deleteString = "DELETE FROM " + SaveEntry.TABLE_NAME + " WHERE _id = " + id + ";";

        try{
            db.execSQL(deleteString);
        }catch (Exception e){
            return false;
        }
        
        SerializableCellGrid pendingDeletion = null;
        for(SerializableCellGrid s : savePreviews) {
            if(s.id == id) {
                pendingDeletion = s;
            }
        }
        if(pendingDeletion != null) savePreviews.remove(pendingDeletion);

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

        savePreviews.clear();

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
    public static SerializableCellGrid deserializeCellGrid(byte[] b) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
            Object object = in.readObject();
            in.close();
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
