package com.qiyue.qdmobile.wizards;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.utils.CustomDistribution;
import com.qiyue.qdmobile.wizards.impl.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class WizardUtils {

	public static class WizardInfo {
		public String label;
		public String id;
		public int icon;
		public int priority = 99;
		public Locale[] countries;
		public boolean isGeneric = false;
		public boolean isWorld = false;
		public Class<?> classObject;
		
		public WizardInfo(String aId, String aLabel, int aIcon, int aPriority, Locale[] aCountries,
						  boolean aIsGeneric, boolean aIsWorld, Class<?> aClassObject) {
			id = aId;
			label = aLabel;
			icon = aIcon;
			priority = aPriority;
			countries = aCountries;
			isGeneric = aIsGeneric;
			isWorld = aIsWorld;
			classObject = aClassObject;
		}
	};
	
	private static boolean initDone = false;
	
    public static final String LABEL = "LABEL";
    public static final String ICON = "ICON";
    public static final String ID = "ID";
    public static final String LANG_DISPLAY = "DISPLAY";
    public static final String PRIORITY = "PRIORITY";
    public static final String PRIORITY_INT = "PRIORITY_INT";
    
    public static final String EXPERT_WIZARD_TAG = "EXPERT";
    public static final String BASIC_WIZARD_TAG = "BASIC";
    public static final String ADVANCED_WIZARD_TAG = "ADVANCED";
    public static final String LOCAL_WIZARD_TAG = "LOCAL";
    
    private static HashMap<String, WizardInfo> WIZARDS_DICT;
    
    private static class WizardPrioComparator implements Comparator<Map<String, Object>> {

		@Override
		public int compare(Map<String, Object> infos1, Map<String, Object> infos2) {
			if (infos1 != null && infos2 != null) {
			    if ((Boolean) infos1.get(PRIORITY_INT)) {
    				Integer w1 = (Integer) infos1.get(PRIORITY);
    				Integer w2 = (Integer) infos2.get(PRIORITY);
    				//Log.d(THIS_FILE, "Compare : "+w1+ " vs "+w2);
    				if (w1 > w2) {
    					return -1;
    				}
    				if (w1 < w2) {
    					return 1;
    				}
			    } else {
			        String name1 = (String) infos1.get(LABEL);
			        String name2 = (String) infos2.get(LABEL);
			        return name1.compareToIgnoreCase(name2);
			    }
			}
			return 0;
		}
    }
    
    private static Locale locale(String isoCode) {
    	String[] codes = isoCode.split("_");
    	if (codes.length == 2) {
    		return new Locale(codes[0].toLowerCase(), codes[1].toUpperCase());
    	} else if (codes.length == 1) {
    		return new Locale(codes[0].toLowerCase());
    	}
    	Log.e("WizardUtils", "Invalid locale " + isoCode);
    	return null;
    }
    
	
    /**
     * Initialize wizards list
     */
	private static void initWizards() {
		WIZARDS_DICT = new HashMap<String, WizardInfo>();
		
		//Generic
		if (CustomDistribution.distributionWantsGeneric(BASIC_WIZARD_TAG)) {
    		WIZARDS_DICT.put(BASIC_WIZARD_TAG, new WizardInfo(BASIC_WIZARD_TAG, "Basic", 
    				R.drawable.ic_wizard_basic, 50, 
    				new Locale[] {}, true, false, 
    				Basic.class));
		}
        if (CustomDistribution.distributionWantsGeneric(ADVANCED_WIZARD_TAG)) {
    		WIZARDS_DICT.put(ADVANCED_WIZARD_TAG, new WizardInfo(ADVANCED_WIZARD_TAG, "Advanced", 
    				R.drawable.ic_wizard_advanced, 10, 
    				new Locale[] {}, true, false, 
    				Advanced.class));
        }

		initDone = true;
	}
	
	private static Map<String, Object> wizardInfoToMap(WizardInfo infos, boolean usePriorityInt) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(LABEL, infos.label);
		map.put(ID, infos.id);
		map.put(ICON, infos.icon);
		map.put(PRIORITY, infos.priority);
		map.put(PRIORITY_INT, usePriorityInt);
		return map;
	}

	public static HashMap<String, WizardInfo> getWizardsList(){
		if (!initDone) {
			initWizards();
		}
		return WIZARDS_DICT;
	}
	
	public static WizardInfo getWizardClass(String wizardId) {
		if (!initDone) {
			initWizards();
		}
		return WIZARDS_DICT.get(wizardId);
	}
	
	public static int getWizardIconRes(String wizardId) {
		// Update account image
		WizardInfo wizard_infos = WizardUtils.getWizardClass(wizardId);
		if (wizard_infos != null) {
			if(!wizard_infos.isGeneric) {
				return wizard_infos.icon;
			}
		}
		return R.drawable.ic_phone_100;
	}
	
	public static Bitmap getWizardBitmap(Context ctxt, SipProfile account) {
		if (account.icon == null) {
			Resources r = ctxt.getResources();
			BitmapDrawable bd = ((BitmapDrawable) r.getDrawable(WizardUtils.getWizardIconRes(account.wizard)));
			account.icon = bd.getBitmap();
		}
		return account.icon;
	}

	private static ArrayList<HashMap<String, String>> wizardGroups = null;

	public static ArrayList<HashMap<String, String>> getWizardsGroups(Context context) {
	    if (wizardGroups != null) {
	        return wizardGroups;
	    }
		wizardGroups = new ArrayList<HashMap<String, String>>();
		boolean hasLocal = false;
		boolean hasGeneric = false;
		boolean hasWorld = false;
		boolean hasOther = false;
		
        Set<Entry<String, WizardInfo>> wizards = getWizardsList().entrySet();
		for (Entry<String, WizardInfo> wizard : wizards) {
		    boolean found = false;

            if (wizard.getValue().isGeneric) {
                hasGeneric = true;
                found = true;
			} else if (wizard.getValue().isWorld) {
				hasWorld = true;
				found = true;
			}
			if (!found) {
				for (Locale country : wizard.getValue().countries) {
					if (country != null) {
						if (country.getCountry().equals(Locale.getDefault().getCountry())) {
							hasLocal = true;
							found = true;
							break;
						} else if (country.getCountry().equalsIgnoreCase("")) {
							if (country.getLanguage().equals(Locale.getDefault().getLanguage())) {
								hasLocal = true;
								found = true;
								break;
							}
						}
					}
				}
			}

			if (!found) {
                hasOther = true;
            }
            if (hasLocal && hasGeneric && hasOther && hasWorld) {
                break;
            }
        }
		
		
		HashMap<String, String> m;
		
		//Local
		if (hasLocal) {
    		m = new HashMap<String, String>();
    		m.put(LANG_DISPLAY, Locale.getDefault().getDisplayCountry());
    		wizardGroups.add(m);
		}
		//Generic
		if (hasGeneric) {
    		m = new HashMap<String, String>();
    		m.put(LANG_DISPLAY, context.getString(R.string.generic_wizards_text));
    		wizardGroups.add(m);
		}
		
		if (hasWorld) {
			//World
			m = new HashMap<String, String>();
			m.put(LANG_DISPLAY, context.getString(R.string.world_wide_providers_text));
			wizardGroups.add(m);
		}
		if (hasOther) {
			//Others
			m = new HashMap<String, String>();
			m.put(LANG_DISPLAY, context.getString(R.string.other_country_providers_text));
			wizardGroups.add(m);
		}
		
		return wizardGroups;
	}


	public static ArrayList<ArrayList<Map<String, Object>>> getWizardsGroupedList() {
		ArrayList<Map<String, Object>> locale_list = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> generic_list = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> world_list = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> others_list = new ArrayList<Map<String, Object>>();
		
		Set<Entry<String, WizardInfo>> wizards = getWizardsList().entrySet();
		for (Entry<String, WizardInfo> wizard : wizards) {
			boolean found = false;
			
			for (Locale country : wizard.getValue().countries) {
				if (country != null) {
					if (country.getCountry().equals(Locale.getDefault().getCountry())) {
						found = true;
						locale_list.add(wizardInfoToMap(wizard.getValue(), true));
						break;
					} else if(country.getCountry().equalsIgnoreCase("")) {
						if (country.getLanguage().equals(Locale.getDefault().getLanguage())) {
							found = true;
							locale_list.add(wizardInfoToMap(wizard.getValue(), true));
							break;
						}
					}
				}
			}
			if (!found) {
				if (wizard.getValue().isGeneric) {
					generic_list.add(wizardInfoToMap(wizard.getValue(), true));
					found = true;
				} else if (wizard.getValue().isWorld) {
					world_list.add(wizardInfoToMap(wizard.getValue(), false));
					found = true;
				}
			}
			if (!found) {
				others_list.add(wizardInfoToMap(wizard.getValue(), false));
			}
		}
		
		WizardPrioComparator comparator = new WizardPrioComparator();
		Collections.sort(locale_list, comparator);
		Collections.sort(generic_list, comparator);
		Collections.sort(world_list, comparator);
		Collections.sort(others_list, comparator);
		
		ArrayList<ArrayList<Map<String, Object>>> result = new ArrayList<ArrayList<Map<String,Object>>>();
		if (locale_list.size() > 0) {
		    result.add(locale_list);
		}
		if (generic_list.size() > 0) {
		    result.add(generic_list);
		}
		if (world_list.size() > 0) {
		    result.add(world_list);
		}
		if (others_list.size() > 0) {
		    result.add(others_list);
		}
		return result;
	}

	
	
	
}
