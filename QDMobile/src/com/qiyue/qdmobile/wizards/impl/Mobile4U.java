package com.qiyue.qdmobile.wizards.impl;

import android.text.InputType;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.models.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Mobile4U extends SimpleImplementation {
	

	@Override
	protected String getDomain() {
		return "sip.mobile4u.hu";
	}
	
	@Override
	protected String getDefaultName() {
		return "Mobile4U";
	}

	//Customization
	@Override
	public void fillLayout(final SipProfile account) {
		super.fillLayout(account);

        accountUsername.setTitle(R.string.w_common_phone_number);
        accountUsername.setDialogTitle(R.string.w_common_phone_number);
		accountUsername.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
		
	}
	
	@Override
	public String getDefaultFieldSummary(String fieldName) {
		if(fieldName.equals(USER_NAME)) {
			return parent.getString(R.string.w_common_phone_number_desc);
		}
		return super.getDefaultFieldSummary(fieldName);
	}
	/*
	@Override
	public SipProfile buildAccount(SipProfile account) {
		SipProfile acc = super.buildAccount(account);
		return acc;
	}
	*/
	@Override
	public List<Filter> getDefaultFilters(SipProfile acc) {
        // Filter1: Rewrite >> Starts with "+" >> Replace match by "00"
        ArrayList<Filter> filters = new ArrayList<Filter>();

        Filter f = new Filter();
        f.account = (int) acc.id;
        f.action = Filter.ACTION_REPLACE;
        f.matchPattern = "^" + Pattern.quote("+") + "(.*)$";
        f.replacePattern = "00$1";
        f.matchType = Filter.MATCHER_STARTS;
        filters.add(f);

        // Filter2: Rewrite >> Starts with "06" >> Replace match by "0036"
        f = new Filter();
        f.account = (int) acc.id;
        f.action = Filter.ACTION_REPLACE;
        f.matchPattern = "^" + Pattern.quote("06") + "(.*)$";
        f.replacePattern = "0036$1";
        f.matchType = Filter.MATCHER_STARTS;
        filters.add(f);

        return filters;
	}
	
}
