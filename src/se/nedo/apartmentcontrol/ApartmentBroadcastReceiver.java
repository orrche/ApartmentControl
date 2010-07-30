package se.nedo.apartmentcontrol;


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
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class ApartmentBroadcastReceiver extends BroadcastReceiver{     
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
                
                //super.onReceive(context, intent);
        }
}