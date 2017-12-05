package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

public class PasteBitmapAdapter extends ArrayAdapter<SerializableCellGrid> {

    public PasteBitmapAdapter(Context context, ArrayList<SerializableCellGrid> grids){
        super(context, 0, grids);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container){
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.paste_list_item, container, false);
        }

        final GridItemDisplay imageView = (GridItemDisplay) listItemView.findViewById(R.id.paste_image);

        SerializableCellGrid grid = getItem(position);

        imageView.id = grid.id;
        imageView.setImageDrawable(new BitmapDrawable(grid.mPreviewBitmap.currentImage));

        return listItemView;
    }
}