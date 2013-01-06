package jp.visioncraft.filelist;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import jp.visioncraft.filelist.R.id;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static File dir;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// get directory, if dir is not directory
		if (dir == null) {
			dir = Environment.getExternalStorageDirectory();
		} else {
			if (!dir.isDirectory()) {
				dir = Environment.getExternalStorageDirectory();
			}
		}

		// directory exists?
		if (!dir.exists()) {
			if (dir.mkdir()) {
				Toast.makeText(this, dir.toString() + " ÇçÏê¨ÇµÇ‹ÇµÇΩÅB", Toast.LENGTH_LONG).show();
			}
		}
		TextView textView = (TextView) findViewById(id.textView1);
		textView.setText(dir.toString());
		
		// get parent, if dir is not root("/")
		File parent = dir.getParentFile();
		ArrayList<File> fileArrayList = new ArrayList<File>();
		ArrayAdapter<File> arrayAdapter = new ArrayAdapter<File>(this, R.layout.array_adapter, fileArrayList);
		if (parent != null) {
			fileArrayList.add(parent);
		}

		// get file list
		File[] fileList = dir.listFiles();
		if (fileList != null) {
			fileArrayList.addAll(Arrays.asList(fileList));
		}
		ListView listView = (ListView) findViewById(id.listView1);
		listView.setAdapter(arrayAdapter);
		
		// listen click
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				File file = (File) arg0.getItemAtPosition(arg2);
				Toast.makeText(MainActivity.this, file.toString(), Toast.LENGTH_SHORT).show();
				if (file.isDirectory()) {
					MainActivity.dir = file;
					MainActivity.this.finish();
					Intent intent = new Intent(MainActivity.this, MainActivity.class);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
