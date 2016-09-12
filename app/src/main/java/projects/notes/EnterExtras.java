package projects.notes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EnterExtras extends AppCompatActivity implements View.OnClickListener{
    EditText edtDate;
    EditText edtTheme;
    EditText edtNameNote;
    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_extras);

        edtDate = (EditText) findViewById(R.id.edtDate);
        edtTheme = (EditText) findViewById(R.id.edtTheme);
        edtNameNote = (EditText) findViewById(R.id.edtNameNote);
        btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd - MM - yyyy");
        String s = simpleDateFormat.format(date);
        edtDate.setText(s);
        Log.d("my tag", date.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enter_extras, menu);
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
        Intent intent = new Intent();
        intent.putExtra("date", edtDate.getText().toString());
        intent.putExtra("theme", edtTheme.getText().toString());
        intent.putExtra("nameNote", edtNameNote.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
