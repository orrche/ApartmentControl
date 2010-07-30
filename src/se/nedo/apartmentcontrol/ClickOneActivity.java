package se.nedo.apartmentcontrol;

import android.app.Activity;
import android.widget.Toast;

import se.nedo.apartmentcontrol.R;
import android.os.Bundle;


public class ClickOneActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// change to our configure view
		//setContentView(R.layout.configure);
		// don't call 'this', use 'getApplicationContext()', the activity-object is
		// bigger than just the context because the activity also stores the UI elemtents
		Toast.makeText(getApplicationContext(), "We are in ClickOneActivity",
		Toast.LENGTH_SHORT).show();
	}
}
