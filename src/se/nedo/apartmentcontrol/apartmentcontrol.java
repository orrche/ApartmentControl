package se.nedo.apartmentcontrol;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import se.nedo.apartmentcontrol.R;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class apartmentcontrol extends AppWidgetProvider {
	public static String ACTION_WIDGET_CONFIGURE = "ConfigureWidget";
	public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
	
	public apartmentcontrol()
	{

	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.i("Apartmentcontrol Service", "Start start start...");
    	
		context.startService(new Intent(context, UpdateService.class));
	}
	
	@Override
    public void onReceive(Context context, Intent intent ) {
            Log.d("Apartmentcontrol Service", "Receive intent= " + intent );
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
		String sUserAgent = null;
		
		public UpdateService()
		{
//			Log.i("Apartmentcontrol Service", "Class created");
//    		
//			
			
		}
		
        @Override
        public void onStart(Intent intent, int startId) {
        	if ( sUserAgent == null ) {
	        	try {
					PackageManager manager = this.getPackageManager();
		            PackageInfo info;
					
					info = manager.getPackageInfo(this.getPackageName(), 0);
		            sUserAgent = String.format(this.getString(R.string.template_user_agent),
		                    info.packageName, info.versionName);
				} catch (Exception e) {
					Log.e("Apartmentcontrol Service", e.toString());
				}
        	}
        	
    		

            final String action = intent.getAction();
            Log.i("Apartmentcontrol Service", "Action " + action + " == " + ACTION_WIDGET_RECEIVER);
    		if ( ACTION_WIDGET_RECEIVER.equals(action) )
    		{
    			counter++;
            	makeAction();
    		}
    		
        	RemoteViews updateViews = buildUpdate(this);
        	
        	// Push update for this widget to the home screen
            ComponentName thisWidget = new ComponentName(this, apartmentcontrol.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(thisWidget, updateViews);
            
        }

        public void makeAction() {
        	
        	if ( isOn() )
    			getXML("http://jkg.for-logic.com/www/cmd.psp?id=3&cmd=on");
    		else
    			getXML("http://jkg.for-logic.com/www/cmd.psp?id=3&cmd=off");    		
        }
        
        public void getXML(String url)
        {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            request.setHeader("User-Agent", sUserAgent);

            try {
                HttpResponse response = client.execute(request);
                
            }
            catch ( Exception e) {
            }
        	
        	
        }
        public boolean isOn()
        {
        	return counter % 2 == 0;
        }
        
        public RemoteViews buildUpdate(Context context)
        {
        	// This really doesn't make much sense to me to have here tho.
    		Toast.makeText(context, "Resetted", Toast.LENGTH_LONG).show();
    		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
    		Intent configIntent = new Intent(context, ClickOneActivity.class);
    		configIntent.setAction(ACTION_WIDGET_CONFIGURE);
    		Intent active = new Intent(context, apartmentcontrol.class);
    		active.setAction(ACTION_WIDGET_RECEIVER);
    		active.putExtra("msg", "Btn:");
    		//active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID , appWidgetIds[0]);
    		

    		
    		if ( isOn() )
    			remoteViews.setImageViewResource(R.id.ImageView01, R.drawable.fan);
    		else
    			remoteViews.setImageViewResource(R.id.ImageView01, R.drawable.fan_off);
    		
			PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
    		remoteViews.setOnClickPendingIntent(R.id.ImageView01, actionPendingIntent);
    		
    		//appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    		return remoteViews;
        	
        }
        
		@Override
		public IBinder onBind(Intent intent) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}