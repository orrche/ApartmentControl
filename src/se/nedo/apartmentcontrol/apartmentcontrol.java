package se.nedo.apartmentcontrol;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import se.nedo.apartmentcontrol.R;
import android.app.PendingIntent;
import android.app.Service;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


import java.util.*;

@SuppressWarnings("unused")
public class apartmentcontrol extends AppWidgetProvider {
	public static String ACTION_WIDGET_CONFIGURE = "ConfigureWidget";
	public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
	public static String TAG = "Apartmentcontrol Service";
	
	public apartmentcontrol()
	{

	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.i(TAG, "Start start start...");
    	
		for ( int id : appWidgetIds) {
			Intent intent = new Intent(context, UpdateService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
			Log.i(TAG, "Starting service with id: " + id);
			context.startService(intent);
		}
	}
	
	@Override
    public void onReceive(Context context, Intent intent ) {
            Log.d(TAG, "Receive intent= " + intent );
            int mAppWidgetId = intent.getIntExtra("ID", -1);
        	if ( mAppWidgetId == -1 )
            {
        		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                	
            	Log.d(TAG, "Fallback for the ID");
            
            }
            String msg = intent.getStringExtra("msg");
            if ( msg != null )
            	Log.d(TAG, "The intent had the message " + msg);
        	Log.d(TAG, "The intent had id " + mAppWidgetId);
        	
            Intent serviceIntent = new Intent(context,apartmentcontrol.UpdateService.class);
            if( intent.getAction() != null ) {
                    serviceIntent.setAction(intent.getAction());
            }
            
            if( intent.getExtras() != null ) {
                    serviceIntent.putExtras(intent.getExtras());
            }
            context.startService(serviceIntent);
            
            super.onReceive(context, intent);
    }
	
	public static class UpdateService extends Service {
		int counter = 0;
		Map<Integer, Boolean> switchState = new HashMap<Integer, Boolean>();
		String sUserAgent = null;
		
		public UpdateService()
		{
//			Log.i(TAG, "Class created");
//    		
//			
			
		}
		
        @Override
        public void onStart(Intent intent, int startId) {
        	super.onStart(intent, startId);
        	
        	int mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        	if ( mAppWidgetId == -1 )
        	{
        		Log.d(TAG, "No ID came along");
        		return;
        	}
        	
        	if ( sUserAgent == null ) {
	        	try {
					PackageManager manager = this.getPackageManager();
		            PackageInfo info;
					
					info = manager.getPackageInfo(this.getPackageName(), 0);
		            sUserAgent = String.format(this.getString(R.string.template_user_agent),
		                    info.packageName, info.versionName);
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
        	}
        	
    		

		    final String action = intent.getAction();
            Log.i(TAG, "Action " + action + " == " + ACTION_WIDGET_RECEIVER);
    		if ( ACTION_WIDGET_RECEIVER.equals(action) )
    		{
    			Log.i(TAG, "We have id: " + mAppWidgetId);
    			counter++;
            	makeAction(mAppWidgetId);
    		}
    		
        	RemoteViews updateViews = buildUpdate(this,mAppWidgetId);
        	
        	// Push update for this widget to the home screen
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(mAppWidgetId, updateViews);
            
        }

        public void makeAction(int mAppWidgetId) {
        	SharedPreferences pref;
        	pref = PreferenceManager.getDefaultSharedPreferences(this);
        	
            int id = pref.getInt("cmdid_"+mAppWidgetId, -1);
            if ( id == -1 )
            {
            	Log.e(TAG, "preference for " + mAppWidgetId + " not set");
            	if ( mAppWidgetId == 13 ) {
            		Log.d(TAG, "But now we are setting hte preferenc right here should stick then");
	            	SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit(); 
	                editor.putInt("cmdid_"+mAppWidgetId, 3);
	                
	                editor.commit();
            	}
            	return;
            }
            
        	if ( isOn(id) )
        	{
        		switchState.put(id, false);
    			getXML("http://minoris.se/www/cmd.psp?id="+id+"&cmd=off");
        	}
    		else
    		{
        		switchState.put(id, true);
    			getXML("http://minoris.se/www/cmd.psp?id="+id+"&cmd=on");
    		}
        }
        
		public void getXML(String url)
        {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            request.setHeader("User-Agent", sUserAgent);

            try {
                client.execute(request);
            }
            catch ( Exception e) {
            }
        	
        	
        }
        public boolean isOn(int id)
        {
        	Boolean state = switchState.get(id);
        	if ( state != null )
        		return state;
        	return true;
        }
        
        public RemoteViews buildUpdate(Context context, int mAppWidgetId)
        {
        	Log.d(TAG, "The build is for app " + mAppWidgetId);
    		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);

        	SharedPreferences pref;
        	pref = PreferenceManager.getDefaultSharedPreferences(this);
    		// Checking if the app is on or off
    		if ( isOn(pref.getInt("cmdid_"+mAppWidgetId, -1)) )
    			remoteViews.setImageViewResource(R.id.ImageView01, R.drawable.fan);
    		else
    			remoteViews.setImageViewResource(R.id.ImageView01, R.drawable.fan_off);
    		
    		// Number beneth the item to identify what ID it has
    		remoteViews.setTextViewText(R.id.TextView01, "" + mAppWidgetId);
    		
    		// TODO: The intent that doesn't work
    		Intent active = new Intent(context, apartmentcontrol.class);
    		active.setAction(ACTION_WIDGET_RECEIVER);
    		active.setType(mAppWidgetId + "");
    		active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
    		active.putExtra("msg", "Btn:" + mAppWidgetId);
    		
			PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, PendingIntent.FLAG_UPDATE_CURRENT);
			
    		remoteViews.setOnClickPendingIntent(R.id.ImageView01, actionPendingIntent);
    		
    		return remoteViews;
        }
        
		@Override
		public IBinder onBind(Intent intent) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}