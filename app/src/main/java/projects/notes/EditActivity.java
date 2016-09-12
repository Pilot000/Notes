package projects.notes;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity implements View.OnClickListener{
    DBHelper dbHelper;
    Button btnSaveEdit;
    SQLiteDatabase db;
    EditText edtMulti;
    String idNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        dbHelper = new DBHelper(this);
        Intent intent = getIntent();
        idNote = intent.getStringExtra("noteId");
        Log.d("my tag", " id Note = " + idNote);
        db = dbHelper.getWritableDatabase();
        btnSaveEdit = (Button) findViewById(R.id.btnSaveEdit);
        btnSaveEdit.setOnClickListener(this);
        edtMulti = (EditText) findViewById(R.id.editText_multiEdit);
        edtMulti.setTextSize(MainActivity.size2);
        Cursor c = null;
        c = db.query("NotesTable", new String[]{"note"}, "_id = ?", new String[]{idNote}, null, null, null);
        String s;
        if(c.moveToFirst()){
            s = c.getString(c.getColumnIndex("note"));
            edtMulti.setText(s);

        }
        c.close();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnSaveEdit:
                ContentValues cv = new ContentValues();
                cv.put("note", edtMulti.getText().toString());
                db.update("NotesTable", cv, "_id = ?", new String[]{idNote});
                dbHelper.close();
                break;
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
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



    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context){
            super(context, "myDB", null, 1);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
