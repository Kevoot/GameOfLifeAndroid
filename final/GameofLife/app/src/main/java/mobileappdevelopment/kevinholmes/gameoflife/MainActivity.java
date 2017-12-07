package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements PasteCloseListener, DatabaseManagementListener {
    private static ImageButton mNewGridButton;
    private static ImageButton mPaintButton;
    private static ImageButton mRandomizeButton;
    private static ImageButton mCutButton;
    private static ImageButton mCopyButton;
    private static ImageButton mPasteButton;
    private static ImageButton mSaveAllButton;
    public static SerializableCellGrid pasteGrid;
    public static CellGridView mCellGridView;

    public static DatabaseHelper mDatabaseHelper;

    // Indicates whether painting currently or not
    public static boolean paintingFlag;
    public static boolean selectingFlag;
    public static boolean pastingFlag;

    public static boolean initialized;

    public static SerializableCellGrid mPasteGrid;

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

        mNewGridButton = (ImageButton) findViewById(R.id.newGridButton);
        mNewGridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!paintingFlag && !selectingFlag && !pastingFlag) {
                    if (mCellGridView.initFlag) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext())
                                .setTitle("Discard?")
                                .setMessage("Do you want to discard the current grid?");
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        });
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mCellGridView.initBlankGrid();
                            }
                        });
                        builder.show();
                    }
                    else
                    {
                        mCellGridView.initBlankGrid();
                    }
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
                    if(mCellGridView.mPaintGrid != null) {
                        for (int i = 0; i < mCellGridView.mPaintGrid.length; i++) {
                            for (int j = 0; j < mCellGridView.mPaintGrid.length; j++) {
                                if (mCellGridView.mPaintGrid[i][j]) {
                                    mCellGridView.mCellGrid[i][j] = true;
                                }
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
                if(selectingFlag && initialized) {
                    mCellGridView.pause();
                    if(!mDatabaseHelper.saveGrid(mCellGridView.copySelected())) {
                        throw new Error("Could not save selection to local database!");
                    }
                    mCellGridView.deselect();
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
                    if(showPasteFragment()) {
                        mCellGridView.pause();
                    }
                    else return;
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

    private boolean showPasteFragment() {
        try {
            PasteActivity pf = new PasteActivity();
            int result = 0;
            Intent i = new Intent(getApplicationContext(),PasteActivity.class);
            startActivityForResult(i, RESULT_OK);
        } catch(Exception e) {
            return false;
        }
        return true;
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
                }
                DatabaseFragment dbf = new DatabaseFragment();
                dbf.show(getFragmentManager(), "database managing");
                // Resume occurs during callback

                return true;
            case R.id.change_speed:
                if (mCellGridView.initFlag) {
                    ShowSpeedDialog();
                }
                return true;
            case R.id.set_grid_size:
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
            public void onStartTrackingTouch(SeekBar arg0) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Button OK
        popDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
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
        popDialog.setTitle("Please Select The Size");
        layout.addView(seek);
        TextView myMsg = new TextView(this);
        myMsg.setText("Larger Cells       Smaller Cells");
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(myMsg);

        popDialog.setView(layout);

        final int oldAdjust = mCellGridView.xAdjust;

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                //Do something here with new value
                if(progress > 45) progress = 45;
                if(progress < 5) progress = 5;
                xAdjust = yAdjust = 50-progress;
                mCellRadius = xAdjust / 2;
            }

            public void onStartTrackingTouch(SeekBar arg0) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        // Button OK
        popDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(mCellGridView.initFlag) {
                            if (xAdjust != oldAdjust) {
                                //Re randomize grid if it changed, otherwise leave it be.
                                mCellGridView.initRandomGrid();
                            }
                            mCellGridView.resume();
                        }
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

    @Override
    public void dml_handleDialogClose(DialogInterface dialogInterface) {
        if (mCellGridView.initFlag) {
            mCellGridView.resume();
        }
    }
}

