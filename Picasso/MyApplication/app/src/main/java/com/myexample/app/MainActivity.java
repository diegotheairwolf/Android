package com.myexample.app;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.content.Intent;
import android.widget.EditText;
import android.content.Context;
import android.content.SharedPreferences;
import java.io.FileOutputStream;

public class MainActivity extends ActionBarActivity {

    public final static String extra_echo_edit_text = "com.myexample.app.echo_edit_text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void echoEditText(View view ) {
        Intent intent = new Intent(this, Echo.class);
        EditText editText = (EditText) findViewById(R.id.edit_text_on_main);
        String message = editText.getText().toString();
        intent.putExtra(MainActivity.extra_echo_edit_text, message);
        startActivity(intent);
    }

    public void saveTextView2(View view) {
        EditText editText = (EditText) findViewById(R.id.textView2);
        String str = editText.getText().toString();

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.preference_textview2), str);
        editor.commit();
    }

    public void button3Save(View view) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);


            /*
            this is an example of using shared preferences
            known as Key-Vaule sets in
            http://developer.android.com/training/basics/data-storage/index.html
             */
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            String str = sharedPref.getString(getString(R.string.preference_textview2), "R.string.preference_textview2 not found");

            EditText editText = (EditText) rootView.findViewById(R.id.textView2);
            editText.setText(str);

            /*
            example of saving file system-esque
            Internal storage
            */
            String filename = "myfile";
            String string = "Hello world!";
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(string.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


            return rootView;
        }


    }

}
