package airbornegamer.com.grgr4;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class StatesRow {

    public BitmapDrawable StatePic;
    public String StateName;

    public StatesRow() {
        super();
    }

    public StatesRow(BitmapDrawable statePic, String StateName){
        super();
        this.StatePic = statePic;
        this.StateName = StateName;
    }

}