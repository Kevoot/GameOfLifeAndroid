package mobileappdevelopment.kevinholmes.gameoflife;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import static android.os.Debug.waitForDebugger;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.SetState;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.mCellGridView;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.mDatabaseHelper;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.mPasteGrid;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.pastingFlag;
import static mobileappdevelopment.kevinholmes.gameoflife.MainActivity.selectedGrid;

public class PasteActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paste_fragment);

        PasteBitmapAdapter adapter = new PasteBitmapAdapter(this, mDatabaseHelper.getPreviewImages());

        final ListView listView = (ListView) findViewById(R.id.paste_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object o =listView.getItemAtPosition(i);
                SerializableCellGrid s = ((SerializableCellGrid)o);
                selectedGrid = s.id;
                mPasteGrid = mDatabaseHelper.requestGrid(selectedGrid);
                mCellGridView.setPreviewBitmap((mPasteGrid.mPreviewBitmap.currentImage));
                mCellGridView.setOnTouchListener(mCellGridView.mTouchPasteHandler);
                mCellGridView.setPreview();
                SetState(false, false, true);
                finish();
            }
        });

        final Button button = (Button) findViewById(R.id.paste_cancel);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // return to previous activity
                selectedGrid = -1;
                pastingFlag = false;
                mCellGridView.resume();
                finish();
            }
        });
    }
}