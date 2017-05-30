package com.makinus.masterkey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SearchActivity extends BaseActivity{

	final Context context = this;
	
	private Button btnBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		btnBack = (Button)findViewById(R.id.btnSearch);
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
					setResult(Activity.RESULT_OK, new Intent().putExtra("isSearch", Boolean.TRUE));
					finish();
			}
		});
	}
	
}
