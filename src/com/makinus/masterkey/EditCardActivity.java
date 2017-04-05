package com.makinus.masterkey;

import com.makinus.model.Card;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

public class EditCardActivity extends BaseActivity{

	private Button btnDelete, btnUpdate;
	private int txtId, remindMeDays;
	private EditText txtAccount, txtUsername, txtPassword, txtUrl;
	private AlertDialog confirmDialog;
	private CheckBox cbHidePwd, cbRemindMe;
	private TextView remindMeMsg;
	Dialog dialog;
	private NumberPicker noOfDaysPicker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editcard);
		
		setFields((Card) getIntent().getExtras().get("card"));
		
		btnDelete = (Button)findViewById(R.id.btnDelete);
		btnUpdate = (Button)findViewById(R.id.btnUpdate);
		
		btnDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				confirmDialog.show();
			}
		});
		
		btnUpdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(checkValidation()) {
					dbService.updateCard(populateCard());
					setResult(Activity.RESULT_OK, new Intent()
							.putExtra("result","Card updated successfully!"));
					finish();
				}
			}
		});
		
		confirmDialog = new AlertDialog.Builder(this)
				   .setTitle(">MasterKey")
				   .setMessage("Are you sure?")
	               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	    dbService.deleteCard(populateCard());
	                	    confirmDialog.dismiss();
		       				setResult(Activity.RESULT_OK, new Intent()
		       						.putExtra("isDeleted", Boolean.TRUE));
		       				finish();
	                   }
	               })
	               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   confirmDialog.dismiss();
	                   }
	               }).create();
	}
	
	private void setFields(Card card) {
		
		txtAccount = (EditText)findViewById(R.id.eTxtAccount);
		txtUsername = (EditText)findViewById(R.id.eTxtUsername);
		txtPassword = (EditText)findViewById(R.id.eTxtPassword);
		txtUrl = (EditText)findViewById(R.id.eTxtUrl);
		cbHidePwd = (CheckBox) findViewById(R.id.cbHidePwd);
		cbRemindMe = (CheckBox) findViewById(R.id.cbRemindMe);
		txtUrl = (EditText)findViewById(R.id.eTxtUrl);
		remindMeMsg = (TextView) findViewById(R.id.lblRemindMsg);
		
		txtId = card.getId();
		txtAccount.setText(card.getAccount());
		txtUsername.setText(card.getUsername());
		txtPassword.setText(card.getPassword());
		txtUrl.setText(card.getUrl());
		cbHidePwd.setChecked(card.getHidePwd().equals(YES) ? true : false);
		cbRemindMe.setChecked(card.getRemindMe().equals(YES) ? true : false);
		remindMeDays = card.getRemindMeDays();
		remindMeMsg.setText(remindDaysMsg.replace("%n%", ""+remindMeDays));
		
		remindMeMsg = (TextView) findViewById(R.id.lblRemindMsg);
		
		cbRemindMe.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
					showDialog();
				else {
					remindMeDays = 0;
					remindMeMsg.setText(R.string.strRemindDaysMsg);
				}
			}
		});
	}
	
	private Card populateCard() {
		//Populate Card model object.
		
		return new Card().setId(txtId).setAccount(txtAccount.getText().toString())
				.setUsername(txtUsername.getText().toString())
				.setPassword(mKeyUtil.encrypt(txtPassword.getText().toString()))
				.setUrl(txtUrl.getText().toString())
				.setHidePwd(cbHidePwd.isChecked() ? YES : NO)
				.setRemindMe(cbRemindMe.isChecked() ? YES : NO)
				.setUpdatedOn(getTodaysDate())
				.setColor(mKeyUtil.getHSBColorCode())
				.setRemindMeDays(remindMeDays);
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
	
	private void showDialog() {
		// custom dialog
		dialog = new Dialog(context);
		dialog.setContentView(R.layout.activity_remindmedialog);
		dialog.setTitle(">MasterKey");
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


