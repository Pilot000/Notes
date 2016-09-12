package projects.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Prof on 19.03.2016.
 */
public class DB {
    private static final String DB_NAME = "myDB";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "NotesTable";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_THEME = "theme";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NOTE = "note";

    private DBHelper dbHelper;
    private SQLiteDatabase mDB;
    private final Context ctx;

    public DB(Context mtx){
        ctx = mtx;
    }

    public void open(){
        dbHelper = new DBHelper(ctx);
        mDB = dbHelper.getWritableDatabase();
    }

    public void close(){
        if(mDB !=null){
            dbHelper.close();
        }
    }

    public Cursor getData(int sortType, String forLike){
        String orderBy = null;
        switch (sortType) {
            case 0:
                orderBy = null;
                break;
            case 4:
                orderBy = "date";
                break;
            case 5:
                orderBy = "date DESC";
                break;
            case 6:
                orderBy = "theme";
                break;
            case 7:
                orderBy = "theme DESC";
                break;
            case 8:
                orderBy = "name";
                break;
            case 9:
                orderBy = "name DESC";
                break;
        }
        return mDB.query(DB_TABLE, null, forLike, null, null, null, orderBy);
    }

    public void addNote(String date, String theme, String name, String note){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_THEME, theme);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_NOTE, note);
        mDB.insert(DB_TABLE, null, cv);
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table NotesTable (" +
                    "_id integer primary key autoincrement, " +
                    "date text, " +
                    "theme text, " +
                    "name text, " +
                    "note text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
