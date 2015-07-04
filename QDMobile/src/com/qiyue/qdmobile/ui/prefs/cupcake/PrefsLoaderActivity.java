package com.qiyue.qdmobile.ui.prefs.cupcake;

import android.os.Bundle;

import com.qiyue.qdmobile.ui.prefs.GenericPrefs;
import com.qiyue.qdmobile.ui.prefs.PrefsLogic;
import com.qiyue.qdmobile.utils.Compatibility;

public class PrefsLoaderActivity extends GenericPrefs {
    
    private int getPreferenceType() {
        return getIntent().getIntExtra(PrefsLogic.EXTRA_PREFERENCE_TYPE, 0);
    }

    @Override
    protected int getXmlPreferences() {
        return PrefsLogic.getXmlResourceForType(getPreferenceType());
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(PrefsLogic.getTitleResourceForType(getPreferenceType()));
    }

    @Override
    protected void afterBuildPrefs() {
        super.afterBuildPrefs();
        PrefsLogic.afterBuildPrefsForType(this, this, getPreferenceType());
        
    }

    @Override
    protected void updateDescriptions() {
        PrefsLogic.updateDescriptionForType(this, this, getPreferenceType());
    }


    
}
