package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.selectedGrid;

public class PasteBitmapAdapter extends ArrayAdapter<Pair<Long, BitmapDataObject>> {

    public PasteBitmapAdapter(Context context, ArrayList<Pair<Long, BitmapDataObject>> grids){
        super(context, 0, grids);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container){
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.paste_list_item, container, false);
        }

        final GridItemDisplay imageView = (GridItemDisplay) listItemView.findViewById(R.id.paste_image);

        Pair<Long, BitmapDataObject> grid = getItem(position);

        imageView.id = grid.first;
        imageView.setImageDrawable(new BitmapDrawable(grid.second.currentImage));
        // imageView.set(new BitmapDrawable(grid.second.currentImage));
        // In clickListener for each view created, Call Alex's get requestGrid()imageView.id
        // That will give you a serializable cell grid object, set pastegrid = to it

        /*listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedGrid = imageView.id;
            }
        });*/

        return listItemView;
    }
}