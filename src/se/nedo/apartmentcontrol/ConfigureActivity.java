package se.nedo.apartmentcontrol;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import se.nedo.apartmentcontrol.R;
import se.nedo.apartmentcontrol.apartmentcontrol.UpdateService;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class ConfigureActivity extends Activity implements OnClickListener{
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	public static String TAG = "Apartmentcontrol Config";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);

	       requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	       setContentView(R.layout.configure);
	      //mTitle = (EditText)findViewById(R.id.conf_title);
	       
	       ((Button)findViewById(R.id.Button01)).setOnClickListener(this);
	       
	       // Read the appWidgetId to configure from the incoming intent
	       mAppWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
	       setConfigureResult(Activity.RESULT_CANCELED);
	       if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
	    	   finish();
	           return;
	       }
	}
	
	 /**
	 * {@inheritDoc}
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	
	}
	
    /**
     * Convenience method to always include {@link #mAppWidgetId} when setting
     * the result {@link Intent}.
     */
    public void setConfigureResult(int resultCode) {
        final Intent data = new Intent();
        data.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(resultCode, data);
    }

	@Override
	public void onClick(View v) {
		 switch (v.getId()) {
         	case R.id.Button01: {
         		ContentValues values = new ContentValues();
         		values.put("cmdid", ((EditText) findViewById(R.id.EditText01)).getText().toString());
                
                SharedPreferences.Editor editor = getPreferences(0).edit();
                int value = Integer.valueOf(((EditText) findViewById(R.id.EditText01)).getText().toString());
                editor.putInt("cmdid_"+mAppWidgetId, value);
                Log.d(TAG, "Configuration for " + mAppWidgetId + " is set to " + value);
                // Trigger pushing a widget update to surface
//                UpdateService.requestUpdate(new int[] {
//                    mAppWidgetId
//                });
                startService(new Intent(this, UpdateService.class));

                setConfigureResult(Activity.RESULT_OK);
                finish();

                break;
         	}
         }
		 
	}
}