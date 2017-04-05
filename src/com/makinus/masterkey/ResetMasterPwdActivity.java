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

public class ResetMasterPwdActivity extends BaseActivity{

	final Context context = this;
	
	private Button btnReset;
	private EditText txtResetCode, txtNewKey, txtConfirmKey;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_masterkey);
		
		txtResetCode = (EditText)findViewById(R.id.eTxtResetCode);
		txtNewKey = (EditText)findViewById(R.id.eTxtNewPwd);
		txtConfirmKey = (EditText)findViewById(R.id.eTxtConfirmPwd);
		
		btnReset = (Button)findViewById(R.id.btnReset);
		btnReset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(checkValidation()) {
					dbService.resetMasterKey(txtResetCode.getText().toString(), mKeyUtil.digest(txtNewKey.getText().toString()));
					setResult(Activity.RESULT_OK, new Intent()
							.putExtra("result","Master key reset successfully!"));
					finish();
				}
			}
		});
		
	}
	
	private boolean checkValidation() {
		if(txtResetCode.getText().toString().trim().isEmpty()) {
			Toast.makeText(context, "Reset code is required.", Toast.LENGTH_SHORT).show();
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
		String savedResetCode = dbService.getMasterResetCode();
		if(!txtResetCode.getText().toString().trim().equals(savedResetCode)) {
			Toast.makeText(context, "Invalid Reset code. Try Again!", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
}
