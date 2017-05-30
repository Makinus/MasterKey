package com.makinus.masterkey.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;

public class Card implements Serializable, Comparable<Card>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2583782310009038367L;
	private int id;
	private String account;
	private String url;
	private String username;
	private String password;
	private String deleted = "N";
	private String hidePwd = "N";
	private String remindMe = "N";
	private int remindMeDays;
	private String updatedOn; 
	private int color;
	
	public int getId() {
		return id;
	}
	public Card setId(int id) {
		this.id = id;
		return this;
	}
	public String getAccount() {
		return account;
	}
	public Card setAccount(String account) {
		this.account = account;
		return this;
	}
	public String getUrl() {
		return url;
	}
	public Card setUrl(String url) {
		this.url = url;
		return this;
	}
	public String getUsername() {
		return username;
	}
	public Card setUsername(String username) {
		this.username = username;
		return this;
	}
	public String getPassword() {
		return password;
	}
	public Card setPassword(String password) {
		this.password = password;
		return this;
	}
	public String getDeleted() {
		return deleted;
	}
	public Card setDeleted(String deleted) {
		this.deleted = deleted;
		return this;
	}
	
	public String getHidePwd() {
		return hidePwd;
	}
	public Card setHidePwd(String hidePwd) {
		this.hidePwd = hidePwd;
		return this;
	}
	public String getRemindMe() {
		return remindMe;
	}
	public Card setRemindMe(String remindMe) {
		this.remindMe = remindMe;
		return this;
	}
	public String getUpdatedOn() {
		return updatedOn;
	}
	public Card setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
		return this;
	}
	@Override
	public String toString() {
		return "# "+account+" #";
	}
	public int getColor() {
		return color;
	}
	public Card setColor(int color) {
		this.color = color;
		return this;
	}
	public int getRemindMeDays() {
		return remindMeDays;
	}
	public Card setRemindMeDays(int remindMeDays) {
		this.remindMeDays = remindMeDays;
		return this;
	}


	@Override
	public int compareTo(@NonNull Card another) {
		if(another == null)
			return 0;

		return this.getAccount().compareTo(another.getAccount());
	}
}
