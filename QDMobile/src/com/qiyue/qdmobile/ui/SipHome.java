package com.qiyue.qdmobile.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.github.snowdream.android.util.Log;
import com.nineoldandroids.animation.ValueAnimator;
import com.qiyue.qdmobile.BasFragmentActivity;
import com.qiyue.qdmobile.QDMobileApplication;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipConfigManager;
import com.qiyue.qdmobile.api.SipManager;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.ui.account.AccountActivity;
import com.qiyue.qdmobile.ui.calllog.CallLogListFragment;
import com.qiyue.qdmobile.ui.dialpad.DialerFragment;
import com.qiyue.qdmobile.ui.messages.ConversationsListFragment;
import com.qiyue.qdmobile.ui.prefs.SettingsFragment;
import com.qiyue.qdmobile.utils.*;
import com.qiyue.qdmobile.utils.NightlyUpdater.UpdaterPopupLauncher;
import com.qiyue.qdmobile.utils.backup.BackupWrapper;
import com.qiyue.qdmobile.wizards.BasePrefsWizard;
import com.qiyue.qdmobile.wizards.WizardUtils.WizardInfo;

import java.io.File;
import java.util.ArrayList;

public class SipHome extends BasFragmentActivity {

    private static final String THIS_FILE = SipHome.class.getSimpleName();

    private PreferencesProviderWrapper prefProviderWrapper;

    private boolean hasTriedOnceActivateAcc = false;

    private Thread asyncSanityChecker;

    private DialerFragment mDialpadFragment;
    private CallLogListFragment mCallLogFragment;
    private SettingsFragment mSettingsFragment;

    private TextView mDialpadBtn, mRecentsBtn, mContactsBtn, mMessagesBtn, mSettingsBtn;

    private Typeface mRBCLightFontFace;

    private ViewGroup mHeaderGroup;

    private int mMenuHeight;

    private LocationClient mLocalLocationClient;


    /**
     * Listener interface for Fragments accommodated in {@link ViewPager}
     * enabling them to know when it becomes visible or invisible inside the
     * ViewPager.
     */
    public interface ViewPagerVisibilityListener {
        void onVisibilityChanged(boolean visible);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(THIS_FILE, "onCreate()...");

        prefProviderWrapper = new PreferencesProviderWrapper(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sip_home_one_pane);

        initBarTintManager();

        mRBCLightFontFace = Typeface.createFromAsset(getAssets(), Constants.FONTS_RBC_LIGHT);

        mHeaderGroup = (ViewGroup) findViewById(R.id.ll_menu_btn);
        mMenuHeight = getResources().getDimensionPixelSize(R.dimen.menu_height);

        mLocalLocationClient = ((QDMobileApplication) getApplication()).mLocalLocationClient;
        if (mLocalLocationClient != null && mLocalLocationClient.isStarted() == false) {
            mLocalLocationClient.start();
        }

        hideMenu();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        FlashFragment flashFragment = (FlashFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_FLASH);
        if (flashFragment == null) {
            flashFragment = new FlashFragment();
        }
        ft.replace(R.id.content_frame, flashFragment, Constants.FRAGMENT_TAG_FLASH);
        ft.commit();

        mDialpadBtn = (TextView) findViewById(R.id.menu_dialpad);
        mDialpadBtn.setOnClickListener((View v) ->  onClickDialpad());

        mRecentsBtn = (TextView) findViewById(R.id.menu_recents);
        mRecentsBtn.setOnClickListener(v -> onClickRecents());

        // TODO
        mContactsBtn = (TextView) findViewById(R.id.menu_contacts);
        mContactsBtn.setVisibility(View.GONE);
        mContactsBtn.setOnClickListener(v -> setMenuButton(MenuButton.contacts));

        mMessagesBtn = (TextView) findViewById(R.id.menu_messages);
        mMessagesBtn.setOnClickListener(v -> onClickMessages());

        mSettingsBtn = (TextView) findViewById(R.id.menu_settings);
        mSettingsBtn.setOnClickListener(v -> onClickSettings());

        setMenuButton(MenuButton.dialpad);

        hasTriedOnceActivateAcc = false;

        if (!prefProviderWrapper.getPreferenceBooleanValue(SipConfigManager.PREVENT_SCREEN_ROTATION)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }

        // Async check
        asyncSanityChecker = new Thread() {
            public void run() {
                asyncSanityCheck();
            }
        };
        asyncSanityChecker.start();

    }

    private void onClickSettings() {
        setMenuButton(MenuButton.settings);

        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();

        mSettingsFragment
                = (SettingsFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_SETTINGS);
        if (mSettingsFragment == null) {
            mSettingsFragment = new SettingsFragment();
        }
        ft1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft1.replace(R.id.content_frame, mSettingsFragment, Constants.FRAGMENT_TAG_SETTINGS);

        ft1.commit();
    }

    private void onClickMessages() {
        setMenuButton(MenuButton.messages);

        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();

        mMessagesFragment
                = (ConversationsListFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_CONVERSATIONS_LIST);
        if (mMessagesFragment == null) {
            mMessagesFragment = new ConversationsListFragment();
        }
        ft1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft1.replace(R.id.content_frame, mMessagesFragment, Constants.FRAGMENT_TAG_CONVERSATIONS_LIST);
        ft1.commit();
    }

    private void onClickRecents() {
        setMenuButton(MenuButton.recents);
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();

        mCallLogFragment
                = (CallLogListFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_RECENTS);
        if (mCallLogFragment == null) {
            mCallLogFragment = new CallLogListFragment();
        }
        ft1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft1.replace(R.id.content_frame, mCallLogFragment, Constants.FRAGMENT_TAG_RECENTS);

        ft1.commit();
    }

    private void onClickDialpad() {
        setMenuButton(MenuButton.dialpad);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        mDialpadFragment
                = (DialerFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_DIALPAD);
        if (mDialpadFragment == null) {
            mDialpadFragment = new DialerFragment();
        }
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.content_frame, mDialpadFragment, Constants.FRAGMENT_TAG_DIALPAD);

        ft.commit();
    }

    public enum MenuButton {
        dialpad, recents, contacts, messages, settings
    }

    public void setMenuButton(MenuButton menuButton) {

        Drawable dialpadDrawable = getResources().getDrawable(R.drawable.icon_keypad_idle),
                 recentsDrawable = getResources().getDrawable(R.drawable.icon_recents_idle),
                 contactsDrawable = getResources().getDrawable(R.drawable.icon_contacts_idle),
                 messagesDrawable = getResources().getDrawable(R.drawable.icon_messages_idle),
                 settingsDrawable = getResources().getDrawable(R.drawable.icon_settings_idle);

        int dialpadColor = getResources().getColor(R.color.menu_text_color_nor);
        int recentsColor = getResources().getColor(R.color.menu_text_color_nor);
        int contactColor = getResources().getColor(R.color.menu_text_color_nor);
        int messageColor = getResources().getColor(R.color.menu_text_color_nor);
        int settingsColor = getResources().getColor(R.color.menu_text_color_nor);

        switch (menuButton) {
            case dialpad:
                dialpadColor = getResources().getColor(R.color.menu_text_color);
                dialpadDrawable = getResources().getDrawable(R.drawable.icon_keypad_selected);
                break;

            case recents:
                recentsColor = getResources().getColor(R.color.menu_text_color);
                recentsDrawable = getResources().getDrawable(R.drawable.icon_recents_selected);
                break;

            case contacts:
                contactColor = getResources().getColor(R.color.menu_text_color);
                contactsDrawable = getResources().getDrawable(R.drawable.icon_contacts_selected);
                break;

            case messages:
                messageColor = getResources().getColor(R.color.menu_text_color);
                messagesDrawable = getResources().getDrawable(R.drawable.icon_messages_selected);
                break;

            case settings:
                settingsColor = getResources().getColor(R.color.menu_text_color);
                settingsDrawable = getResources().getDrawable(R.drawable.icon_more_selected);
                break;
        }

        dialpadDrawable.setBounds(0, 0, dialpadDrawable.getMinimumWidth(), dialpadDrawable.getMinimumHeight());
        mDialpadBtn.setCompoundDrawables(null, dialpadDrawable, null, null);

        recentsDrawable.setBounds(0, 0, dialpadDrawable.getMinimumWidth(), dialpadDrawable.getMinimumHeight());
        mRecentsBtn.setCompoundDrawables(null, recentsDrawable, null, null);

        contactsDrawable.setBounds(0, 0, dialpadDrawable.getMinimumWidth(), dialpadDrawable.getMinimumHeight());
        mContactsBtn.setCompoundDrawables(null, contactsDrawable, null, null);

        messagesDrawable.setBounds(0, 0, dialpadDrawable.getMinimumWidth(), dialpadDrawable.getMinimumHeight());
        mMessagesBtn.setCompoundDrawables(null, messagesDrawable, null, null);

        settingsDrawable.setBounds(0, 0, dialpadDrawable.getMinimumWidth(), dialpadDrawable.getMinimumHeight());
        mSettingsBtn.setCompoundDrawables(null, settingsDrawable, null, null);

        mDialpadBtn.setTextColor(dialpadColor);
        mRecentsBtn.setTextColor(recentsColor);
        mContactsBtn.setTextColor(contactColor);
        mMessagesBtn.setTextColor(messageColor);
        mSettingsBtn.setTextColor(settingsColor);

    }

    public Typeface getRBCLightFontFace() {
        if (mRBCLightFontFace == null) {
            mRBCLightFontFace = Typeface.createFromAsset(getAssets(), Constants.FONTS_RBC_LIGHT);
        }

        return mRBCLightFontFace;
    }

    private ConversationsListFragment mMessagesFragment;
//    private FavListFragment mPhoneFavoriteFragment;

    private void asyncSanityCheck() {
        // if(Compatibility.isCompatible(9)) {
        // // We check now if something is wrong with the gingerbread dialer
        // integration
        // Compatibility.getDialerIntegrationState(SipHome.this);
        // }

        // Nightly build check
        if (NightlyUpdater.isNightlyBuild(this)) {
            Log.d(THIS_FILE, "Sanity check : we have a nightly build here");
            ConnectivityManager connectivityService = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo ni = connectivityService.getActiveNetworkInfo();
            // Only do the process if we are on wifi
            if (ni != null && ni.isConnected() && ni.getType() == ConnectivityManager.TYPE_WIFI) {
                // Only do the process if we didn't dismissed previously
                NightlyUpdater nu = new NightlyUpdater(this);

                if (!nu.ignoreCheckByUser()) {
                    long lastCheck = nu.lastCheck();
                    long current = System.currentTimeMillis();
                    long oneDay = 43200000; // 12 hours
                    if (current - oneDay > lastCheck) {
                        if (onForeground) {
                            // We have to check for an update
                            UpdaterPopupLauncher ru = nu.getUpdaterPopup(false);
                            if (ru != null && asyncSanityChecker != null) {
                                runOnUiThread(ru);
                            }
                        }
                    }
                }
            }
        }

    }

    // Service monitoring stuff
    private void startSipService() {
        Thread t = new Thread("StartSip") {
            public void run() {
                Intent serviceIntent = new Intent(SipManager.INTENT_SIP_SERVICE);
                // Optional, but here we bundle so just ensure we are using csipsimple package
                serviceIntent.setPackage(SipHome.this.getPackageName());
                serviceIntent.putExtra(SipManager.EXTRA_OUTGOING_ACTIVITY, new ComponentName(SipHome.this, SipHome.class));
                startService(serviceIntent);
                postStartSipService();
            }
        };
        t.start();

    }

    private void postStartSipService() {
        // If we have never set fast settings
        if (CustomDistribution.showFirstSettingScreen()) {
            if (!prefProviderWrapper.getPreferenceBooleanValue(PreferencesWrapper.HAS_ALREADY_SETUP, false)) {
                Intent prefsIntent = new Intent(SipManager.ACTION_UI_PREFS_FAST);
                prefsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(prefsIntent);
                return;
            }
        } else {
            boolean doFirstParams = !prefProviderWrapper.getPreferenceBooleanValue(PreferencesWrapper.HAS_ALREADY_SETUP, false);
            prefProviderWrapper.setPreferenceBooleanValue(PreferencesWrapper.HAS_ALREADY_SETUP, true);
            if (doFirstParams) {
                prefProviderWrapper.resetAllDefaultValues();
            }
        }

        // If we have no account yet, open account panel,
        if (!hasTriedOnceActivateAcc) {

            Cursor c = getContentResolver().query(SipProfile.ACCOUNT_URI, new String[]{
                    SipProfile.FIELD_ID
            }, null, null, null);
            int accountCount = 0;
            if (c != null) {
                try {
                    accountCount = c.getCount();
                } catch (Exception e) {
                    Log.e(THIS_FILE, "Something went wrong while retrieving the account", e);
                } finally {
                    c.close();
                }
            }

            if (accountCount == 0) {
                Intent accountIntent = null;
                WizardInfo distribWizard = CustomDistribution.getCustomDistributionWizard();
                if (distribWizard != null) {
                    accountIntent = new Intent(this, BasePrefsWizard.class);
                    accountIntent.putExtra(SipProfile.FIELD_WIZARD, distribWizard.id);
                } else {
                    accountIntent = new Intent(this, AccountActivity.class);
                }

                if (accountIntent != null) {
                    accountIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(accountIntent);
                    hasTriedOnceActivateAcc = true;
                    return;
                }
            }
            hasTriedOnceActivateAcc = true;
        }
    }

    private boolean onForeground = false;

    @Override
    protected void onPause() {
        Log.d(THIS_FILE, "On Pause SIPHOME");
        onForeground = false;
        if (asyncSanityChecker != null) {
            if (asyncSanityChecker.isAlive()) {
                asyncSanityChecker.interrupt();
                asyncSanityChecker = null;
            }
        }
        super.onPause();

    }

    @Override
    protected void onResume() {
        Log.d(THIS_FILE, "On Resume SIPHOME");
        super.onResume();
        onForeground = true;

        prefProviderWrapper.setPreferenceBooleanValue(PreferencesWrapper.HAS_BEEN_QUIT, false);

        // Set visible the currently selected account
//        sendFragmentVisibilityChange(mViewPager.getCurrentItem(), true);

        Log.d(THIS_FILE, "WE CAN NOW start SIP service");
        startSipService();

    }

    private ArrayList<View> getVisibleLeafs(View v) {
        ArrayList<View> res = new ArrayList<View>();
        if (v.getVisibility() != View.VISIBLE) {
            return res;
        }
        if (v instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
                ArrayList<View> subLeafs = getVisibleLeafs(((ViewGroup) v).getChildAt(i));
                res.addAll(subLeafs);
            }
            return res;
        }
        res.add(v);
        return res;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private String initDialerWithText = null;
    Integer initTabId = null;

    @Override
    protected void onDestroy() {
        disconnect(false);
        super.onDestroy();
        Log.d(THIS_FILE, "---DESTROY SIP HOME END---");

    }


    private final static int CHANGE_PREFS = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHANGE_PREFS) {
            sendBroadcast(new Intent(SipManager.ACTION_SIP_REQUEST_RESTART));
            BackupWrapper.getInstance(this).dataChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void disconnect(boolean quit) {
        Log.d(THIS_FILE, "True disconnection...");
        Intent intent = new Intent(SipManager.ACTION_OUTGOING_UNREGISTER);
        intent.putExtra(SipManager.EXTRA_OUTGOING_ACTIVITY, new ComponentName(this, SipHome.class));
        sendBroadcast(intent);
        if (quit) {
            finish();
        }
    }

    public void showMenu() {
        if (mHeaderGroup != null) {
            ViewGroup.LayoutParams menu_lp = mHeaderGroup.getLayoutParams();
            menu_lp.height = mMenuHeight;
            mHeaderGroup.setLayoutParams(menu_lp);
        }
    }

    public void hideMenu() {
        if (mHeaderGroup != null) {
            ViewGroup.LayoutParams menu_lp = mHeaderGroup.getLayoutParams();
            menu_lp.height = 0;
            mHeaderGroup.setLayoutParams(menu_lp);
        }
    }

    public void hideMenuWithAnimator() {
        if (mHeaderGroup != null) {

            final ViewGroup.LayoutParams menu_lp = mHeaderGroup.getLayoutParams();
            ValueAnimator animator = ValueAnimator.ofInt(mMenuHeight, 0).setDuration(300);

            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    menu_lp.height = (Integer) valueAnimator.getAnimatedValue();
                    mHeaderGroup.setLayoutParams(menu_lp);
                }
            });

            animator.start();
        }
    }

    public void showMenuWithAnimator() {
        if (mHeaderGroup != null) {

            final ViewGroup.LayoutParams menu_lp = mHeaderGroup.getLayoutParams();
            if (menu_lp.height == mMenuHeight) {
                return;
            }

            ValueAnimator animator = ValueAnimator.ofInt(0, mMenuHeight).setDuration(300);

            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    menu_lp.height = (Integer) valueAnimator.getAnimatedValue();
                    mHeaderGroup.setLayoutParams(menu_lp);

                }
            });

            animator.start();
        }
    }

    public void showLogsDialog() {
        showDialog(DIALOG_SHOW_LOGS);
    }

    private boolean[] mLogFileChecked;
    private static final int DIALOG_SHOW_LOGS       = 1;

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {

            case DIALOG_SHOW_LOGS:

                final String logPath = FileUtil.getQDMobileLogsFilePath(QDMobileApplication.getContextQD());
                final String[] logs = new File(logPath).list();
                mLogFileChecked = new boolean[logs.length];

                return new AlertDialog.Builder(SipHome.this)
                        .setTitle("QDMobile Logs")
                        .setMultiChoiceItems(logs, new boolean[logs.length],
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        if (which < mLogFileChecked.length) {
                                            mLogFileChecked[which] = isChecked;
                                        }
                                    }
                                })
                        .setPositiveButton("Send Email", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                if (mLogFileChecked == null || mLogFileChecked.length > logs.length) {
                                    return;
                                }

                                ArrayList<String> attachments = new ArrayList<String>();
                                for (int i = 0; i < mLogFileChecked.length; i++) {
                                    boolean isCheck = mLogFileChecked[i];
                                    if (isCheck) {
                                        attachments.add(FileUtil.getQDMobileLogsFilePath(QDMobileApplication.getContextQD())
                                                + File.separator + logs[i]);
                                    }
                                }

                                if (attachments == null || attachments.isEmpty()) {
                                    return;
                                }

                                String emailBody = "";
                                if (attachments.size() == 1) {
                                    emailBody = "The attached is QDMobile log";
                                } else {
                                    emailBody = "The attached are QDMobile logs";
                                }

                                emailBody += "\n\n"
                                        + Build.BRAND + " " + android.os.Build.MODEL
                                        + " Android " + android.os.Build.VERSION.SDK_INT;

                                new EmailSender(SipHome.this).sendEmail(new String[]{},
                                        "QDMobile Log", emailBody, attachments.toArray(new String[]{}));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }).create();


        }

        return null;

    }
}
