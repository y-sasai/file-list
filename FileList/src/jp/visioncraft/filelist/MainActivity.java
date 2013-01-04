package jp.visioncraft.filelist;

import java.io.File;

import jp.visioncraft.filelist.R.id;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// get movies directory
		File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
		TextView textView = (TextView) findViewById(id.textView1);
		textView.setText(moviesDir.toString());
		
		// movies directory exists?
		if (!moviesDir.exists()) {
			if (moviesDir.mkdir()) {
				Toast.makeText(this, moviesDir.toString() + " ÇçÏê¨ÇµÇ‹ÇµÇΩÅB", Toast.LENGTH_LONG).show();
			}
		}
		
		// get file list
		String[] fileList = moviesDir.list();
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.array_adapter, fileList);
		ListView listView = (ListView) findViewById(id.listView1);
		listView.setAdapter(arrayAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
