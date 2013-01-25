package jp.visioncraft.filelist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.visioncraft.filelist.R.id;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static File dir;
	List<File> fileArrayList = new ArrayList<File>();
	List<String> fileNameList = new ArrayList<String>();
	
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
		//ArrayAdapter<File> arrayAdapter = new ArrayAdapter<File>(this, R.layout.array_adapter, fileArrayList);
		ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.array_adapter, fileNameList);
		if (parent != null) {
			fileArrayList.add(parent);
			fileNameList.add("..");
		}

		// get file list
		File[] fileList = dir.listFiles();
		if (fileList != null) {
			// show only file name
			for (File f : fileList) {
				fileArrayList.add(f);
				String name = f.getName();
				// add slash if f is directory.
				if (f.isDirectory()) {
					name += "/";
				}
				fileNameList.add(name);
			}
			//fileArrayList.addAll(Arrays.asList(fileList));
		}
		ListView listView = (ListView) findViewById(id.listView1);
		//listView.setAdapter(arrayAdapter);
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
				}
				else if (file.isFile()) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					//String extension = MimeTypeMap.getFileExtensionFromUrl(file.toString());
					Uri uri = Uri.fromFile(file);
					String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
					String mimeType = "";
					if (extension != "") {
						mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase(Locale.US));
						if (mimeType != null) {
							intent.setDataAndType(Uri.fromFile(file), mimeType);
						}
					}
					Toast.makeText(MainActivity.this, mimeType, Toast.LENGTH_SHORT).show();
					// Check the intent whether the Activity be.
					PackageManager pm = getPackageManager();
					//ComponentName componentName = intent.resolveActivity(pm);
					List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
					List<CharSequence> labelList = new ArrayList<CharSequence>();
					for (ResolveInfo ri : resolveInfoList) {
						CharSequence label = ri.loadLabel(pm);
						labelList.add(label);
					}
					if (!resolveInfoList.isEmpty()) {
						Toast.makeText(MainActivity.this, labelList.toString(), Toast.LENGTH_LONG).show();
						startActivity(intent);
					} else {
						Toast.makeText(MainActivity.this, "activity not found", Toast.LENGTH_SHORT).show();
					}
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
