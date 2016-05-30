package com.werdpressed.partisan.texttobinary;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String CLIP_TAG = "Binary String";

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

        Button convertBtn = (Button) findViewById(R.id.button);
        if (convertBtn != null) {
            convertBtn.setOnClickListener(this);
        }

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

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

    @Override
    public void onClick(View v) {

        byte[] inputBytes = null;

        try {
            inputBytes = input.getText().toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuilder builder = new StringBuilder();

        for (byte b : inputBytes) {
            String s = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            builder.append(s);
            builder.append(" ");
        }

        output.setText(builder.toString());

    }

    private static Intent getShareIntent(String outgoingText) {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, outgoingText);
        i.setType("text/plain");
        return i;

    }

}
