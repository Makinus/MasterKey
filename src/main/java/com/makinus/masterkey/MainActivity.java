package com.makinus.masterkey;

import java.io.Serializable;
import java.util.List;

import com.makinus.masterkey.service.MyScheduleReceiver;
import com.makinus.masterkey.service.NotifyService;
import com.makinus.masterkey.model.Card;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends BaseActivity {
	
	EditText dlgMasterKey, dlgEmail;
	Button addNew;
	FloatingActionButton addNewFB;
	TextView dlgLblMasterKey, dlgLblForgotKey;

	SearchView editSearch;
	Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		ActionBar ab = getSupportActionBar();
		// Enable the Up button
		ab.setDisplayHomeAsUpEnabled(false);
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
		addNewFB = (FloatingActionButton) findViewById(R.id.btnAddNewFB);
		addNewFB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
					startActivityForResult(new Intent(context, FormActivity.class), REQ_CODE);
			}
		});

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
		cardsListView.setTextFilterEnabled(true);
		cardsListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				startActivityForResult(new Intent(context, ViewCardActivity.class)
						.putExtra("card", (Serializable) (Card) cardsListView.getItemAtPosition(arg2)),
						REQ_CODE);
				
			}
		});
		
		
		cardsListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Card c = (Card) cardsListView.getItemAtPosition(position);
				View vi=view;
		        if(view==null){
		        	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		            vi = inflater.inflate(R.layout.list_row, null);
		        }
		        if(c.getHidePwd().equals("N")) {
			        final TextView password = (TextView)vi.findViewById(R.id.txtPassword);
			        if(password.getText().toString().equals("********")) {
			        	password.setText(mKeyUtil.decrypt(c.getPassword()));
				        password.setTextColor(Color.RED);
			        }
			        else {
			        	password.setText("********");
			            password.setTextColor(context.getResources().getColor(R.color.textColor));
			        }
		        }
				return true;
			}
			
		});

		editSearch = (SearchView) findViewById(R.id.search);
		editSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				lazyAdapter.filter(newText);
				return false;
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
	        	if(!data.getBooleanExtra("isMenu", Boolean.FALSE)) 
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
		//dialog.setTitle(">MasterKey");
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
			/*case R.id.mi_backup:
				dbService.doBackup(context);
				return true;
			case R.id.mi_restore:
				dbService.doRestore(context);
				return true;*/
			case R.id.mi_about:
				startActivityForResult(new Intent(this, MenuHandlerActivity.class).putExtra("menu", "about"), REQ_CODE);
				return true;
			case R.id.mi_sort:
				showPopupManu();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void showPopupManu() {

		View menuItemSort = findViewById(R.id.mi_sort);
		PopupMenu popupMenu = new PopupMenu(this, menuItemSort);
		MenuInflater inflater = popupMenu.getMenuInflater();
		inflater.inflate(R.menu.activity_main_popup, popupMenu.getMenu());

		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case R.id.mi_sort_a:
						lazyAdapter.sort("atoz");
						return true;
					case R.id.mi_sort_z:
						lazyAdapter.sort("ztoa");
						return true;
					default:
						return false;
				}

			}
		});
		popupMenu.show();
	}

	/*@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Sort by..");
		menu.add(0, v.getId(), 0, "Ascending");//groupId, itemId, order, title
		menu.add(0, v.getId(), 0, "Descending");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item){
		if(item.getTitle()=="Ascending")
			lazyAdapter.sort("atoz");
		else if(item.getTitle()=="Descending")
			lazyAdapter.sort("ztoa");
		else
			return false;

		return true;
	}*/

}