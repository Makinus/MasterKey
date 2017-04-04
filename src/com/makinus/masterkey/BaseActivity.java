package com.makinus.masterkey;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.makinus.db.service.impl.DatabaseHandler;
import com.makinus.masterkey.utils.MasterKeyUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends Activity{

	public static final int REQ_CODE = 1; 
	public static final String YES = "Y";
	public static final String NO = "N";
	
	final Context context = this;
	protected DatabaseHandler dbService; 
	protected MasterKeyUtils mKeyUtil;
	
	public String remindDaysMsg = "(Remind me in %n% days to change the password.)";
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		dbService = DatabaseHandler.getInstance(context);
		mKeyUtil = MasterKeyUtils.getInstance(context);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;
		switch (item.getItemId()) {
        	case R.id.mi_resetPwd:
	        	i = new Intent(context, ResetMasterPwdActivity.class);
				startActivityForResult(i, REQ_CODE);
		        return true;
	        case R.id.mi_changePwd:
	        	i = new Intent(context, ChangeMasterPwdActivity.class);
				startActivityForResult(i, REQ_CODE);
		        return true;
	        case R.id.mi_generateCode:    
	        	i = new Intent(context, GenerateResetCodeActivity.class);
				startActivityForResult(i, REQ_CODE);
		        return true;
	        case R.id.mi_about:
	        	startActivityForResult(new Intent(this, MenuHandlerActivity.class).putExtra("menu", "about"), REQ_CODE);
	        	return true;
	        /*case R.id.mi_help:
		        startActivity(new Intent(this, MenuHandlerActivity.class));
		        return true;*/
	        default:
	        	return super.onOptionsItemSelected(item);
        }
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