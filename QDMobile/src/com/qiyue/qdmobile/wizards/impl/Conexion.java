package com.qiyue.qdmobile.wizards.impl;

import android.preference.ListPreference;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipProfile;

import java.util.SortedMap;
import java.util.TreeMap;


public class Conexion extends AuthorizationImplementation {
	
	@Override
	protected String getDefaultName() {
		return "Conexion";
	}
	
	ListPreference sipServer; 
	static SortedMap<String, String> providers = new TreeMap<String, String>(){
		private static final long serialVersionUID = -2561302247222706262L;
	{
		put("sip.asu.conexion.com.py", "sip.asu.conexion.com.py");
		put("sip.sao.conexion.com.br", "sip.sao.conexion.com.br");
	}
	};

	private static final String PROVIDER_LIST_KEY = "provider_list";
	
	@Override
	public void fillLayout(final SipProfile account) {
		super.fillLayout(account);
		
		
		boolean recycle = true;
		sipServer = (ListPreference) findPreference(PROVIDER_LIST_KEY);
		if(sipServer == null) {
			sipServer = new ListPreference(parent);
			sipServer.setKey(PROVIDER_LIST_KEY);
			recycle = false;
		}
		
		CharSequence[] e = new CharSequence[providers.size()];
		CharSequence[] v = new CharSequence[providers.size()];
        int i = 0;
        for(String pv : providers.keySet()) {
        	e[i] = pv;
        	v[i] = providers.get(pv);
        	i++;
        }
		
		sipServer.setEntries(e);
		sipServer.setEntryValues(v);
		sipServer.setDialogTitle(R.string.w_common_server);
		sipServer.setTitle(R.string.w_common_server);
        sipServer.setDefaultValue("sip.asu.conexion.com.py");
        
        if(!recycle) {
            addPreference(sipServer);
        }
		
        String domain = account.reg_uri;
        if( domain != null ) {
            for(CharSequence state : v) {
                String currentComp = "sip:" + state;
	        	if( currentComp.equalsIgnoreCase(domain) ) {
	        		sipServer.setValue(state.toString());
	        		break;
	        	}
	        }
        }
        
        hidePreference(null, SERVER);
   }
	
	/* (non-Javadoc)
	 * @see com.qiyue.qdmobile.wizards.impl.AuthorizationImplementation#canSave()
	 */
	@Override
	public boolean canSave() {
        boolean isValid = true;
        
        isValid &= checkField(accountDisplayName, isEmpty(accountDisplayName));
        isValid &= checkField(accountUsername, isEmpty(accountUsername));
        isValid &= checkField(accountAuthorization, isEmpty(accountAuthorization));
        isValid &= checkField(accountPassword, isEmpty(accountPassword));

        return isValid;
	}


	protected String getDomain() {
		String provider = sipServer.getValue();
		if(provider != null) {
			return provider;
		}
		return "";
	}
	
	@Override
	public void updateDescriptions() {
		super.updateDescriptions();
		setStringFieldSummary(PROVIDER_LIST_KEY);
	}

	@Override
	public String getDefaultFieldSummary(String fieldName) {
		if(fieldName == PROVIDER_LIST_KEY) {
			if(sipServer != null) {
				return sipServer.getEntry().toString();
			}
		}
		
		return super.getDefaultFieldSummary(fieldName);
	}
}
