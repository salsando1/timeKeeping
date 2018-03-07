package sandoval.cis2237.com.timekepping_bysalvador;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

/**
 * Created by ssandoval114 on 1/20/2017.
 */
public class SimpleCursorAdapterForTotalPerDate extends SimpleCursorAdapter {

    private Cursor cursor;
    private Context context;
    private int layout;
    private  String[] from;
    private int[] to;
    private int flags;

    public SimpleCursorAdapterForTotalPerDate(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
        this.layout = layout;
        this.cursor = c;
        this.from = from;
        this.to = to;
        this.flags = flags;
    }

    @Override
    public void changeCursor(Cursor cursor){
        super.changeCursor(cursor);
        this.cursor = cursor;

    }
}
