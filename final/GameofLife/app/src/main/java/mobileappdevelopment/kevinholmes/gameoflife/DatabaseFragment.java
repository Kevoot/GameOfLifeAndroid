package mobileappdevelopment.kevinholmes.gameoflife;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

/**
 * Created by George Le on 11/14/2017.
 */

public class DatabaseFragment extends ListFragment implements AdapterView.OnItemClickListener {

    // button cancelling selecting from the database
    private Button mButtonCancel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.db_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<byte[]> adapter = ArrayAdapter.createFromResource(getActivity(), );
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){

    }
}