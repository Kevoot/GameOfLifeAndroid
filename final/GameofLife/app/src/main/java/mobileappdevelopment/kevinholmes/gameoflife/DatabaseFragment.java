package mobileappdevelopment.kevinholmes.gameoflife;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

//import mobileappdevelopment.kevinholmes.gameoflife.DBFragmentListViewLoader;

/**
 * Created by George Le on 11/14/2017.
 */

public class DatabaseFragment extends Fragment {

    // A listview object that accesses the elements of the database and displays the element's
    // information for the user to view. It also allows for the users to select an element and
    // load it in the MainActivity
    //private DBFragmentListViewLoader dbloader;

    // two buttons on selecting from the database
    private Button mButtonCancel;
    private Button mButtonLoad;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.db_fragment, container, false);
    }
}