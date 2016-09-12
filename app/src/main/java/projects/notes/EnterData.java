package projects.notes;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.*;
import android.widget.Button;
import android.widget.EditText;

public class EnterData extends AppCompatActivity implements OnKeyListener, OnClickListener {

    EditText edt;
    Button btnSave;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_data);
        edt = (EditText) findViewById(R.id.editText_multi);
        edt.setTextSize(MainActivity.size2);

        //Typeface font = Typeface.createFromAsset(getAssets(), "adanaScript.ttf");
        //edt.setTypeface(font);
        edt.setOnKeyListener(this);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        dbHelper = new DBHelper(this);
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.d("mytag", "load, load, load");
        if (event.getAction() == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
            // сохраняем текст, введенный до нажатия Enter в переменную
            int linNumber = edt.getLineCount();
            edt.append(linNumber + ") ");
            return true;
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, EnterExtras.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null){return;}
        String date = data.getStringExtra("date");
        String theme = data.getStringExtra("theme");
        String nameNote = data.getStringExtra("nameNote");
        String note = edt.getText().toString();

        ContentValues cv = new ContentValues();
        cv.put("date", date);
        cv.put("theme", theme);
        cv.put("name", nameNote);
        cv.put("note", note);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insert("NotesTable", null, cv);
        finish();
    }

    class DBHelper extends SQLiteOpenHelper{

        public DBHelper(Context context){
            super(context, "myDB", null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table NotesTable (" +
                    "id integer primary key, " +
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
