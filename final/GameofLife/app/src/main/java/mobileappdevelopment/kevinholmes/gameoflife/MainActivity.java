package mobileappdevelopment.kevinholmes.gameoflife;

import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private CellGridView mCellGridView;

    private ImageButton mNewGridButton;
    private ImageButton mPaintButton;
    private ImageButton mRandomizeButton;
    private ImageButton mCutButton;
    private ImageButton mCopyButton;
    private ImageButton mPasteButton;
    private ImageButton mSaveAllButton;

    public DatabaseHelper mDatabaseHelper;

    // Indicates whether painting currently or not
    public boolean paintingFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mCellGridView = (CellGridView) findViewById(R.id.cellGridView);

        // TODO: (Alex): Make sure I'm setting the context correctly here
        mDatabaseHelper = new DatabaseHelper(this);

        // TODO: (Anyone): Add button disabling based on context
        // I.E. if Paint is selected, all other buttons should be temporarily disabled
        // until the paint function is completed. If a selection is occuring, paste/paint/newCanvas
        // buttons should be disabled. Cut/copy should only be available when a selection is
        // available

        mNewGridButton = (ImageButton) findViewById(R.id.newGridButton);
        mNewGridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCellGridView.initFlag) {
                    // TODO: (George): confirm dialog to discard current grid (check to make sure)
                    // return true for confirm, false for cancel
                    // if(false) return;
                    // else let fall through
                }
                mCellGridView.initBlankGrid();
            }
        });

        mPaintButton = (ImageButton) findViewById(R.id.paintButton);
        mPaintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!paintingFlag) {
                    paintingFlag = true;
                    mCellGridView.pause();
                    // Check to make sure we have a canvas to draw on
                    if(!mCellGridView.initFlag) mCellGridView.initBlankGrid();
                    // Remove selection listener and switch to painting
                    mCellGridView.setOnTouchListener(mCellGridView.mTouchPaintHandler);
                } else {
                    paintingFlag = false;
                    // Add the painted cells to the current simulation
                    for(int i=0; i<mCellGridView.mPaintGrid.length; i++) {
                        for(int j = 0; j < mCellGridView.mPaintGrid.length; j++) {
                            if(mCellGridView.mPaintGrid[i][j]) {
                                mCellGridView.mCellGrid[i][j] = true;
                            }
                        }
                    }
                    // ALWAYS clear paint grid when painting is complete
                    mCellGridView.mPaintGrid = null;
                    // Reset listener to selection mode
                    mCellGridView.setOnTouchListener(mCellGridView.mTouchSelectionHandler);
                    mCellGridView.resume();
                }
            }
        });

        mRandomizeButton = (ImageButton) findViewById(R.id.randomizeButton);
        mRandomizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCellGridView.pause();
                mCellGridView.initRandomGrid();
            }
        });

        mCutButton = (ImageButton) findViewById(R.id.cutButton);
        mCutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pause simulation
                if(mCellGridView.x1 + mCellGridView.x2 + mCellGridView.y1 + mCellGridView.y2 != 0) {
                    mCellGridView.pause();
                    Pair<boolean[][], int[][]> returnedGrids = mCellGridView.copySelected();
                    // TODO: Copy contents to local DB, don't delete unless save works
                    if(mDatabaseHelper.saveSelection(returnedGrids)) {
                        mCellGridView.deleteSelected();
                    } else throw new Error("Could not save selection to local database!");

                    // Either way, unselect and resume
                    mCellGridView.deselect();
                    mCellGridView.resume();
                }
            }
        });

        mCopyButton = (ImageButton) findViewById(R.id.copyButton);
        mCopyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pause simulation
                // James - Thinking that if I understand this correct, cut should just make
                //      the cells dead in the selected area. If this is correct, should we pause
                //      the simulation when we cut or can we just keep it running?
                // Kevin - I think we still want to copy the values out before destroying those cells.
                //         database should automatically get updated with a copy of that selection
                //         every time a copy or cut operation occurs. But this is the copy function
                //         so no deletion

                // Getting rid of those previous variables, just make sure there's a selection first
                if(mCellGridView.x1 + mCellGridView.x2 + mCellGridView.y1 + mCellGridView.y2 != 0) {
                    mCellGridView.pause();
                    // TODO: Copy the cell grid values into the local DB (Will have to scale to get
                    // TODO: correct values)
                    Pair<boolean[][], int[][]> returnedGrids = mCellGridView.copySelected();
                    if(!mDatabaseHelper.saveSelection(returnedGrids)) {
                        throw new Error("Could not save selection to local database!");
                    }
                    mCellGridView.resume();
                }
            }
        });

        mPasteButton = (ImageButton) findViewById(R.id.pasteButton);
        mPasteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCellGridView.x1 + mCellGridView.x2 + mCellGridView.y1 + mCellGridView.y2 != 0) {
                    mCellGridView.pause();
                    // TODO: Begin db fragment
                }
            }
        });

        mSaveAllButton = (ImageButton) findViewById(R.id.saveButton);
        mSaveAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCellGridView.x1 + mCellGridView.x2 + mCellGridView.y1 + mCellGridView.y2 != 0) {
                    mCellGridView.pause();
                    // TODO: Save whole grid to DB
                    if(!mDatabaseHelper.saveGrid(
                            new Pair<>(mCellGridView.mCellGrid, mCellGridView.mColorGrid))) {
                        throw new Error("Could not save grid to local database!");
                    }
                    mCellGridView.resume();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save_mgmt:
                mCellGridView.pause();
                // TODO: (Alex & George): bring up save management fragment for deleting
                // previously saved selections & full grids
                // After completion, resume
                mCellGridView.resume();
                return true;
            case R.id.change_speed:
                mCellGridView.pause();
                // TODO: (George): Create some UI element that allows for variable speed change
                // Will have to experiment with max / min speed to see what feels best.
                // Maybe add hardware polling to figure out what the phone can feasibly handle?
                // variable to change from result is the mDelay in mCellGridView
                mCellGridView.resume();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

