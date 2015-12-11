package control;

import android.app.Application;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

/**
 * Created by David on 12/7/2015.
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private boolean startup;

    @Override
    public void onCreate() {
        super.onCreate();
        startup = false;
        Log.i(TAG, "Application Created");
    }

    public boolean isStartup() {
        return startup;
    }

    public void setStartup(boolean startup) {
        this.startup = startup;
    }

    public Information getInformation() {
        return Information.getInstance();
    }
}
