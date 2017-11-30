package mobileappdevelopment.kevinholmes.gameoflife;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.view.Gravity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.TextView;
import android.widget.LinearLayout;

import static mobileappdevelopment.kevinholmes.gameoflife.CellGridView.mCellRadius;
import static mobileappdevelopment.kevinholmes.gameoflife.CellGridView.xAdjust;
import static mobileappdevelopment.kevinholmes.gameoflife.CellGridView.yAdjust;

public class MainActivity extends AppCompatActivity implements PasteCloseListener {
    private CellGridView mCellGridView;

    private static ImageButton mNewGridButton;
    private static ImageButton mPaintButton;
    private static ImageButton mRandomizeButton;
    private static ImageButton mCutButton;
    private static ImageButton mCopyButton;
    private static ImageButton mPasteButton;
    private static ImageButton mSaveAllButton;
    public static SerializableCellGrid pasteGrid;

    public static DatabaseHelper mDatabaseHelper;

    // Indicates whether painting currently or not
    public static boolean paintingFlag;
    public static boolean selectingFlag;
    public static boolean pastingFlag;

    // TODO: James, there's a static already contained in CellGridView,
    // is this different?
    public static boolean initialized;

    public SerializableCellGrid mPasteGrid;

    public static long selectedGrid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mCellGridView = (CellGridView) findViewById(R.id.cellGridView);

        mDatabaseHelper = new DatabaseHelper(this);
        paintingFlag = false;
        selectingFlag = false;
        pastingFlag = false;

        // TODO: (Anyone): Add button disabling based on context
        // I.E. if Paint is selected, all other buttons should be temporarily disabled
        // until the paint function is completed. If a selection is occuring, paste/paint/newCanvas
        // buttons should be disabled. Cut/copy should only be available when a selection is
        // available



        mNewGridButton = (ImageButton) findViewById(R.id.newGridButton);
        mNewGridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!paintingFlag && !selectingFlag && !pastingFlag) {
                    if (mCellGridView.initFlag) {
                        // TODO: (George): confirm dialog to discard current grid (check to make sure)
                        // return true for confirm, false for cancel
                        // if(false) return;
                        // else let fall through
                    }
                    mCellGridView.initBlankGrid();
                    SetState(false, false, false);
                }
            }
        });

        mPaintButton = (ImageButton) findViewById(R.id.paintButton);
        mPaintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!paintingFlag && !selectingFlag && !pastingFlag) {
                    // Set state to disable buttons
                    SetState(true, false, false);
                    // Pause the simulation to allow painting to not be interrupted
                    mCellGridView.pause();
                    // Check to make sure we have a canvas to draw on
                    if(!mCellGridView.initFlag) mCellGridView.initBlankGrid();
                    // Remove selection listener and switch to painting
                    mCellGridView.setOnTouchListener(mCellGridView.mTouchPaintHandler);
                } else if (paintingFlag) {
                    // Set state back to normal because were done painting
                    SetState(false, false, false);
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
                if (!paintingFlag && !selectingFlag && !pastingFlag) {
                    mCellGridView.pause();
                    mCellGridView.initRandomGrid();
                    SetState(false, false, false);
                }
            }
        });

        mCutButton = (ImageButton) findViewById(R.id.cutButton);
        mCutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pause simulation
                if(selectingFlag && initialized) {
                    mCellGridView.pause();
                    // TODO: Copy contents to local DB, don't delete unless save works
                    if(mDatabaseHelper.saveGrid(mCellGridView.copySelected())) {
                        mCellGridView.deleteSelected();
                    } else throw new Error("Could not save selection to local database!");

                    // Either way, un-select and resume
                    mCellGridView.deselect();
                    mCellGridView.DrawGrid();
                    mCellGridView.resume();
                    SetState(false, false, false);
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
                if(selectingFlag && initialized) {
                    mCellGridView.pause();
                    // TODO: Copy the cell grid values into the local DB (Will have to scale to get
                    // TODO: correct values)
                    if(!mDatabaseHelper.saveGrid(mCellGridView.copySelected())) {
                        throw new Error("Could not save selection to local database!");
                    }
                    mCellGridView.DrawGrid();
                    mCellGridView.resume();
                    SetState(false, false, false);
                }
            }
        });

        mPasteButton = (ImageButton) findViewById(R.id.pasteButton);
        mPasteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Temporary for testing paste functionality

                if(!selectingFlag && !pastingFlag && initialized && !paintingFlag) {
                    pastingFlag = true;
                    mCellGridView.pause();
                    // TODO: Begin db fragment
                            // sets the selected paste section
                            showPasteFragment();
                            // In db fragment, set selectedGrid to the id of the one tapped by the user
                } else if (!selectingFlag && pastingFlag && initialized) {
                    boolean[][] cells = mPasteGrid.getCellGrid();
                    mCellGridView.transferCellsFromPaste(cells,
                            ((mCellGridView.x2 / xAdjust) - 1 - (cells.length/2)),/////////
                            ((mCellGridView.y2 / yAdjust) - 1 - (cells[0].length/2)));/////////
                    mCellGridView.DrawGrid();
                    mCellGridView.resume();
                    pastingFlag = false;
                    mCellGridView.setOnTouchListener(mCellGridView.mTouchSelectionHandler);
                    SetState(false, false ,false);
                }

            }
        });

        mSaveAllButton = (ImageButton) findViewById(R.id.saveButton);
        mSaveAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!paintingFlag && !selectingFlag && initialized && !pastingFlag) {
                    mCellGridView.pause();
                    // TODO: Save whole grid to DB
                    if(!mDatabaseHelper.saveGrid(
                            mCellGridView.mCellGrid)) {
                        throw new Error("Could not save grid to local database!");
                    }
                    mCellGridView.resume();
                    SetState(false, false, false);
                }
            }
        });

        // create initial state of not selecting or painting or pasting
        SetState(false, false, false);
        initialized = false;
    }

    private void showPasteFragment() {
        PasteFragment pf = new PasteFragment();
        pf.show(getFragmentManager(), "pasting");
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
                if (mCellGridView.initFlag) {
                    mCellGridView.pause();
                    // TODO: (Alex & George): bring up save management fragment for deleting
                    // previously saved selections & full grids
                    // After completion, resume
                    mCellGridView.resume();
                }
                return true;
            case R.id.change_speed:
                if (mCellGridView.initFlag) {
                    ShowSpeedDialog();
                }
                return true;
            case R.id.set_grid_size:
                mCellGridView.initFlag = false;
                mCellGridView.pause();
                ShowSizeDialog();
                return true;
            case R.id.clear_all_saves:
                mCellGridView.pause();
                ShowClearDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static boolean SetState (boolean painting, boolean selected, boolean pasting){
        float off = (float)0.1;
        float on = (float)1.0;

        paintingFlag = painting;
        selectingFlag = selected;
        pastingFlag = pasting;

        if (!initialized){
            mNewGridButton.setAlpha(on);
            mPaintButton.setAlpha(on);
            mRandomizeButton.setAlpha(on);
            mCutButton.setAlpha(off);
            mCopyButton.setAlpha(off);
            mPasteButton.setAlpha(off);
            mSaveAllButton.setAlpha(off);
            return true;
        }

        // paint mode
        if (painting && !selected && !pasting){
            mNewGridButton.setAlpha(off);
            mPaintButton.setAlpha(on);
            mRandomizeButton.setAlpha(off);
            mCutButton.setAlpha(off);
            mCopyButton.setAlpha(off);
            mPasteButton.setAlpha(off);
            mSaveAllButton.setAlpha(off);
            return true;
        }
        // selection mode
        if (!painting && selected && !pasting){
            mNewGridButton.setAlpha(off);
            mPaintButton.setAlpha(off);
            mRandomizeButton.setAlpha(off);
            mCutButton.setAlpha(on);
            mCopyButton.setAlpha(on);
            mPasteButton.setAlpha(off);
            mSaveAllButton.setAlpha(off);
            return true;
        }

        // pasting mode
        if (!painting && !selected && pasting){
            mNewGridButton.setAlpha(off);
            mPaintButton.setAlpha(off);
            mRandomizeButton.setAlpha(off);
            mCutButton.setAlpha(off);
            mCopyButton.setAlpha(off);
            mPasteButton.setAlpha(on);
            mSaveAllButton.setAlpha(off);
            return true;
        }

        // running mode
        if (!painting && !selected && !pasting){
            mNewGridButton.setAlpha(on);
            mPaintButton.setAlpha(on);
            mRandomizeButton.setAlpha(on);
            mCutButton.setAlpha(off);
            mCopyButton.setAlpha(off);
            mPasteButton.setAlpha(on);
            mSaveAllButton.setAlpha(on);
            return true;
        }
        return false;
    }

    public void ShowClearDialog(){
        final AlertDialog.Builder clearDialog = new AlertDialog.Builder(this);
        clearDialog.setTitle("Clear All Saves");
        clearDialog.setMessage("Are you sure you want to clear all saves?");
        clearDialog.setIcon(android.R.drawable.ic_dialog_alert);
        clearDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                mDatabaseHelper.clearAllSaves();
            }});
        clearDialog.setNegativeButton(android.R.string.no, null);
        clearDialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(mCellGridView.initFlag)
                    mCellGridView.resume();
            }
        });
        clearDialog.create();

        clearDialog.show();
    }

    public void ShowSpeedDialog()
    {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final SeekBar seek = new SeekBar(this);
        seek.setMax(2000);
        seek.setProgress(2001-mCellGridView.mDelay);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        popDialog.setTitle("Please Select The Speed");
        layout.addView(seek);
        TextView myMsg = new TextView(this);
        myMsg.setText("Slower         Faster");
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(myMsg);

        popDialog.setView(layout);


        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                //Do something here with new value
                mCellGridView.mDelay = 2000-progress;
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
        });


        // Button OK
        popDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // mCellGridView.resume();
                        dialog.dismiss();
                    }

                });


        popDialog.create();
        popDialog.show();

    }

    public void ShowSizeDialog()
    {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final SeekBar seek = new SeekBar(this);
        seek.setMax(50);
        seek.setProgress(50-mCellGridView.xAdjust);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        popDialog.setTitle("Please Select The Speed");
        layout.addView(seek);
        TextView myMsg = new TextView(this);
        myMsg.setText("Larger Cells       Smaller Cells");
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(myMsg);

        popDialog.setView(layout);


        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                //Do something here with new value
                if(progress > 45) progress = 45;
                if(progress < 5) progress = 5;
                xAdjust = yAdjust = 50-progress;
                mCellRadius = xAdjust / 2;
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
        });


        // Button OK
        popDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mCellGridView.initRandomGrid();
                        mCellGridView.resume();
                        dialog.dismiss();
                    }

                });


        popDialog.create();
        popDialog.show();

    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        if(selectedGrid == -1) {
            pastingFlag = false;
            mCellGridView.resume();
            return;
        }
        mPasteGrid = mDatabaseHelper.requestGrid(selectedGrid);
        mCellGridView.setPreviewBitmap((mPasteGrid.mPreviewBitmap.currentImage));
        mCellGridView.setOnTouchListener(mCellGridView.mTouchPasteHandler);
        mCellGridView.setPreview();
        SetState(false, false, true);
    }
}

