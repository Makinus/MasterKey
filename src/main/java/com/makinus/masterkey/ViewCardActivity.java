package com.makinus.masterkey;

import java.io.Serializable;
import java.util.Calendar;

import com.makinus.masterkey.R;
import com.makinus.masterkey.model.Card;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class ViewCardActivity extends BaseActivity{

	private Button btnEdit, btnShare;
	private int txtId, remindMeDays;
	private String pwd;
	private TextView txtAccount, txtUsername, txtPassword, txtUrl, remindMeMsg;
	private CheckBox cbHidePwd, cbRemindMe;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewcard);
		
		btnEdit = (Button)findViewById(R.id.btnEdit);
		btnEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(context, EditCardActivity.class)
						.putExtra("card", (Serializable) populateCard()), REQ_CODE);
			}
		});
		
		btnShare = (Button)findViewById(R.id.btnShare);
		btnShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				startActivity(Intent.createChooser(new Intent().setAction(Intent.ACTION_SEND)
						.putExtra(Intent.EXTRA_TEXT, getShareMessage())
						.setType("text/plain"), "Share To.."));
			}
		});
		
		setFields((Card) getIntent().getExtras().get("card"));
		
	}
	
	private String getShareMessage() {
		return "Account Name : " + txtAccount.getText().toString() +
				"\nUsername : " + txtUsername.getText().toString() +
				"\nPassword : " + txtPassword.getText().toString() +
				"\nUrl : " + txtUrl.getText().toString() +
				"\n\n - shared via >MasterKey app.";
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    if (requestCode == REQ_CODE) {
	        if(resultCode == Activity.RESULT_OK){
	        	if(data.getBooleanExtra("isDeleted", Boolean.FALSE)) {
					setResult(Activity.RESULT_OK, new Intent()
							.putExtra("result","Card deleted successfully!"));
					finish();
	        	} else {
		        	setFields(dbService.getCard(txtId));
		            Toast.makeText(context, data.getStringExtra("result"), Toast.LENGTH_SHORT).show();
	        	}
	        }
	    }
	}
	
	private void setFields(Card card) {
		
		txtAccount = (TextView)findViewById(R.id.eTxtAccount);
		txtUsername = (TextView)findViewById(R.id.eTxtUsername);
		txtPassword = (TextView)findViewById(R.id.eTxtPassword);
		txtUrl = (TextView)findViewById(R.id.eTxtUrl);
		cbHidePwd = (CheckBox) findViewById(R.id.cbHidePwd);
		cbRemindMe = (CheckBox) findViewById(R.id.cbRemindMe);
		remindMeMsg = (TextView) findViewById(R.id.lblRemindMsg);
		
		txtId = card.getId();
		txtAccount.setText(card.getAccount());
		txtUsername.setText(card.getUsername());
		txtUrl.setText(card.getUrl());
		pwd = mKeyUtil.decrypt(card.getPassword());
		if(card.getHidePwd().equals(YES)) {
			txtPassword.setText("******");
			cbHidePwd.setChecked(true);
			btnShare.setEnabled(false);
		}
		else {
			txtPassword.setText(pwd);
			cbHidePwd.setChecked(false);
			btnShare.setEnabled(true);
		}
		cbRemindMe.setChecked(card.getRemindMe().equals(YES) ? true : false);
		remindMeDays = card.getRemindMeDays();
		if (remindMeDays > 0)
			remindMeMsg.setText(remindDaysMsg.replace("%n%", ""+remindMeDays));
		else
			remindMeMsg.setText(R.string.strRemindDaysMsg);
	}

	private Card populateCard() {
		//Populate Card model object.
		return new Card().setId(txtId).setAccount(txtAccount.getText().toString())
				.setUsername(txtUsername.getText().toString())
				.setPassword(pwd)
				.setUrl(txtUrl.getText().toString())
				.setHidePwd(cbHidePwd.isChecked() ? YES : NO)
				.setRemindMe(cbRemindMe.isChecked() ? YES : NO)
				.setUpdatedOn(Calendar.getInstance().getTime().toString())
				.setRemindMeDays(remindMeDays);
	}

}
