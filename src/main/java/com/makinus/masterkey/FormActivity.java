package com.makinus.masterkey;

import com.makinus.masterkey.model.Card;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class FormActivity extends BaseActivity{

	final Context context = this;
	
	private Button btnSave;
	private EditText txtAccount, txtUsername, txtPassword, txtUrl;
	private CheckBox cbHidePwd, cbRemindMe;
	private TextView remindMeMsg;
	Dialog dialog;
	private NumberPicker noOfDaysPicker;
	private int remindMeDays;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form);
		
		txtAccount = (EditText)findViewById(R.id.eTxtAccount);
		txtUsername = (EditText)findViewById(R.id.eTxtUsername);
		txtPassword = (EditText)findViewById(R.id.eTxtPassword);
		txtUrl = (EditText)findViewById(R.id.eTxtUrl);
		cbHidePwd = (CheckBox) findViewById(R.id.cbHidePwd);
		cbRemindMe = (CheckBox) findViewById(R.id.cbRemindMe);
		remindMeMsg = (TextView) findViewById(R.id.lblRemindMsg);
		
		cbRemindMe.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked) {
					showDialog();
				} 
				else {
					remindMeDays = 0;
					remindMeMsg.setText(R.string.strRemindDaysMsg);
				}
			}
		});
		
		btnSave = (Button)findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(checkValidation()) {
					dbService.addCard(populateCard());
					setResult(Activity.RESULT_OK, new Intent()
							.putExtra("result","Card saved successfully!"));
					finish();
				}
			}
		});
		
	}
	
	private boolean checkValidation() {
		if(txtAccount.getText().toString().trim().isEmpty()) {
			Toast.makeText(context, "Account is required.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(txtUsername.getText().toString().trim().isEmpty()) {
			Toast.makeText(context, "Username is required.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(txtPassword.getText().toString().trim().isEmpty()) {
			Toast.makeText(context, "Passowrd is required.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private Card populateCard() {
		//Populate Card model object.
		return new Card().setAccount(txtAccount.getText().toString())
				.setUsername(txtUsername.getText().toString())
				.setPassword(mKeyUtil.encrypt(txtPassword.getText().toString()))
				.setUrl(txtUrl.getText().toString())
				.setHidePwd(cbHidePwd.isChecked() ? YES : NO)
				.setRemindMe(cbRemindMe.isChecked() ? YES : NO)
				.setUpdatedOn(getTodaysDate())
				.setColor(mKeyUtil.getRGBColorCode())
				.setRemindMeDays(remindMeDays);
	}
	
	private void showDialog() {
		// custom dialog
		dialog = new Dialog(context);
		dialog.setContentView(R.layout.activity_remindmedialog);
		//dialog.setTitle(">MasterKey");
		dialog.setCancelable(true);
 
		noOfDaysPicker = (NumberPicker) dialog.findViewById(R.id.noOfDaysPicker);
		noOfDaysPicker.setMinValue(1);
		noOfDaysPicker.setMaxValue(90);
		noOfDaysPicker.setValue(30);
		
		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
		dialogButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				remindMeDays = noOfDaysPicker.getValue();
				remindMeMsg.setText(remindDaysMsg.replace("%n%", ""+remindMeDays ));
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
}
