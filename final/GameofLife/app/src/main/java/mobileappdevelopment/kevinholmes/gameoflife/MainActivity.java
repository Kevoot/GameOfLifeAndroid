package mobileappdevelopment.kevinholmes.gameoflife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private CellGridView mCellGridView;

    public static boolean cutSelected = false;
    public static boolean copySelected = false;
    public static boolean saveSelected = false;
    public static boolean dbSelected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mCellGridView = (CellGridView) findViewById(R.id.cellGridView);

        /* TODO: Need to fix button interactions, so only one is selectedable at any given time
        *  additionally, placing some highlighting on the button during selection and disabling
        *  the rest would be nice
        */

        final ImageButton cutButton = (ImageButton) findViewById(R.id.cutButton);
        cutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pause simulation
                if(!cutSelected) {
                    cutSelected = true;
                    mCellGridView.pause();
                    mCellGridView.deleteSelected();
                    mCellGridView.unselect();

                    // TODO: Begin cut fragment

                } else {
                    cutSelected = false;
                    mCellGridView.resume();
                }
            }
        });

        final ImageButton copyButton = (ImageButton) findViewById(R.id.copyButton);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pause simulation
                // James - Thinking that if I understand this correct, cut should just make
                //      the cells dead in the selected area. If this is correct, should we pause
                //      the simulation when we cut or can we just keep it running?
                // Kevin - I think we still want to copy the values out before destroying those cells.
                //         database should automatically get updated with a copy of that selection
                //         every time a copy or cut operation occurs.
                if(!copySelected) {
                    copySelected = true;
                    mCellGridView.pause();
                    mCellGridView.deleteSelected();
                } else {
                    copySelected = false;
                    mCellGridView.resume();
                }
            }
        });

        final ImageButton saveButton = (ImageButton) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pause simulation
                if(!saveSelected) {
                    saveSelected = true;
                    mCellGridView.pause();
                    // TODO: Begin save fragment

                } else {
                    saveSelected = false;
                    mCellGridView.resume();
                }
            }
        });

        final ImageButton dbButton = (ImageButton) findViewById(R.id.dbButton);
        dbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pause simulation
                if(!dbSelected) {
                    dbSelected = true;
                    mCellGridView.pause();
                    // TODO: Begin db fragment

                } else {
                    dbSelected = false;
                    mCellGridView.resume();
                }
            }
        });

        final ImageButton randomizeButton = (ImageButton) findViewById(R.id.randomizeButton);
        randomizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCellGridView.pause();
                mCellGridView.initRandomGrid();
            }
        });

    }

    @Override
    public void onResume() {
        // Once application starts running normally, get CellGridView's dimensions and init
        super.onResume();
    }
}

