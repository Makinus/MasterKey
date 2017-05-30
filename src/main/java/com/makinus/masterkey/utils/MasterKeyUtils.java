package com.makinus.masterkey.utils;

import java.util.Properties;
import java.util.Random;

import org.jasypt.digest.StandardStringDigester;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.content.Context;
import android.graphics.Color;

public class MasterKeyUtils {

	private static MasterKeyUtils sInstance;
	private StandardStringDigester digester;
	private StandardPBEStringEncryptor encryptor;
	
	private MasterKeyUtils(Context context) {
		try{
		    Properties properties = new Properties();
		    properties.load(context.getAssets().open("app.properties"));
			
			digester = new StandardStringDigester();
			digester.setAlgorithm(properties.getProperty("DIGESTER_ALGORITHM"));
			digester.setIterations(Integer.parseInt(properties.getProperty("ITERATIONS")));
			
			encryptor = new StandardPBEStringEncryptor();
			encryptor.setAlgorithm(properties.getProperty("ENCRYPTOR_ALGORITHM"));
			encryptor.setPassword(properties.getProperty("SALT_PWD"));
			
		}catch(Exception e){
		    e.printStackTrace();
		}
	}
	
	public static synchronized MasterKeyUtils getInstance(Context context) {
        
	    if (sInstance == null) {
	      sInstance = new MasterKeyUtils(context);
	    }
	    return sInstance;
	}
	
	public String encrypt(String password) {
		return encryptor.encrypt(password);
	}
	
	public String decrypt(String password) {
		return encryptor.decrypt(password);
	}
	
	public String digest(String masterKey) {
		return digester.digest(masterKey);
	}
	
	public boolean matches(String pwdString, String pwdDigest) {
		return digester.matches(pwdString, pwdDigest);
	}
	
	public int getRGBColorCode(){
		int R = (int)(Math.random()*256);
		int G = (int)(Math.random()*256);
		int B= (int)(Math.random()*256);
		
		return Color.rgb(R, B, G);
		
	}
	
	public int getHSBColorCode(){
		//to get rainbow, pastel colors
		float a[] = new float[3];
		Random random = new Random();
		a[0] = random.nextFloat();//hue
		a[1] = 0.9f;//1.0 for brilliant, 0.0 for dull
		a[2] = 1.0f; //1.0 for brighter, 0.0 for black
		
		return Color.HSVToColor(a);
	}
	
	public boolean isExpired(String cardUpdatedDate){
	
		  DateTimeFormatter f = DateTimeFormat.forPattern("dd/MM/yyyy");
		  DateTime updatedOn = f.parseDateTime(cardUpdatedDate);
		  if(updatedOn.plusDays(30).isBeforeNow()) {
			  return true;
		  }
		  return false;
	}
}
