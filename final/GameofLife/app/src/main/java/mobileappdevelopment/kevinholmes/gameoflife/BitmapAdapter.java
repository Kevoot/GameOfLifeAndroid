package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.pasteGrid;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.mDatabaseHelper;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.selectedGrid;

/**
 * Created by George Le on 11/21/2017.
 */

public class BitmapAdapter extends ArrayAdapter<Pair<Pair<Long, BitmapDataObject>, Pair<Integer, Integer>>> {

    public BitmapAdapter(Context context, ArrayList<Pair<Pair<Long, BitmapDataObject>, Pair<Integer, Integer>>> data){
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container){
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.db_fragment, container, false);
        }

        final GridItemDisplay imageView = (GridItemDisplay) listItemView.findViewById(R.id.image);

        Pair<Pair<Long, BitmapDataObject>, Pair<Integer, Integer>> grid = getItem(position);

        imageView.id = grid.first.first;
        imageView.setImageDrawable(new BitmapDrawable(grid.first.second.currentImage));
        // imageView.set(new BitmapDrawable(grid.second.currentImage));
        // In clickListener for each view created, Call Alex's get requestGrid()imageView.id
        // That will give you a serializable cell grid object, set pastegrid = to it

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedGrid = imageView.id;
            }
        });

        return listItemView;
    }
}