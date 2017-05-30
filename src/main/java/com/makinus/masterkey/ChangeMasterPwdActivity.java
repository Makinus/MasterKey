package com.makinus.masterkey;

import com.makinus.masterkey.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangeMasterPwdActivity extends BaseActivity{

	final Context context = this;
	
	private Button btnConfirm;
	private EditText txtOldKey;
	private EditText txtNewKey;
	private EditText txtConfirmKey;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_masterkey);
		
		txtOldKey = (EditText)findViewById(R.id.eTxtOldPwd);
		txtNewKey = (EditText)findViewById(R.id.eTxtNewPwd);
		txtConfirmKey = (EditText)findViewById(R.id.eTxtConfirmPwd);
		
		btnConfirm = (Button)findViewById(R.id.btnConfirm);
		btnConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String masterKeyDigest = dbService.getMasterKey();
				if(checkValidation(masterKeyDigest)) {
					dbService.updateMasterKey(masterKeyDigest, mKeyUtil.digest(txtNewKey.getText().toString()));
					Intent i = new Intent();
					i.putExtra("result","Master key updated successfully!");
					setResult(Activity.RESULT_OK, i);
					finish();
				}
			}
		});
		
	}
	
	private boolean checkValidation(String masterKeyDigest) {
		if(txtOldKey.getText().toString().trim().isEmpty()) {
			Toast.makeText(context, "Current Master key is required.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(txtNewKey.getText().toString().trim().isEmpty()) {
			Toast.makeText(context, "New Master key is required.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(txtConfirmKey.getText().toString().trim().isEmpty()) {
			Toast.makeText(context, "Confirm Master key is required.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(!txtNewKey.getText().toString().trim().equals(txtConfirmKey.getText().toString().trim())) {
			Toast.makeText(context, "New and Confirm Master key must be same.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(!mKeyUtil.matches(txtOldKey.getText().toString().trim(), masterKeyDigest)) {
			Toast.makeText(context, "Invalid Master key. Try Again!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(mKeyUtil.matches(txtNewKey.getText().toString().trim(), masterKeyDigest)) {
			Toast.makeText(context, "New Master key must not be same as old Master key.!", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
}
