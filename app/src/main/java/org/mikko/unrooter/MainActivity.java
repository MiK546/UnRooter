package org.mikko.unrooter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
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

        mRootedStatus = RootUtil.isDeviceRooted();
        changeStatus(mRootedStatus);
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

    /** Called when the central button is pressed.
     *
     * @param view -
     */
    public void onRootChange(View view){
        String message;

        if(mRootedStatus){
            message = StatusChanger.unRoot();
            if(message.equals("")){
                mRootedStatus = false;
                changeStatus(mRootedStatus);
            }else{
                showSnackbar(message);
            }
        }else{
            message = StatusChanger.root();
            switch (message) {
                case "":
                    mRootedStatus = true;
                    changeStatus(mRootedStatus);
                    break;
                case "?noRoot":
                    changeStatus("noRoot");
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
