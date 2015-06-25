package com.qiyue.qdmobile.service;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.api.SipConfigManager;
import com.qiyue.qdmobile.utils.PreferencesWrapper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.text.TextUtils;

/**
 * This provider allow to retrieve preference from different process than the UI
 * process Should be used by service For the future could be usefull for third
 * party apps.
 * 
 * @author r3gis3r
 * 
 */
public class PreferenceProvider extends ContentProvider {

	private PreferencesWrapper prefs;


	private static final int PREFS = 1;
	private static final int PREF_ID = 2;
	private static final int RAZ = 3;

	public static final int COL_INDEX_NAME = 0;
	public static final int COL_INDEX_VALUE = 1;
	private static final String THIS_FILE = "PrefsProvider";
	

	private final static UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		URI_MATCHER.addURI(SipConfigManager.AUTHORITY, SipConfigManager.PREFS_TABLE_NAME, PREFS);
		URI_MATCHER.addURI(SipConfigManager.AUTHORITY, SipConfigManager.PREFS_TABLE_NAME + "/*", PREF_ID);
		URI_MATCHER.addURI(SipConfigManager.AUTHORITY, SipConfigManager.RESET_TABLE_NAME, RAZ);
	}

	@Override
	public boolean onCreate() {
		prefs = new PreferencesWrapper(getContext());
		return true;
	}

	/**
	 * Return the MIME type for an known URI in the provider.
	 */
	@Override
	public String getType(Uri uri) {
		switch (URI_MATCHER.match(uri)) {
		case PREFS:
		case RAZ:
			return SipConfigManager.PREF_CONTENT_TYPE;
		case PREF_ID:
			return SipConfigManager.PREF_CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String order) {
		MatrixCursor resCursor = new MatrixCursor(new String[] { SipConfigManager.FIELD_NAME, SipConfigManager.FIELD_VALUE });
		if (URI_MATCHER.match(uri) == PREF_ID) {
			String name = uri.getLastPathSegment();
			Class<?> aClass = null;
			if (TextUtils.isEmpty(selection)) {
				aClass = PreferencesWrapper.gPrefClass(name);
			} else {
				try {
					aClass = Class.forName(selection);
				} catch (ClassNotFoundException e) {
					Log.e(THIS_FILE, "Impossible to retrieve class from selection");
				}
			}
			Object value = null;
			if (aClass == String.class) {
				value = prefs.getPreferenceStringValue(name);
			} else if (aClass == Float.class) {
				value = prefs.getPreferenceFloatValue(name);
			} else if (aClass == Boolean.class) {
			    Boolean v = prefs.getPreferenceBooleanValue(name);
			    if (v != null) {
			        value = v ? 1 : 0;
			    } else {
			        value = -1;
			    }
			} else if(aClass == Integer.class) {
			    value = prefs.getPreferenceIntegerValue(name);
			}
			
			if (value != null) {
				resCursor.addRow(new Object[] { name, value });
			} else {
				resCursor = null;
			}
		}
		return resCursor;
	}

	@Override
	public int update(Uri uri, ContentValues cv, String selection, String[] selectionArgs) {
		int count = 0;
		switch (URI_MATCHER.match(uri)) {
		case PREFS:
			// Ignore for now
			break;
		case PREF_ID:
			String name = uri.getLastPathSegment();
			Class<?> aClass = null;
			if (TextUtils.isEmpty(selection)) {
				aClass = PreferencesWrapper.gPrefClass(name);
			} else {
				try {
					aClass = Class.forName(selection);
				} catch (ClassNotFoundException e) {
					Log.e(THIS_FILE, "Impossible to retrieve class from selection");
				}
			}
			if (aClass == String.class) {
				prefs.setPreferenceStringValue(name, cv.getAsString(SipConfigManager.FIELD_VALUE));
			} else if (aClass == Float.class) {
				prefs.setPreferenceFloatValue(name, cv.getAsFloat(SipConfigManager.FIELD_VALUE));
			} else if (aClass == Boolean.class) {
				prefs.setPreferenceBooleanValue(name, cv.getAsBoolean(SipConfigManager.FIELD_VALUE));
			}
			count++;
			break;
		case RAZ:
			prefs.resetAllDefaultValues();
			break;
		}
		return count;
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// Not implemented
		return 0;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// Not implemented
		return null;
	}

}
