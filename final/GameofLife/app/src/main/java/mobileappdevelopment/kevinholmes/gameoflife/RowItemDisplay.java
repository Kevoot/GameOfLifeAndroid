package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class RowItemDisplay extends RelativeLayout{
    public long id;
    public ImageView view;
    public CheckBox cb;

    public RowItemDisplay(Context context) {
        super(context);
    }
}
