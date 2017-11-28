package mobileappdevelopment.kevinholmes.gameoflife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import static android.os.Debug.waitForDebugger;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.mDatabaseHelper;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.selectedGrid;

/**
 * Created by George Le on 11/14/2017.
 */

public class PasteFragment extends DialogFragment{

    // button cancelling selecting from the database
    private Button mButtonCancel;

    static PasteFragment newInstance(String title) {
        PasteFragment f = new PasteFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        waitForDebugger();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater factory = LayoutInflater.from(getActivity());

        final View view = factory.inflate(R.layout.paste_fragment, null);

        PasteBitmapAdapter adapter = new PasteBitmapAdapter(this.getContext(), mDatabaseHelper.getPreviewImages());

        final ListView listView = (ListView) view.findViewById(R.id.paste_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object o =listView.getItemAtPosition(i);
                selectedGrid = ((Pair<Long, int[][]>)o).first;
                dismiss();
            }
        });


        final Button button = view.findViewById(R.id.paste_cancel);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // return to previous activity
                dismiss();
            }
        });

        builder.setView(view);

        return builder.create();
    }
}