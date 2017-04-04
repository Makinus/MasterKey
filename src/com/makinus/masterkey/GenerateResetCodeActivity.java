package com.makinus.masterkey;

import com.makinus.masterkey.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GenerateResetCodeActivity extends BaseActivity{

	final Context context = this;
	
	private Button btnGenerate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generate_code);
		
		btnGenerate = (Button)findViewById(R.id.btnGenerate);
		btnGenerate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String resetCode = generateResetCode();
				dbService.saveMasterKeyAndEmail(dbService.getMasterKey(), resetCode);
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + ""));
		        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[>MasterKey] Reset Code");
		        emailIntent.putExtra(Intent.EXTRA_TEXT, "Reset code : "+ resetCode);
		        //emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body); //If you are using HTML in your body text
		        
		        startActivity(Intent.createChooser(emailIntent, "Opening email.."));
		        Intent i = new Intent();
				i.putExtra("result","Reset code generated successfully!");
				setResult(Activity.RESULT_OK, i);
				finish();
			}
		});
		
	}
	
}
