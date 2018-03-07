package sandoval.cis2237.com.timekepping_bysalvador;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * Created by ssandoval114 on 1/5/2017.
 */
public class TimeKeepinDbAdapter {


    //these are the column names
    public static final String COL_ID = "_id";
    public static final String COL_TIME_IN = "timeIn";
    public static final String COL_TIME_OUT = "timeOut";
    public static final String COL_DATE = "date";
    public static final String COL_TOTAL_HOURS = "total_hours";
    public static final String COL_TOTAL_MINUTES = "total_minutes";

    //these are the corresponding indices
    public static final int INDEX_ID = 0;
    public static final int INDEX_TIME_IN = INDEX_ID + 1;
    public static final int INDEX_TIME_OUT = INDEX_ID + 2;
    public static final int INDEX_DATE = INDEX_ID + 3;
    public static final int INDEX_TOTAL_HOURS = INDEX_ID + 4;
    public static final int INDEX_TOTAL_MINUTES = INDEX_ID + 5;
    //used for logging
    private static final String TAG = "TimeKeepingDbAdapter";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "dba_timekeeping";
    private static final String TABLE_NAME = "tbl_time_keeping";
    private static final int DATABASE_VERSION = 1;
    //SQL statement used to create the database
    private final Context mCtx;
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                    COL_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    COL_TIME_IN + " TEXT, " +
                    COL_TIME_OUT + " TEXT, " +
                    COL_DATE + " TEXT,"+
                    COL_TOTAL_HOURS + " INT," +
                    COL_TOTAL_MINUTES + " INT );";


    public  TimeKeepinDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    //open the database
    public void open() throws SQLException {
        dbHelper = new DatabaseHelper(mCtx);
        db = dbHelper.getWritableDatabase();
    }
    //close the database
    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    //CREATE
    //note that the id will be created for you automatically
    public void timeEntry(String date, String timeIn, String timeOut,int total_hours, int total_minutes) {
        ContentValues values = new ContentValues();
        values.put(COL_TIME_IN, timeIn );
        values.put(COL_TIME_OUT, timeOut);
        values.put(COL_DATE, date);
        values.put(COL_TOTAL_HOURS, total_hours);
        values.put(COL_TOTAL_MINUTES,total_minutes);
        db.insert(TABLE_NAME, null, values);
    }
    //overloaded to take a reminder
    public long timeEntry(TimeKeeping timeKeeping) {
        ContentValues values = new ContentValues();
        values.put(COL_TIME_IN, timeKeeping.getInTime());
        values.put(COL_TIME_OUT, timeKeeping.getOutTime());
        values.put(COL_DATE, timeKeeping.getDate());
        values.put(COL_TOTAL_HOURS, timeKeeping.getTotalHours());
        values.put(COL_TOTAL_MINUTES, timeKeeping.getTotalMinutes());
        // Inserting Row
        return db.insert(TABLE_NAME, null, values);
    }
    // adding all the hours method
    public  int totalHours(){
        int number = 0;

        SQLiteStatement statement = db.compileStatement("SELECT sum(total_hours) FROM tbl_time_keeping;");

        long result = statement.simpleQueryForLong();

        number = (int) result ;

        return number;
    }
     // adding all the minutes method
    public int totalMinutes(){
         int number;

        SQLiteStatement statement = db.compileStatement("SELECT sum(total_minutes) FROM tbl_time_keeping;");

        long result = statement.simpleQueryForLong();

        number = (int) result;

        return number;
    }
    // adding all the hours for certain date
    public int totalHoursPerDate(String date){
        SQLiteStatement statement = db.compileStatement("SELECT sum(total_hours)" +
                                                        "FROM tbl_time_keeping" +
                                                         " WHERE " + COL_DATE + " LIKE '"+date+"%';");

        long result = statement.simpleQueryForLong();

        return (int)result;
    }
    // adding all the minutes for certain date
    public int totalMinutesPerDate(String date){
        SQLiteStatement statement = db.compileStatement("SELECT sum(total_minutes)" +
                                                        "FROM tbl_time_keeping" +
                                                        " WHERE " + COL_DATE + " LIKE '"+date+"%';");

        long result = statement.simpleQueryForLong();

        return (int)result;
    }
    //READ
    public TimeKeeping timeKeepingById(int id) {

        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_ID,
                        COL_TIME_IN, COL_TIME_OUT,COL_DATE,COL_TOTAL_MINUTES,COL_TOTAL_HOURS}, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null
        );
        if (cursor != null)
            cursor.moveToFirst();

        return new TimeKeeping(


                cursor.getInt(INDEX_ID),
                cursor.getString(INDEX_TIME_IN),
                cursor.getString(INDEX_TIME_OUT),
                cursor.getString(INDEX_DATE),
                cursor.getInt(INDEX_TOTAL_HOURS),
                cursor.getInt(INDEX_TOTAL_MINUTES)
        );
    }

    public String getDateByid(int id){
        SQLiteStatement statement = db.compileStatement("SELECT DISTINCT " + COL_DATE + " FROM tbl_time_keeping " +
                                                        "WHERE " + COL_ID + " = " + id + ";");

        String result = statement.simpleQueryForString();

        return result;

    }

    public Cursor fetchAllRemindersByDate(String date){

        Cursor mCursor = db.query(TABLE_NAME, new String[]{COL_ID,
                        COL_TIME_IN, COL_TIME_OUT,COL_DATE,COL_TOTAL_MINUTES,COL_TOTAL_HOURS},
                COL_DATE + " LIKE " + "'" + date + "%'", null, null, null, null
        );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    public Cursor fetchAllReminders() {
        Cursor mCursor = db.query(true,TABLE_NAME, new String[]{COL_ID,
                COL_TIME_IN, COL_TIME_OUT,COL_DATE,COL_TOTAL_HOURS,COL_TOTAL_MINUTES},
                null, null, COL_DATE, null,null,null
        );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //UPDATE
    public void updateTimeKeeping(TimeKeeping timeKeeping) {
        SQLiteStatement statement = db.compileStatement("UPDATE " + TABLE_NAME +
                " SET " + COL_TOTAL_MINUTES + " = " + timeKeeping.getTotalMinutes() + ", " +
                          COL_TOTAL_HOURS + " = " + timeKeeping.getTotalHours() + ", " +
                          COL_TIME_OUT + " = " + "'"+timeKeeping.getOutTime()+"'" + ", " +
                          COL_TIME_IN + " = " +"'" + timeKeeping.getInTime() + "'" +
                " WHERE " + COL_ID + " = " + timeKeeping.getId() +";" );

        statement.execute();
    }

    //DELETE
    public void deleteTimeKeepingById(int nId) {
        SQLiteStatement statement = db.compileStatement("DELETE FROM " + TABLE_NAME +
                " WHERE " + COL_ID + " = " + nId +";" );

        statement.execute();

    }

    public void deleteTimeByDate(String date){
        SQLiteStatement statement = db.compileStatement("DELETE FROM " + TABLE_NAME +
                                                        " WHERE " + COL_DATE + " LIKE '" + date +"%';" );

        statement.execute();

    }
    public void deleteAllReminders() {
        db.delete(TABLE_NAME, null, null);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

}
