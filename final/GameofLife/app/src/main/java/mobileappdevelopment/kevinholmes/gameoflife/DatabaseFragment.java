package mobileappdevelopment.kevinholmes.gameoflife;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.v4.util.Pair;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.mDatabaseHelper;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.selectedGrid;

/**
 * Created by George Le on 11/29/2017.
 */

public class DatabaseFragment extends DialogFragment {

    ArrayList<SerializableCellGrid> gridsToBeDeleted;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater factory = LayoutInflater.from(getActivity());

        final View view = factory.inflate(R.layout.db_fragment, null);

        final DatabaseManagementAdapter adapter = new DatabaseManagementAdapter(this.getContext(), mDatabaseHelper.getPreviewImages());

        final ListView listView = (ListView) view.findViewById(R.id.database_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapter.checked == false) {
                    gridsToBeDeleted.add(adapter.getItem(i));
                    adapter.checked = true;
                }
                else if(adapter.checked == true) {
                    gridsToBeDeleted.remove(adapter.getItem(i));
                    adapter.checked = false;
                }
            }
        });

        final Button button = view.findViewById(R.id.database_cancel);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // return to previous activity
                selectedGrid = -1;
                dismiss();
            }
        });

        final Button delete_button = view.findViewById(R.id.database_delete);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < gridsToBeDeleted.size(); ++i) {
                    adapter.remove((SerializableCellGrid) gridsToBeDeleted.get(i));
                    mDatabaseHelper.clearSave(gridsToBeDeleted.get(i).id);
                }
                gridsToBeDeleted.clear();
                dismiss();
            }
        });

        builder.setView(view);

        return builder.create();
    }

    public void onDismiss(DialogInterface dialog)
    {
        Activity activity = getActivity();
        if(activity instanceof DatabaseManagementListener)
            ((DatabaseManagementListener)activity).dml_handleDialogClose(dialog);
    }


}
