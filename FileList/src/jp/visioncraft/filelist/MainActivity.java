package jp.visioncraft.filelist;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.visioncraft.filelist.R.id;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
//import android.view.Menu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static File dir;
	List<File> fileArrayList = new ArrayList<File>();
	List<Map<String, String>> fileNameList = new ArrayList<Map<String, String>>();
	private final static String MY_PREFS = "FileList";
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide title bar. -> styles.xml
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		if (dir == null || !dir.isDirectory()) {
			// Get the saved directory.
			SharedPreferences mySharedpreferences = getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE);
			String lastDir = mySharedpreferences.getString("dir", null);
			if (lastDir != null) {
				dir = new File(lastDir);
			} else {
				// Gets the Android external storage directory (such as SD card).
				String state = Environment.getExternalStorageState();
				if (Environment.MEDIA_MOUNTED.equals(state)) {
					dir = Environment.getExternalStorageDirectory();
				}
			}
		}

		// If dir is not exists, set "/".
		if (!dir.exists()) {
			dir = new File("/");
		}
		
		// Save current directory to SharedPreferences.
		SharedPreferences mySharedPreferences = getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("dir", dir.toString());
		editor.commit();

		TextView textView = (TextView) findViewById(id.textView1);
		textView.setText(dir.toString());
		
		// get parent, if dir is not root("/")
		File parent = dir.getParentFile();
		//ArrayAdapter<File> arrayAdapter = new ArrayAdapter<File>(this, R.layout.array_adapter, fileArrayList);
		//ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.array_adapter, fileNameList);
		if (parent != null) {
			fileArrayList.add(parent);
			Map<String, String> map = new HashMap<String, String>();
			map.put("main", "..");
			map.put("sub", "");
			fileNameList.add(map);
		}

		// Get the list of files.
		File[] listFiles = dir.listFiles();
		
		if (listFiles != null) {
			// Sort listFiles.
			List<File> temp = Arrays.asList(listFiles);
			Collections.sort(temp);
			listFiles = (File[]) temp.toArray();

			// show only file name
			for (File f : listFiles) {
				fileArrayList.add(f);
				String name = f.getName();
				// add slash if f is directory.
				if (f.isDirectory()) {
					name += "/";
				}
				Date date = new Date(f.lastModified());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
				
				Map<String, String> map = new HashMap<String, String>();
				map.put("main", name);
				map.put("sub", sdf.format(date));
				fileNameList.add(map);
			}
			//fileArrayList.addAll(Arrays.asList(fileList));
		}

		ListView listView = (ListView) findViewById(id.listView1);
		//listView.setAdapter(arrayAdapter);
		SimpleAdapter adapter = new SimpleAdapter(this, fileNameList, R.layout.simple_list_item_2,
				new String[]{"main", "sub"}, new int[]{R.id.text1, R.id.text2});
		listView.setAdapter(adapter);
		
		// listen click
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//File file = (File) arg0.getItemAtPosition(arg2);
				File file = fileArrayList.get(arg2);
				//Toast.makeText(MainActivity.this, file.toString(), Toast.LENGTH_SHORT).show();
				if (file.isDirectory()) {
					MainActivity.dir = file;
					MainActivity.this.finish();
					Intent intent = new Intent(MainActivity.this, MainActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(MainActivity.this, FileInfoActivity.class);
					intent.putExtra("file", file.toString());
					startActivity(intent);
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		// note: API level 5(Android 2.0)
		File parent = dir.getParentFile();
		if (parent != null) {
			dir = parent;
			finish();
			Intent intent = new Intent(MainActivity.this, MainActivity.class);
			startActivity(intent);
		} else {
			dir = null;
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.terminate_app:
			dir = null;
			super.onBackPressed();
			return true;
		case R.id.shortcut:
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle("Shortcut")
			.setItems(getUsefulDirectory(), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String[] dirs = getUsefulDirectory();
					MainActivity.dir = new File(dirs[which]);
					MainActivity.this.finish();
					Intent intent = new Intent(MainActivity.this, MainActivity.class);
					startActivity(intent);
					
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, 0);
	}

	public String[] getUsefulDirectory() {
		List<File> dirs = new ArrayList<File>();
		dirs.add(Environment.getDataDirectory());
		dirs.add(Environment.getDownloadCacheDirectory());
		dirs.add(Environment.getExternalStorageDirectory());
		dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS));
		dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
		dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
		dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
		dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
		dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS));
		dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
		dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS));
		dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES));
		dirs.add(Environment.getRootDirectory());
		//
		//dirs.add(this.getCacheDir());
		//dirs.add(this.getExternalCacheDir());
		//dirs.add(this.getFilesDir());
		//dirs.add(this.getObbDir()); // API level 11
		
		List<String> strs = new ArrayList<String>();
		for (File f : dirs) {
			strs.add(f.toString());
		}
		return strs.toArray(new String[0]);
	}
}
