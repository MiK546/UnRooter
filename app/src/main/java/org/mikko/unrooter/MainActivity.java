package org.mikko.unrooter;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/** Main activity of the application showing a central button to root and unroot the device.
 */
public class MainActivity extends AppCompatActivity {
    /** Notification id for the unrooted reminder. */
    public static final int NOTIFICATION_ID_UNROOTED = 1;
    /** Notification channel for displaying the unrooted reminder. */
    private static final String NOTIFICATION_CHANNEL_UNROOTED = "org.mikko.unrooter.channel.unrooted";

    TextView mStatusTextView;
    ImageView mStatusImageView;
    TextView mHintTextView;
    ConstraintLayout mButtonLayout;
    boolean mRootedStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatusTextView = findViewById(R.id.statusTextView);
        mStatusImageView = findViewById(R.id.statusImageView);
        mHintTextView = findViewById(R.id.hintTextView);
        mButtonLayout = findViewById(R.id.buttonLayout);
    }

    @Override
    public void onResume() {
        // check root status on onResume() to ensure it is correct
        setRootedStatus();
        if(!mRootedStatus){
            createNotification();
        }

        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // Detects if the user has rooted the device from the notification by checking root status
        // once the activity gains focus (the notification drawer is pulled up).
        if (hasFocus) {
            setRootedStatus();
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_licenses:
                Intent licensesIntent = new Intent(this, LicensesActivity.class);
                startActivity(licensesIntent);
                break;
        }

        return true;
    }

    /**
     * Changes the activity's status based on if the device is rooted or not and sets the value of
     * {@link #mRootedStatus}.
     */
    private void setRootedStatus(){
        mRootedStatus = RootUtil.isDeviceRooted();
        changeStatus(mRootedStatus);
    }

    /** Called when the central button is pressed.
     *
     * @param view -
     */
    public void onRootChange(View view){
        String message;

        if(mRootedStatus){
            message = StatusChanger.unRoot(this);
            if(message.equals("")){
                mRootedStatus = false;
                changeStatus(mRootedStatus);
                createNotification();
            }else{
                showSnackbar(message);
            }
        }else{
            message = StatusChanger.root(this);
            switch (message) {
                case "":
                    mRootedStatus = true;
                    changeStatus(mRootedStatus);
                    removeNotification();
                    break;
                case "?noRoot":
                    changeStatus("noRoot");
                    removeNotification();
                    break;
                default:
                    showSnackbar(message);
                    break;
            }
        }
    }

    /** Shows a snackbar at the bottom of the view.
     *
     * @param message The message to be shown in the snackbar.
     */
    private void showSnackbar(String message){
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    /**
     * Create an notification to tell the user their device is temporarily unrooted.
     */
    private void createNotification(){
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent clickIntent = new Intent(this, MainActivity.class);
        PendingIntent clickPendingIntent = PendingIntent.getActivity(
                        this,
                        0,
                        clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Intent rootIntent = new Intent(this, RootService.class);
        rootIntent.setAction(RootService.ACTION_ROOT);
        PendingIntent rootPendingIntent = PendingIntent.getService(this, 0, rootIntent, 0);

        Intent closeIntent = new Intent(this, RootService.class);
        closeIntent.setAction(RootService.ACTION_CLOSE);
        PendingIntent closePendingIntent = PendingIntent.getService(this, 0, closeIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_UNROOTED)
                        .setContentTitle(getString(R.string.notification_title))
                        .setTicker(getString(R.string.notification_title))
                        .setContentText(getString(R.string.notification_message))
                        .setSmallIcon(R.drawable.ic_unrooter_notification_icon)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(getString(R.string.notification_message)))
                        .setOngoing(true)
                        .setContentIntent(clickPendingIntent)
                        .addAction(R.drawable.ic_lock, getString(R.string.notification_action_root), rootPendingIntent)
                        .addAction(R.drawable.ic_close_black_24dp, getString(R.string.notification_action_close), closePendingIntent);

        notificationManager.notify(NOTIFICATION_ID_UNROOTED, notificationBuilder.build());
    }

    /**
     * Remove the unrooted notification.
     */
    private void removeNotification(){
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(NOTIFICATION_ID_UNROOTED);
    }

    /** Change the state of the central button.
     *
     * @param bStatus True if the device is currently rooted, false otherwise.
     */
    private void changeStatus(boolean bStatus){
        if(bStatus){
            changeStatus("rooted");
        }else{
            changeStatus("unrooted");
        }
    }

    /** Change the state of the central button.
     *
     * @param status "rooted", "unrooted" or "noRoot" The status that the button is set to.
     */
    private void changeStatus(String status){
        switch (status){
            case "rooted":
                mStatusTextView.setText(getString(R.string.status_rooted));
                mStatusImageView.setImageResource(R.drawable.ic_check_black_24dp);
                mHintTextView.setText(getString(R.string.hint_rooted));
                mButtonLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRooted)));
                break;
            case "unrooted":
                mStatusTextView.setText(getString(R.string.status_unrooted));
                mStatusImageView.setImageResource(R.drawable.ic_do_not_disturb_black_24dp);
                mHintTextView.setText(getString(R.string.hint_unrooted));
                mButtonLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorUnrooted)));
                break;
            case "noRoot":
                mStatusTextView.setText(getString(R.string.status_not_rooted));
                mStatusImageView.setImageResource(R.drawable.ic_error_outline_black_24dp);
                mHintTextView.setText(getString(R.string.hint_not_rooted));
                mButtonLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorNotRooted)));
                break;
        }
    }
}
