package com.qiyue.qdmobile.ui.prefs.cupcake;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipManager;
import com.qiyue.qdmobile.ui.prefs.PrefsFilters;
import com.qiyue.qdmobile.ui.prefs.PrefsLogic;
import com.qiyue.qdmobile.utils.PreferencesWrapper;

import java.util.ArrayList;
import java.util.List;

public class MainPrefs extends SherlockListActivity {
	
	private static final String THIS_FILE = MainPrefs.class.getSimpleName();
	private PrefGroupAdapter adapter;
	
	private PreferencesWrapper prefsWrapper;
	
	private Intent getIntentForType(int t) {
	    Intent it = new Intent(this, PrefsLoaderActivity.class);
	    it.putExtra(PrefsLogic.EXTRA_PREFERENCE_TYPE, t);
	    return it;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		prefsWrapper = new PreferencesWrapper(this);
		
		List<PrefGroup> prefs_list = new ArrayList<PrefGroup>();
		prefs_list.add(new PrefGroup(R.string.prefs_fast, R.string.prefs_fast_desc, 
				R.drawable.ic_prefs_fast, new Intent(SipManager.ACTION_UI_PREFS_FAST)));
		prefs_list.add(new PrefGroup(R.string.prefs_network, R.string.prefs_network_desc, 
				R.drawable.ic_prefs_network, getIntentForType(PrefsLogic.TYPE_NETWORK)));
		prefs_list.add(new PrefGroup(R.string.prefs_media, R.string.prefs_media_desc, 
				R.drawable.ic_prefs_media, getIntentForType(PrefsLogic.TYPE_MEDIA)));		

		prefs_list.add(new PrefGroup(R.string.prefs_ui, R.string.prefs_ui_desc,
				R.drawable.ic_prefs_ui, getIntentForType(PrefsLogic.TYPE_UI)));

		prefs_list.add(new PrefGroup(R.string.prefs_calls, R.string.prefs_calls_desc,
				R.drawable.ic_prefs_calls, getIntentForType(PrefsLogic.TYPE_CALLS)));
		prefs_list.add(new PrefGroup(R.string.filters, R.string.filters_desc, 
				R.drawable.ic_prefs_filter, new Intent(this, PrefsFilters.class)));
		
		adapter = new PrefGroupAdapter(this, prefs_list);
		setListAdapter(adapter);
		
		getListView().setOnCreateContextMenuListener(this);
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.w(THIS_FILE, "Click at index " + position + " id " + id);
		super.onListItemClick(l, v, position, id);
		
		PrefGroup pref_gp = adapter.getItem(position);
		startActivity(pref_gp.intent);
		
	}
	
	class PrefGroup {
		public String title;
		public int icon;
		public String summary;
		public Intent intent;
		
		public PrefGroup(String title, String summary, int icon, Intent intent) {
			this.title = title;
			this.summary = summary;
			this.icon = icon;
			this.intent = intent;
		}

        public PrefGroup(String title_res, int summary_res, int icon, Intent intent) {
            this.title = title_res;
            this.summary = getString(summary_res);
            this.icon = icon;
            this.intent = intent;
        }

		public PrefGroup(int title_res, int summary_res, int icon, Intent intent) {
			this.title = getString(title_res);
			this.summary = getString(summary_res);
			this.icon = icon;
			this.intent = intent;
		}
	}
	
	class PrefGroupAdapter extends ArrayAdapter<PrefGroup>{

		public PrefGroupAdapter(Context context, List<PrefGroup> objects) {
			super(context, R.layout.icon_preference_screen, objects);
		}
		
	    public View getView(int position, View convertView, ViewGroup parent) {
			
			View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.icon_preference_screen, parent, false);
            }
            
            PrefGroup pref_gp = adapter.getItem(position);
            ImageView icon_view = (ImageView)v.findViewById(R.id.icon);
            
            TextView title_view = (TextView)v.findViewById(android.R.id.title);
            TextView summary_view = (TextView)v.findViewById(android.R.id.summary);
            icon_view.setImageResource(pref_gp.icon);
            title_view.setText(pref_gp.title);
            summary_view.setText(pref_gp.summary);
            
            return v;
	    }
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main_prefs, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        PrefsLogic.onMainActivityPrepareOptionMenu(menu, this, prefsWrapper);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (PrefsLogic.onMainActivityOptionsItemSelected(item, this, prefsWrapper)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
}
