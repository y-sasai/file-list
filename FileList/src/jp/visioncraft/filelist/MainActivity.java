package jp.visioncraft.filelist;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.visioncraft.filelist.R.id;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
//import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ads.*;

public class MainActivity extends Activity {
	private AdView adView;

	public static File dir;
	List<File> fileArrayList = new ArrayList<File>();
	List<Map<String, String>> fileNameList = new ArrayList<Map<String, String>>();
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide title bar.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		// get directory, if dir is not directory
		if (dir == null || !dir.isDirectory()) {
			dir = Environment.getExternalStorageDirectory();
		}

		// directory exists?
		if (!dir.exists()) {
			if (dir.mkdir()) {
				Toast.makeText(this, dir.toString() + " was created.", Toast.LENGTH_LONG).show();
			}
		}
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
				}
				else if (file.isFile()) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					//String extension = MimeTypeMap.getFileExtensionFromUrl(file.toString());
					Uri uri = Uri.fromFile(file);
					String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
					if (extension != "") {
						String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase(Locale.US));
						if (mimeType != null) {
							Toast.makeText(MainActivity.this, mimeType, Toast.LENGTH_LONG).show();
							intent.setDataAndType(Uri.fromFile(file), mimeType);
						} else {
							Toast.makeText(MainActivity.this, "Could not find MIME type.", Toast.LENGTH_LONG).show();
							return;
						}
					} else {
						Toast.makeText(MainActivity.this, "Could not find MIME type.", Toast.LENGTH_LONG).show();
						return;
					}
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
						//Toast.makeText(MainActivity.this, labelList.toString(), Toast.LENGTH_LONG).show();
						startActivity(intent);
					} else {
						Toast.makeText(MainActivity.this, "Could not open the file.", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		
		// Create the adView
		adView = new AdView(this, AdSize.BANNER, "a1510748cc95c95");
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		adView.setLayoutParams(layoutParams);
		
		// Lookup your RelativeLayout assuming it's been given
		// the attribute android:id="@+id/mainLayout"
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.mainLayout);
		
		// Add the adView to it
		layout.addView(adView);
		
		// Initiate a generic request to load it with an ad
		adView.loadAd(new AdRequest());
	}

	@Override
	protected void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// note: API level 5(Android 2.0)
		File parent = dir.getParentFile();
		if (parent != null) {
			MainActivity.dir = parent;
			MainActivity.this.finish();
			Intent intent = new Intent(MainActivity.this, MainActivity.class);
			startActivity(intent);
		} else {
			super.onBackPressed();
		}
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_main, menu);
//		return true;
//	}

}
