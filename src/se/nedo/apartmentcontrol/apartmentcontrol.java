package se.nedo.apartmentcontrol;

import se.nedo.apartmentcontrol.R;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Bundle;
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

public class apartmentcontrol extends AppWidgetProvider {
	public static String ACTION_WIDGET_CONFIGURE = "ConfigureWidget";
	public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
	
	public apartmentcontrol()
	{

	}


	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Toast.makeText(context, "Resetted" + appWidgetIds[0], Toast.LENGTH_LONG).show();
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
		Intent configIntent = new Intent(context, ClickOneActivity.class);
		configIntent.setAction(ACTION_WIDGET_CONFIGURE);
		Intent active = new Intent(context, apartmentcontrol.class);
		active.setAction(ACTION_WIDGET_RECEIVER);
		active.putExtra("msg", "Btn:");
		active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID , appWidgetIds[0]);
		
		PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
		remoteViews.setOnClickPendingIntent(R.id.ImageView01, actionPendingIntent);
		//remoteViews.setOnClickPendingIntent(R.id.button_two, configPendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
	}
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		try
		{
			final String action = intent.getAction();

			if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
				final int appWidgetId = intent.getExtras().getInt( AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
				
				Toast.makeText(context, "ouch gets deleted this way " + appWidgetId, Toast.LENGTH_SHORT).show();
				if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
					this.onDeleted(context, new int[] { appWidgetId });
				}
			}
			if (intent.getAction().equals(ACTION_WIDGET_RECEIVER)) {
				final int appWidgetId = intent.getIntExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
				String whatever = intent.getStringExtra("msg");
				if ( whatever == null ) whatever = "null";
				
				Toast.makeText(context, whatever + appWidgetId + " " + counter, Toast.LENGTH_SHORT).show();
				
				RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.main);
				
				if ( counter % 2 == 0 ) {
					updateViews.setImageViewResource(R.id.ImageView01, R.drawable.fan_off);
				}
				else {
					updateViews.setImageViewResource(R.id.ImageView01, R.drawable.fan);
				}
					
				// Push update for this widget to the home screen
				ComponentName thisWidget = new ComponentName(context, apartmentcontrol.class);
				AppWidgetManager manager = AppWidgetManager.getInstance(context);
				manager.updateAppWidget(thisWidget, updateViews);
				
//				Intent widgetUpdate = new Intent();
//				widgetUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//				widgetUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,  new int[] { appWidgetId });
//				
//				PendingIntent newPending = PendingIntent.getBroadcast(context, 0, widgetUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
//				newPending.send();
			}
		}
		catch (Exception e)
		{
			Toast.makeText(context, "God damit", Toast.LENGTH_SHORT).show();
		
		}

		super.onReceive(context, intent);
	}
}