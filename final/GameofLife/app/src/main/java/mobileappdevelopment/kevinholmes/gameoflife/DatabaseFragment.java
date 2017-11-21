package mobileappdevelopment.kevinholmes.gameoflife;

import android.os.Bundle;

import android.support.v4.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ListView;
/**
 * Created by George Le on 11/14/2017.
 */

public class DatabaseFragment extends ListFragment {

    // button cancelling selecting from the database
    private Button mButtonCancel;
    public DatabaseHelper databaseHelper;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.db_fragment, container, false);
        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.db_fragment, container, false);
        }

        BitmapAdapter adapter = new BitmapAdapter(this.getContext(), );

        ListView listView = (ListView) view.findViewById(R.id.list);
        listView.setAdapter(adapter);

        return view;
    }


}