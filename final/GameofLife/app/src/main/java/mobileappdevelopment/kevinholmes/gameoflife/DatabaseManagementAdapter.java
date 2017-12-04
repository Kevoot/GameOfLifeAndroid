package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by George Le on 11/29/2017.
 */

public class DatabaseManagementAdapter extends ArrayAdapter<SerializableCellGrid> {

    public boolean checked = false;

    public DatabaseManagementAdapter(Context context, ArrayList<SerializableCellGrid> grids){
        super(context, 0, grids);
    }

    private static class RowItemDisplay{
        public long id;
        CheckBox box;
        public ImageView view;
        public RowItemDisplay() {
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container){
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.db_management, container, false);
        }

        final RowItemDisplay imageView = new RowItemDisplay();

        imageView.view = listItemView.findViewById(R.id.database_image);
        imageView.box = listItemView.findViewById(R.id.checkbox);

        SerializableCellGrid grid = getItem(position);

        imageView.id = grid.id;
        imageView.view.setImageDrawable(new BitmapDrawable(grid.mPreviewBitmap.currentImage));
        this.checked = false;
        return listItemView;
    }
}