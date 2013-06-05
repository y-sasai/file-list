package jp.visioncraft.filelist;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class FileInfoActivity extends Activity {

	File file;
	String mimeType;
	List<ResolveInfo> resolveInfo;
	private AdView adView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.file_info);
	    
	    // Get selected File object.
	    Intent intentThisActivity = getIntent();
	    file = new File(intentThisActivity.getStringExtra("file"));

	    TextView fileName = (TextView) findViewById(R.id.fileName);
	    fileName.setText(file.getName());
	    
	    // Get MIME type.
//	    String extension = MimeTypeMap.getFileExtensionFromUrl(file.toURI().toString());
//		if (extension != "") {
//			mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase(Locale.US));
//		}
		mimeType = URLConnection.guessContentTypeFromName(file.toURI().toString());
		
		TextView mimeTypeTextView = (TextView) findViewById(R.id.mimeType);
		if (mimeType != null) {
			mimeTypeTextView.setText(mimeType);
		} else {
			mimeTypeTextView.setText("(none)");
		}
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		if (mimeType != null) {
			intent.setDataAndType(Uri.fromFile(file), mimeType);
		} else {
			intent.setData(Uri.fromFile(file));
		}

		PackageManager pm = getPackageManager();
		resolveInfo = pm.queryIntentActivities(intent, 0);
		List<CharSequence> labelList = new ArrayList<CharSequence>();
		for (ResolveInfo ri : resolveInfo) {
			CharSequence label = ri.loadLabel(pm);
			labelList.add(label);
		}
		
		Button openButton = (Button) findViewById(R.id.buttonOpen);
		
		if (!resolveInfo.isEmpty()) {
			ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
					this, android.R.layout.simple_spinner_item, labelList);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			Spinner spinner = (Spinner) findViewById(R.id.spinner1);
			spinner.setAdapter(adapter);
			spinner.setVisibility(View.VISIBLE);
			openButton.setVisibility(View.VISIBLE);
		}

		String info = new String();
	    info += "canRead: " + file.canRead() + "\n";
	    info += "canWrite: " + file.canWrite() + "\n";
	    info += "exists: " + file.exists() + "\n";
	    info += "getAbsolutePath: " + file.getAbsolutePath() + "\n";
	    try {
			info += "getCanonicalPath: " + file.getCanonicalPath() + "\n";
		} catch (IOException e) {
			e.printStackTrace();
		}
	    info += "getName: " + file.getName() + "\n";
	    info += "getParent: " + file.getParent() + "\n";
	    info += "getPath: " + file.getPath() + "\n";
	    info += "hashCode: " + Integer.toHexString(file.hashCode()) + "\n";
	    info += "isAbsolute: " + file.isAbsolute() + "\n";
	    info += "isDirectory: " + file.isDirectory() + "\n";
	    info += "isFile: " + file.isFile() + "\n";
	    info += "isHidden: " + file.isHidden() + "\n";
	    info += "lastModified: " + (new Date(file.lastModified())).toString() + "\n";
	    info += "length: " + String.format("%,3d", file.length()) + " byte\n";
	    info += "toString: " + file.toString() + "\n";
	    info += "toURI: " + file.toURI() + "\n";

	    TextView textView1 = (TextView) findViewById(R.id.textView1);
	    textView1.setText(info);
	    // クリックしたときに文字が灰色になるので、ScrollViewに変更した。
	    //textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
	    
	    createAdView();
	}

	public void openFile(View view) {
		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		int position = spinner.getSelectedItemPosition();
		if (position == AdapterView.INVALID_POSITION) {
			position = 0;
		}
		ResolveInfo info = resolveInfo.get(position);

		Intent intent = new Intent(Intent.ACTION_VIEW);
		if (mimeType != null) {
			intent.setDataAndType(Uri.fromFile(file), mimeType);
		} else {
			intent.setData(Uri.fromFile(file));
		}
		intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
		startActivity(intent);
	}
	
	public void createAdView() {
		// Create the adView
		adView = new AdView(this, AdSize.BANNER, "a1510748cc95c95");
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		adView.setLayoutParams(layoutParams);
		
		// Lookup your RelativeLayout assuming it's been given
		// the attribute android:id="@+id/mainLayout"
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.fileInfoLayout);
		
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

}
