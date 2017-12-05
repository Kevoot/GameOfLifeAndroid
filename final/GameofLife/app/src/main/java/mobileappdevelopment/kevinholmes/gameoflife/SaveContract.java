package mobileappdevelopment.kevinholmes.gameoflife;

import android.provider.BaseColumns;

class SaveContract {
    private SaveContract(){}

    static final class SaveEntry implements BaseColumns{
        final static String TABLE_NAME = "saves";

        final static String _ID = BaseColumns._ID;

        final static String COLUMN_SAVE_DATA = "save_data";
    }
}
