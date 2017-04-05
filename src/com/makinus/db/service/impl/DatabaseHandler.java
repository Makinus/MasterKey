package com.makinus.db.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.makinus.model.Card;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "MasterKey";
 
    // Contacts table name
    private static final String TABLE_MAIN = "main";
    private static final String TABLE_CARDS = "cards";
 
    // Contacts Table Columns names
    private static final String COL_MASTER_KEY = "master_key";
    private static final String COL_RESET_CODE = "reset_code";
    
    private static final String COL_ID = "id";
    private static final String COL_ACCOUNT = "account";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_URL = "url";
    private static final String COL_DELETED = "deleted";
    private static final String COL_HIDE_PWD = "hide_password";
    private static final String COL_REMIND_ME = "remind_me";
    private static final String COL_UPDATED_ON = "updated_on";
    private static final String COL_COLOR = "color_code";
    private static final String COL_REMIND_ME_DAYS = "remind_me_days";
    
    private static DatabaseHandler sInstance;
    private boolean isValidMasterKey = false;
    
    public static synchronized DatabaseHandler getInstance(Context context) {
        
	    // Use the application context, which will ensure that you 
	    // don't accidentally leak an Activity's context.
	    // See this article for more information: http://bit.ly/6LRzfx
	    if (sInstance == null) {
	      sInstance = new DatabaseHandler(context.getApplicationContext());
	    }
	    return sInstance;
	}
    
    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    	String CREATE_KEY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_MAIN + "("
                + COL_MASTER_KEY + " TEXT,"
    			+ COL_RESET_CODE + " TEXT" +")";
    	
        String CREATE_CARDS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CARDS + "("
                + COL_ID + " INTEGER PRIMARY KEY," + COL_ACCOUNT + " TEXT,"
                + COL_USERNAME + " TEXT," + COL_PASSWORD + " TEXT,"
                + COL_URL + " TEXT," + COL_DELETED + " TEXT,"
                + COL_HIDE_PWD + " TEXT," + COL_REMIND_ME + " TEXT,"
                + COL_UPDATED_ON + " DATE," + COL_COLOR + " TEXT,"
                + COL_REMIND_ME_DAYS + " INTEGER" +")";
        
        db.execSQL(CREATE_KEY_TABLE);
        db.execSQL(CREATE_CARDS_TABLE);
    }
    
    
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAIN);
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDS);
 
        onCreate(db);
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Adding new card
    public void addCard(Card card) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(COL_ACCOUNT, card.getAccount()); 
        values.put(COL_USERNAME, card.getUsername()); 
        values.put(COL_PASSWORD, card.getPassword()); 
        values.put(COL_URL, card.getUrl()); 
        values.put(COL_DELETED, card.getDeleted()); 
        values.put(COL_HIDE_PWD, card.getHidePwd()); 
        values.put(COL_REMIND_ME, card.getRemindMe()); 
        values.put(COL_UPDATED_ON, card.getUpdatedOn()); 
        values.put(COL_COLOR, String.valueOf(card.getColor()));
        values.put(COL_REMIND_ME_DAYS, card.getRemindMeDays());
        
        db.insert(TABLE_CARDS, null, values);
        db.close(); 
    }
 
    // Saving new master key
    public void saveMasterKeyAndEmail(String masterKey, String resetCode) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(COL_MASTER_KEY, masterKey);
        values.put(COL_RESET_CODE, resetCode);
                
        db.insert(TABLE_MAIN, null, values);
        db.close(); 
    }
    
    //Getting master key
    public String getMasterKey() {
        
    	String key = "";
        String selectQuery = "SELECT  * FROM " + TABLE_MAIN;
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        if (cursor.moveToFirst()) {
            do {
            	key = cursor.getString(0);
            } while (cursor.moveToNext());
        }
 
        cursor.close();
        db.close();

        return key;
    }
    
  //Getting master reset code
    public String getMasterResetCode() {
        
    	String key = "";
        String selectQuery = "SELECT  * FROM " + TABLE_MAIN;
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        if (cursor.moveToFirst()) {
            do {
            	key = cursor.getString(1);
            } while (cursor.moveToNext());
        }
 
        cursor.close();
        db.close();

        return key;
    }
    
    // Getting All Cards
    public List<Card> getAllCards() {
        List<Card> cardList = new ArrayList<Card>();
        String selectQuery = "SELECT  * FROM " + TABLE_CARDS;
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        if (cursor.moveToFirst()) {
            do {
                Card card = new Card();
                card.setId(Integer.parseInt(cursor.getString(0)));
                card.setAccount(cursor.getString(1));
                card.setUsername(cursor.getString(2));
                card.setPassword(cursor.getString(3));
                card.setUrl(cursor.getString(4));
                card.setDeleted(cursor.getString(5));
                card.setHidePwd(cursor.getString(6));
                card.setRemindMe(cursor.getString(7));
                card.setUpdatedOn(cursor.getString(8));
                card.setColor(Integer.parseInt(cursor.getString(9)));
                card.setRemindMeDays(cursor.getInt(10));
                
                cardList.add(card);
            } while (cursor.moveToNext());
        }
 
        cursor.close();
        db.close();

        return cardList;
    }
 
    // Getting Single card
    public Card getCard(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_CARDS, new String[] { COL_ID,
                COL_ACCOUNT, COL_USERNAME, COL_PASSWORD,
                COL_URL, COL_DELETED, COL_HIDE_PWD,
                COL_REMIND_ME, COL_UPDATED_ON, COL_COLOR, COL_REMIND_ME_DAYS}, COL_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        Card card = new Card();
        card.setId(cursor.getInt(0));
        card.setAccount(cursor.getString(1));
        card.setUsername(cursor.getString(2));
        card.setPassword(cursor.getString(3));
        card.setUrl(cursor.getString(4));
        card.setDeleted(cursor.getString(5));
        card.setHidePwd(cursor.getString(6));
        card.setRemindMe(cursor.getString(7));
        card.setUpdatedOn(cursor.getString(8));
        card.setColor(Integer.parseInt(cursor.getString(9)));
        card.setRemindMeDays(cursor.getInt(10));
        
        cursor.close();
        db.close();

        return card;
    }
    
    // Updating single card
    public int updateCard(Card card) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(COL_ACCOUNT, card.getAccount()); 
        values.put(COL_USERNAME, card.getUsername()); 
        values.put(COL_PASSWORD, card.getPassword()); 
        values.put(COL_URL, card.getUrl()); 
        values.put(COL_DELETED, card.getDeleted()); 
        values.put(COL_HIDE_PWD, card.getHidePwd()); 
        values.put(COL_REMIND_ME, card.getRemindMe()); 
        values.put(COL_UPDATED_ON, card.getUpdatedOn());
        values.put(COL_COLOR, String.valueOf(card.getColor()));
        values.put(COL_REMIND_ME_DAYS, card.getRemindMeDays());
 
        int result = db.update(TABLE_CARDS, values, COL_ID + " = ?",
                new String[] { String.valueOf(card.getId()) });
        
        db.close();
        return result;
    }
 
    // Deleting single card
    public void deleteCard(Card card) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARDS, COL_ID + " = ?",
                new String[] { String.valueOf(card.getId()) });
        db.close();
    }
 
    // Updating Master key
	public int updateMasterKey(String oldKey, String newKey) {
	
		SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(COL_MASTER_KEY, newKey); 
 
        int result = db.update(TABLE_MAIN, values, COL_MASTER_KEY + " = ?",
                new String[] { String.valueOf(oldKey) });
        
        db.close();
        return result;
    }
	
	// Updating Master reset code
	public int updateMasterResetCode(String masterKey, String newCode) {
	
		SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(COL_RESET_CODE, newCode); 
 
        int result = db.update(TABLE_MAIN, values, COL_MASTER_KEY + " = ?",
                new String[] { String.valueOf(masterKey) });
        
        db.close();
        return result;
    }
	
	// Resetting Master key
	public int resetMasterKey(String resetCode, String newKey) {
	
		SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(COL_MASTER_KEY, newKey); 
 
        int result = db.update(TABLE_MAIN, values, COL_RESET_CODE + " = ?",
                new String[] { String.valueOf(resetCode) });
        
        db.close();
        return result;
    }
	
	public boolean isValidMasterKey() {
    	return isValidMasterKey;
    }
    public void setValidMasterKey(boolean flag) {
    	isValidMasterKey = true;
    }
}
