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
import android.util.Log;
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
    private static final String STATE_TAG = "state_tag";

    private static final int BINARY_OUTPUT = 6932;
    private static final int HEX_OUTPUT = BINARY_OUTPUT + 1;

    private int state = BINARY_OUTPUT;

    private EditText input;
    private TextView output;
    private TextView title;

    private ClipboardManager clipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = (EditText) findViewById(R.id.input_et);
        output = (TextView) findViewById(R.id.output_tv);
        title = (TextView) findViewById(R.id.binary_output_title_tv);

        setOutputTitleString(R.string.binary);

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

        state = prefs.getInt(STATE_TAG, BINARY_OUTPUT);

        if (savedInstanceState != null) {
            input.setText(savedInstanceState.getString(INPUT_TAG));
            output.setText(savedInstanceState.getString(OUTPUT_TAG));
            state = savedInstanceState.getInt(STATE_TAG);
            setOutputTitleString((state == HEX_OUTPUT) ? R.string.hex : R.string.binary);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INPUT_TAG, input.getText().toString());
        outState.putString(OUTPUT_TAG, output.getText().toString());
        outState.putInt(STATE_TAG, state);
    }

    @Override
    protected void onPause() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(INPUT_TAG, input.getText().toString()).apply();
        prefs.edit().putString(OUTPUT_TAG, output.getText().toString()).apply();
        prefs.edit().putInt(STATE_TAG, state).apply();
        super.onPause();
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
            case R.id.action_swap_binary:
                setOutputTitleString(R.string.binary);
                state = BINARY_OUTPUT;
                updateOutputText();
                return true;
            case R.id.action_swap_hex:
                setOutputTitleString(R.string.hex);
                state = HEX_OUTPUT;
                updateOutputText();
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

    private static String getHexString(String input) {

        StringBuilder builder = new StringBuilder();

        for (char c : input.toCharArray()) {
            builder.append(Integer.toHexString(c));
            builder.append(" ");
        }

        return builder.toString();
    }

    private void updateOutputText() {

        output.setText(getOutputString(input.getText().toString()));

    }

    private String getOutputString(String input) {

        switch (state) {
            case BINARY_OUTPUT:
                return getBinaryString(input);
            case HEX_OUTPUT:
                return getHexString(input);
            default:
                throw new AssertionError();
        }

    }

    private void setOutputTitleString(int titleId) {
        this.title.setText(getString(R.string.convert_title, getString(titleId)));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        output.setText(getOutputString(s.toString()));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
