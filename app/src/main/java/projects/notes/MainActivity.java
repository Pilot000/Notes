package projects.notes;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private DBHelper dbHelper;
    private DB db;
    private ListView lv;
    protected static int size1 = 20;
    protected static int size2 = 30;
    protected static int sortType = 0;
    protected static String forLike = null;
    protected static int viewMode = 1;
    private SimpleCursorAdapter csd;
    private static final String KEY_SIZE1 = "size1";
    private static final String KEY_SIZE2 = "size2";
    private static final String KEY_SORTTYPE = "sortType";
    private static final String KEY_VIEWMODE = "viewMode";


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SIZE1, size1);
        outState.putInt(KEY_SIZE2, size2);
        outState.putInt(KEY_SORTTYPE, sortType);
        outState.putInt(KEY_VIEWMODE, viewMode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            size1 = savedInstanceState.getInt(KEY_SIZE1);
            size2 = savedInstanceState.getInt(KEY_SIZE2);
            sortType = savedInstanceState.getInt(KEY_SORTTYPE);
            viewMode = savedInstanceState.getInt(KEY_VIEWMODE);
        }
        Log.d("MyTag", "ViewMode = " + viewMode);
        if (viewMode == 1) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_main_2);
        }
        dbHelper = new DBHelper(this);
        db = new DB(this);
        db.open();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            forLike = "note like '%" + intent.getStringExtra(SearchManager.QUERY) + "%'" +
                    "or theme like '%" + intent.getStringExtra(SearchManager.QUERY) + "%'" +
                    "or name like '%" + intent.getStringExtra(SearchManager.QUERY) + "%'";
        } else {
            forLike = null;
        }
        setViewMode();
    }


    public void onClickMain(View v) {
        switch (v.getId()) {
            case R.id.addNote:
                Intent intent = new Intent(this, EnterData.class);
                Toast.makeText(this, "load...", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
            case R.id.btnDel:
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                RelativeLayout layFor = (RelativeLayout) v.getParent();
                TextView tvIdNote = (TextView) layFor.findViewById(R.id.tvIdNote);
                db.delete("NotesTable", "_id =" + tvIdNote.getText().toString(), null);
                if (viewMode == 0) {
                    getSupportLoaderManager().getLoader(0).forceLoad();
                } else {
                    onResume();
                }
                break;
            case R.id.btnEdit:
                RelativeLayout layId = (RelativeLayout) v.getParent();
                TextView tvIdNoteEdit = (TextView) layId.findViewById(R.id.tvIdNote);
                Intent intentEdit = new Intent(this, EditActivity.class);
                intentEdit.putExtra("noteId", tvIdNoteEdit.getText().toString());
                startActivity(intentEdit);
                break;
            case R.id.btnTheme:
                String[] from = {"theme"};
                int[] to = {android.R.id.text1};
                csd = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, 0) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        if (view instanceof TextView) {
                            ((TextView) view).setTextSize(size2);
                            ((TextView) view).setTextColor(Color.WHITE);
                        }
                        return view;
                    }
                };
                csd.swapCursor(this.db.getData(sortType, forLike));
                csd.notifyDataSetChanged();
                lv.setAdapter(csd);
                //getSupportLoaderManager().getLoader(0).forceLoad();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, 1, 1, "SORT");
        menu.add(0, 2, 2, "FONT SIZE");
        menu.add(0, 3, 3, "SEARCH");
        menu.add(0, 4, 4, "SHORT VIEW");
        menu.add(0, 5, 5, "EXTENDED VIEW");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case 1:
                View v = findViewById(R.id.viewForEmulSort);
                registerForContextMenu(v);
                openContextMenu(v);
                break;
            case 2:
                View v1 = findViewById(R.id.viewForEmulFont);
                registerForContextMenu(v1);
                openContextMenu(v1);
                break;
            case 3:
                onSearchRequested();
                break;
            case 4:
                viewMode = 0;
                Intent i = new Intent(this, this.getClass());
                finish();
                this.startActivity(i);
                break;
            case 5:
                viewMode = 1;
                Intent j = new Intent(this, this.getClass());
                finish();
                this.startActivity(j);
                break;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        switch (v.getId()) {
            case R.id.viewForEmulFont:
                menu.add(0, 1, 0, "small");
                menu.add(0, 2, 0, "middle");
                menu.add(0, 3, 0, "big");
                break;
            case R.id.viewForEmulSort:
                menu.add(0, 4, 0, "Date↓");
                menu.add(0, 5, 0, "Date↑");
                menu.add(0, 6, 0, "Theme↓");
                menu.add(0, 7, 0, "Theme↑");
                menu.add(0, 8, 0, "Name↓");
                menu.add(0, 9, 0, "Name↑");
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("mytag", "onPause");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                size1 = 15;
                size2 = 20;
                break;
            case 2:
                size1 = 18;
                size2 = 25;
                break;
            case 3:
                size1 = 20;
                size2 = 30;
                break;
            case 4:
                sortType = 4;
                break;
            case 5:
                sortType = 5;
                break;
            case 6:
                sortType = 6;
                break;
            case 7:
                sortType = 7;
                break;
            case 8:
                sortType = 8;
                break;
            case 9:
                sortType = 9;
                break;
        }
        onResume();
        return super.onContextItemSelected(item);
    }

    private void setViewMode() {
        if (viewMode == 1) {
            setSortTypeUser(sortType, forLike);
        } else {
            setShortMode();
        }
    }

    private boolean setShortMode() {
        //String[] from = {"date", "theme", "name", "note", "_id"};
        //int[] to = {R.id.tvDate, R.id.tvTheme, R.id.tvName, R.id.tvNote, R.id.tvIdNote};
        if(getSupportLoaderManager().hasRunningLoaders()){
            getSupportLoaderManager().getLoader(0).forceLoad();
            return false;
        }
        String[] from = {"theme"};
        int[] to = {android.R.id.text1};
        csd = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, 0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (view instanceof TextView) {
                    ((TextView) view).setTextSize(size2);
                    ((TextView) view).setTextColor(Color.WHITE);
                }
                return view;
            }
        };

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(csd);
        listView.setOnItemClickListener(new MyClickItem(this));
        getSupportLoaderManager().initLoader(0, null, this);
        return true;
    }

    class MyClickItem implements AdapterView.OnItemClickListener{
        Context mtx;
        public MyClickItem(Context ctx){
            mtx = ctx;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String tv = null;
            if(view instanceof TextView) {
                tv = ((TextView)view).getText().toString();
                Log.d("MyTag", "#" + tv);
            }
            if (view.getParent() instanceof ListView) {
                lv = (ListView) view.getParent();
                String[] from = {"date", "name", "note", "_id"};
                int[] to = {R.id.tvDate, R.id.tvName, R.id.tvNote, R.id.tvIdNote};
                csd = new SimpleCursorAdapter(mtx, R.layout.notes_view_2, null, from, to, 0){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        setFontsSizeUser(size1, size2, view);
                        return view;
                    }
                };
                csd.swapCursor(db.getData(sortType, "theme = '"+tv+"'"));
                csd.notifyDataSetChanged();
                lv.setAdapter(csd);
                //getSupportLoaderManager().getLoader(0).forceLoad();
            }
        }
    }

    private void setSortTypeUser(int i, String forLike) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.linLayout);
        LayoutInflater inflater = getLayoutInflater();
        layout.removeAllViews();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"date", "theme", "name", "note", "_id"};
        String orderBy = null;
        switch (i) {
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
        Cursor c = db.query("NotesTable", columns, forLike, null, null, null, orderBy);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    View item = inflater.inflate(R.layout.notes_view, layout, false);
                    setFontsSizeUser(size1, size2, item);
                    TextView tvDate = (TextView) item.findViewById(R.id.tvDate);
                    tvDate.setText(c.getString(c.getColumnIndex(columns[0])));
                    TextView tvTheme = (TextView) item.findViewById(R.id.tvTheme);
                    tvTheme.setText(c.getString(c.getColumnIndex(columns[1])));
                    TextView tvName = (TextView) item.findViewById(R.id.tvName);
                    tvName.setText(c.getString(c.getColumnIndex(columns[2])));
                    TextView tvNote = (TextView) item.findViewById(R.id.tvNote);
                    tvNote.setText(c.getString(c.getColumnIndex(columns[3])));
                    TextView tvIdNote = (TextView) item.findViewById(R.id.tvIdNote);
                    tvIdNote.setText(c.getString(c.getColumnIndex(columns[4])));

                    layout.addView(item);
                } while (c.moveToNext());
            }
            c.close();
        }
        db.close();
    }

    private void setFontsSizeUser(int i, int j, View item) {
        TextView v1 = (TextView) item.findViewById(R.id.tvDate);
        TextView v3 = (TextView) item.findViewById(R.id.tvName);
        TextView v4 = (TextView) item.findViewById(R.id.textView4);

        TextView v6 = (TextView) item.findViewById(R.id.textView6);
        TextView v7 = (TextView) item.findViewById(R.id.tvNote);
        v1.setTextSize(i);
        v3.setTextSize(i);
        v4.setTextSize(i);

        v6.setTextSize(i);
        v7.setTextSize(j);
        try {
            TextView v2 = (TextView) item.findViewById(R.id.tvTheme);
            TextView v5 = (TextView) item.findViewById(R.id.textView5);
            v5.setTextSize(i);
            v2.setTextSize(i);
        }catch (Exception e){}
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyLoader(this, db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        csd.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    static class MyLoader extends CursorLoader {
        DB db;

        public MyLoader(Context context, DB mDb) {
            super(context);
            db = mDb;
        }

        @Override
        public Cursor loadInBackground() {
            return db.getData(sortType, forLike);
        }
    }

}
