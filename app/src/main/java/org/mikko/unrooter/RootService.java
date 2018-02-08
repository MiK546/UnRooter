package org.mikko.unrooter;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;


/**
 * An {@link IntentService} to handle action on the unrooted status notification.
 */
public class RootService extends IntentService {
    /** Action to root the device. */
    public static final String ACTION_ROOT = "org.mikko.unrooter.action.ROOT";
    /** Action to close the notification. */
    public static final String ACTION_CLOSE = "org.mikko.unrooter.action.CLOSE";

    public RootService() {
        super("RootService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if(action.equals(ACTION_ROOT)){
                rootDevice();
            }else if(action.equals(ACTION_CLOSE)){
                closeNotification();
            }
        }
    }

    /**
     * Roots the device and closes the unrooted status notification if successful.
     */
    private void rootDevice(){
        // only execute if device is not rooted
        if(!RootUtil.isDeviceRooted()){
            String result = StatusChanger.root(this);
            if(result.equals("")){
                // if the rooting succeeds remove the notification
                closeNotification();
            }
        }else{
            closeNotification();
        }
    }

    /**
     * Closes the unrooted status notification.
     */
    private void closeNotification(){
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MainActivity.NOTIFICATION_ID_UNROOTED);
    }
}
