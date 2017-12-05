package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.v4.util.Pair;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.ArrayList;

public class DatabaseManagementAdapter extends ArrayAdapter<SerializableCellGrid> {
    public DatabaseManagementAdapter(Context context, ArrayList<SerializableCellGrid> grids){
        super(context, 0, grids);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container){
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.db_management, container, false);
        }

        final RowItemDisplay rowItem = new RowItemDisplay(listItemView.getContext());

        rowItem.view = listItemView.findViewById(R.id.database_image);
        rowItem.cb = listItemView.findViewById(R.id.checkbox);
        rowItem.cb.setChecked(false);
        rowItem.cb.setTag(position);

        SerializableCellGrid grid = getItem(position);

        rowItem.id = grid.id;
        rowItem.view.setImageDrawable(new BitmapDrawable(grid.mPreviewBitmap.currentImage));
        return listItemView;
    }
}

