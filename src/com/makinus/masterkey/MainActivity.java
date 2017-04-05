package com.makinus.masterkey;

import java.io.Serializable;
import java.util.List;

import com.makinus.masterkey.R;
import com.makinus.masterkey.service.MyScheduleReceiver;
import com.makinus.masterkey.service.NotifyService;
import com.makinus.model.Card;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	
	EditText dlgMasterKey, dlgEmail;
	Button addNew;
	ListView cardsListView;
	LazyAdapter lazyAdapter;
	TextView dlgLblMasterKey, dlgLblForgotKey;
	
	Dialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		try {
			if(!isNotifyServiceRunning(NotifyService.class)) {
				//startService(new Intent(context, NotifyService.class));
				IntentFilter intentFilter = new IntentFilter("com.makinus.masterkey.service.MyScheduleReceiver");
				registerReceiver(new MyScheduleReceiver(), intentFilter);
				
				sendBroadcast(new Intent("com.makinus.masterkey.service.MyScheduleReceiver")); 
			}
		} catch(Exception ex){
		}
		
		//AddNew Button
		addNew = (Button) findViewById(R.id.btnAddNew);
		addNew.setText("Enter Master key");
		addNew.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(addNew.getText().toString().equals("Enter Master key")){
					showDialog();
				}
				else {
					startActivityForResult(new Intent(context, FormActivity.class), REQ_CODE);
				}
			}
		});
		
		
		//Cards listView
		cardsListView = (ListView)findViewById(R.id.cardsList);
		cardsListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				startActivityForResult(new Intent(context, ViewCardActivity.class)
						.putExtra("card", (Serializable) (Card) cardsListView.getItemAtPosition(arg2)),
						REQ_CODE);
				
			}
		});
		
		if(dbService.isValidMasterKey())
			showAllCards();
		else
			showDialog();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    if (requestCode == REQ_CODE) {
	        if(resultCode == Activity.RESULT_OK){
        		Toast.makeText(context, data.getStringExtra("result"), Toast.LENGTH_SHORT).show();
	        }
	    }
	    showAllCards();
	}
	
	private void showAllCards()
	{
		if(addNew.getText().toString().equals("Add Card")) {
			List<Card> cardsList = dbService.getAllCards();
			lazyAdapter = new LazyAdapter(this, cardsList);
			if(cardsList != null) {
				cardsListView.setAdapter(lazyAdapter);
				if(cardsList.size() == 0)
					Toast.makeText(context, "No Records", Toast.LENGTH_SHORT).show();
			}
		}		
		else
			showDialog();
	}
	
	private void showDialog() {
		// custom dialog
		dialog = new Dialog(context);
		dialog.setContentView(R.layout.activity_masterdialog);
		dialog.setTitle(">MasterKey");
		dialog.setCancelable(true);
 
		dlgMasterKey = (EditText) dialog.findViewById(R.id.eTxtMasterKey);
		dlgLblMasterKey = (TextView) dialog.findViewById(R.id.lblMasterKey);
		dlgLblForgotKey = (TextView) dialog.findViewById(R.id.lblForgotKey);
		dlgLblForgotKey.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(context, ResetMasterPwdActivity.class), REQ_CODE);
			}
		});
		
		
		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
		dialogButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String masterKey = dlgMasterKey.getText().toString().trim();
				String masterKeyDigest = dbService.getMasterKey();
				
				if(!checkDlgValidation()) {
					dialog.show();
				}
				else if(masterKeyDigest.isEmpty()) {
					dbService.saveMasterKeyAndEmail(mKeyUtil.digest(masterKey), "");
					Toast.makeText(context, "Master key saved successfully!", Toast.LENGTH_SHORT).show();
					addNew.setText("Add Card");
					dbService.setValidMasterKey(true);
					dialog.dismiss();
				}
				else {
					if(mKeyUtil.matches(masterKey, masterKeyDigest)) {
						Toast.makeText(context, "Login success!", Toast.LENGTH_SHORT).show();
						dbService.setValidMasterKey(true);
						addNew.setText("Add Card");
						dlgLblForgotKey.setVisibility(View.VISIBLE);
						showAllCards();
						dialog.dismiss();
					}
					else {
						Toast.makeText(context, "Invalid Master key. Try Again!", Toast.LENGTH_SHORT).show();
						dialog.show();
					}
				}
					
			}
		});
 
		//Show Save button at first time use.
		if(dbService.getMasterKey().isEmpty()) {
			dialogButton.setText("Save");
			dlgLblMasterKey.setText("New Master key : ");
			dlgLblForgotKey.setVisibility(View.INVISIBLE);
		}
		
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				finishFromChild(getParent());
			}
		});
		dialog.show();
		
	}

	/*@Override
	public void onBackPressed() {
	    if(dialog.isShowing()){
	    	dialog.dismiss();
	    }
	    this.finish();
	    super.onBackPressed();
	}*/
	
	private boolean checkDlgValidation() {
		if(dlgMasterKey.getText().toString().trim().isEmpty()){
			Toast.makeText(context, "MasterKey is required.", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
}