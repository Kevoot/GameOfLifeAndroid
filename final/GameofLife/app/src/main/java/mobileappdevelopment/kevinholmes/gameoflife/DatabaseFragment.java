package mobileappdevelopment.kevinholmes.gameoflife;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by George Le on 11/14/2017.
 */

public class DatabaseFragment extends Fragment {

    // button cancelling selecting from the database
    private Button mButtonCancel;
    public DatabaseHelper databaseHelper;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.db_fragment, container, false);

        final ArrayList<String> menuItems = databaseHelper.getSaveNames();

        ListView listView = (ListView) view.findViewById(R.id.list);

        final ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menuItems);

        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // DO SOMETHING
                pasteGrid = databaseHelper.requestGrids(menuItems.get(i));
            }
        });
        return view;
    }
}