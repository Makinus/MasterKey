package com.makinus.masterkey;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.makinus.masterkey.db.service.impl.DatabaseHandler;
import com.makinus.masterkey.utils.MasterKeyUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ListView;

public class BaseActivity extends AppCompatActivity{

	public static final int REQ_CODE = 1; 
	public static final String YES = "Y";
	public static final String NO = "N";

	final Context context = this;
	ListView cardsListView;
	protected LazyAdapter lazyAdapter;
	protected DatabaseHandler dbService; 
	protected MasterKeyUtils mKeyUtil;
	
	public String remindDaysMsg = "(Remind me in %n% days to change the password.)";
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		dbService = DatabaseHandler.getInstance(context);
		mKeyUtil = MasterKeyUtils.getInstance(context);
	}

	@SuppressLint("TrulyRandom")
	public String generateResetCode() {
		    return new BigInteger(130, new SecureRandom()).toString(32);
	}
	
	public DatabaseHandler getDbInstance() {
		return DatabaseHandler.getInstance(context); 
	}
	
	public String getTodaysDate() {
		return DateTimeFormat.forPattern("dd/MM/yyyy").print(DateTime.now());
	}
	
	public boolean isNotifyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

}