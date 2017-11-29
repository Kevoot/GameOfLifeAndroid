package mobileappdevelopment.kevinholmes.gameoflife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.mDatabaseHelper;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.selectedGrid;

/**
 * Created by George Le on 11/29/2017.
 */

public class DatabaseManagementFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater factory = LayoutInflater.from(getActivity());

        final View view = factory.inflate(R.layout.database_management_fragment, null);

        DatabaseManagementAdapter adapter = new DatabaseManagementAdapter(this.getContext(), mDatabaseHelper.getPreviewImages());

        final ListView listView = (ListView) view.findViewById(R.id.database_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object o =listView.getItemAtPosition(i);
                selectedGrid = ((Pair<Long, int[][]>)o).first;
                dismiss();
            }
        });


        final Button button = view.findViewById(R.id.database_cancel);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // return to previous activity
                dismiss();
            }
        });

        final Button button_delete_all = view.findViewById(R.id.database_delete_all);
        button_delete_all.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mDatabaseHelper.clearAllSaves();
                dismiss();
            }
        });

        builder.setView(view);

        return builder.create();
    }
}
