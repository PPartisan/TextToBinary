package com.werdpressed.partisan.texttobinary;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements TextWatcher {

    private static final String CLIP_TAG = "Binary String";

    private static final String ERROR_STRING = "nothing_to_see_here_move_along";

    private static final String INPUT_TAG = "input_tag";
    private static final String OUTPUT_TAG = "output_tag";

    private EditText input;
    private TextView output;

    private ClipboardManager clipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = (EditText) findViewById(R.id.input_et);
        output = (TextView) findViewById(R.id.output_tv);

        input.addTextChangedListener(this);
        output.setMovementMethod(new ScrollingMovementMethod());

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!prefs.getString(INPUT_TAG, ERROR_STRING).equals(ERROR_STRING)){
            input.setText(prefs.getString(INPUT_TAG, ERROR_STRING));
        }

        if (!prefs.getString(OUTPUT_TAG, ERROR_STRING).equals(ERROR_STRING)){
            output.setText(prefs.getString(OUTPUT_TAG, ERROR_STRING));
        }

        if (savedInstanceState != null) {
            input.setText(savedInstanceState.getString(INPUT_TAG));
            output.setText(savedInstanceState.getString(OUTPUT_TAG));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INPUT_TAG, input.getText().toString());
        outState.putString(OUTPUT_TAG, output.getText().toString());
    }

    @Override
    protected void onDestroy() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(INPUT_TAG, input.getText().toString()).apply();
        prefs.edit().putString(OUTPUT_TAG, output.getText().toString()).apply();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_copy:
                clipboardManager.setPrimaryClip(ClipData.newPlainText(CLIP_TAG, output.getText().toString()));
                Toast.makeText(this, getString(R.string.clipboard_msg), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_share:
                startActivity(getShareIntent(output.getText().toString()));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static Intent getShareIntent(String outgoingText) {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, outgoingText);
        i.setType("text/plain");
        return i;

    }

    private static String getBinaryString(String input) {

        byte[] inputBytes = input.getBytes();

        StringBuilder builder = new StringBuilder();

        for (byte b : inputBytes) {
            String s = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            builder.append(s);
            builder.append(" ");
        }

        return builder.toString();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        output.setText(getBinaryString(s.toString()));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
