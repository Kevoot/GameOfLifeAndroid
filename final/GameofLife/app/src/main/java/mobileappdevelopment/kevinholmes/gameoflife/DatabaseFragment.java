package mobileappdevelopment.kevinholmes.gameoflife;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.mDatabaseHelper;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.selectedGrid;

public class DatabaseFragment extends DialogFragment {

    ArrayList<SerializableCellGrid> gridsToBeDeleted;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater factory = LayoutInflater.from(getActivity());
        gridsToBeDeleted = new ArrayList<>();

        this.gridsToBeDeleted = new ArrayList<>();

        final View view = factory.inflate(R.layout.db_fragment, null);

        final DatabaseManagementAdapter adapter = new DatabaseManagementAdapter(this.getContext(), mDatabaseHelper.getPreviewImages());

        final ListView listView = (ListView) view.findViewById(R.id.database_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Added Kevin - should getting the individual list item, and it's cb.
                AppCompatCheckBox cb = (AppCompatCheckBox) ((ViewGroup)view).getChildAt(0);

                if(cb.isChecked()) {
                    if(gridsToBeDeleted.contains(adapter.getItem(i))) {
                        gridsToBeDeleted.remove(adapter.getItem(i));
                    }
                    cb.setChecked(false);
                }
                else {
                    if(!gridsToBeDeleted.contains(adapter.getItem(i))) {
                        gridsToBeDeleted.add(adapter.getItem(i));
                    }
                    cb.setChecked(true);
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
                // for(int i = 0; i < gridsToBeDeleted.size(); ++i) {
                //     // adapter.remove((SerializableCellGrid) gridsToBeDeleted.get(i));
                //     mDatabaseHelper.clearSave(gridsToBeDeleted.get(i).id);
                // }
                for(SerializableCellGrid s : gridsToBeDeleted) {
                    mDatabaseHelper.clearSave(s.id);
                }
                gridsToBeDeleted = null;
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
