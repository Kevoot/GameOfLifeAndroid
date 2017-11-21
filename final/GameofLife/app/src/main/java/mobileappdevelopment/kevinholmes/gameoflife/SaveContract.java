package mobileappdevelopment.kevinholmes.gameoflife;

import android.provider.BaseColumns;

/**
 * Created by Alex on 11/9/2017.
 */

public class SaveContract {
    private SaveContract(){}

    public static final class SaveEntry implements BaseColumns{
        public final static String TABLE_NAME = "saves";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_SAVE_DATA = "save_data";
    }
}
