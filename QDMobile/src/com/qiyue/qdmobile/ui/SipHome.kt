package com.qiyue.qdmobile.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.database.Cursor
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.support.v4.app.*
import android.support.v4.view.ViewPager
import android.view.*
import android.widget.TextView

import com.baidu.location.LocationClient
import com.github.snowdream.android.util.Log
import com.nineoldandroids.animation.ValueAnimator
import com.qiyue.qdmobile.BasFragmentActivity
import com.qiyue.qdmobile.QDMobileApplication
import com.qiyue.qdmobile.R
import com.qiyue.qdmobile.api.SipConfigManager
import com.qiyue.qdmobile.api.SipManager
import com.qiyue.qdmobile.api.SipProfile
import com.qiyue.qdmobile.ui.account.AccountActivity
import com.qiyue.qdmobile.ui.calllog.CallLogListFragment
import com.qiyue.qdmobile.ui.dialpad.DialerFragment
import com.qiyue.qdmobile.ui.messages.ConversationsListFragment
import com.qiyue.qdmobile.ui.prefs.SettingsFragment
import com.qiyue.qdmobile.utils.*
import com.qiyue.qdmobile.utils.NightlyUpdater.UpdaterPopupLauncher
import com.qiyue.qdmobile.utils.backup.BackupWrapper
import com.qiyue.qdmobile.wizards.BasePrefsWizard
import com.qiyue.qdmobile.wizards.WizardUtils.WizardInfo

import java.io.File
import java.util.ArrayList

import kotlinx.android.synthetic.sip_home_one_pane.*

/**
 *
 */
public class SipHome : BasFragmentActivity() {

    private var prefProviderWrapper: PreferencesProviderWrapper? = null

    private var hasTriedOnceActivateAcc = false

    private var asyncSanityChecker: Thread? = null

    private var mDialpadFragment: Fragment? = null
    private var mCallLogFragment: Fragment? = null
    private var mSettingsFragment: Fragment? = null
    private var mMessagesFragment: Fragment? = null

    private var mRBCLightFontFace: Typeface? = null

    private var mHeaderGroup: ViewGroup? = null

    private var mMenuHeight: Int = 0

    private var mLocalLocationClient: LocationClient? = null


    /**
     * Listener interface for Fragments accommodated in [ViewPager]
     * enabling them to know when it becomes visible or invisible inside the
     * ViewPager.
     */
    public interface ViewPagerVisibilityListener {
        public fun onVisibilityChanged(visible: Boolean)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.i(THIS_FILE, "--->> onCreate(Bundle) <<---")

        prefProviderWrapper = PreferencesProviderWrapper(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.sip_home_one_pane)

        initBarTintManager()

        mRBCLightFontFace = Typeface.createFromAsset(getAssets(), Constants.FONTS_RBC_LIGHT)

        mHeaderGroup = findViewById(R.id.ll_menu_btn) as ViewGroup
        mMenuHeight = getResources().getDimensionPixelSize(R.dimen.menu_height)

        mLocalLocationClient = (getApplication() as QDMobileApplication).mLocalLocationClient
        if (mLocalLocationClient != null && mLocalLocationClient!!.isStarted() == false) {
            mLocalLocationClient!!.start()
        }

        hideMenu()

        val ft = getSupportFragmentManager().beginTransaction()
        var flashFragment = getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_FLASH)
        if (flashFragment == null) {
            flashFragment = FlashFragment()
        }
        ft.replace(R.id.content_frame, flashFragment, Constants.FRAGMENT_TAG_FLASH)
        ft.commit()

        menu_dialpad.setOnClickListener { onClickDialpad() }

        menu_recents.setOnClickListener { onClickRecents() }

//        mRecentsBtn = findViewById(R.id.menu_recents) as TextView
//        mRecentsBtn!!.setOnClickListener()

        // TODO
        menu_contacts.setVisibility(View.GONE)

//        mContactsBtn = findViewById(R.id.menu_contacts) as TextView
//        mContactsBtn!!.setVisibility(View.GONE)
//        mContactsBtn!!.setOnClickListener()

//        mMessagesBtn = findViewById(R.id.menu_messages) as TextView
//        mMessagesBtn!!.setOnClickListener()

        menu_messages.setOnClickListener { onClickMessages() }

//        mSettingsBtn = findViewById(R.id.menu_settings) as TextView
//        mSettingsBtn!!.setOnClickListener()

        menu_settings.setOnClickListener { onClickSettings() }

        setMenuButton(MenuButton.dialpad)

        hasTriedOnceActivateAcc = false

        if (!prefProviderWrapper!!.getPreferenceBooleanValue(SipConfigManager.PREVENT_SCREEN_ROTATION)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR)
        }

        // Async check
        asyncSanityChecker = object : Thread() {
            override fun run() {
                asyncSanityCheck()
            }
        }
        asyncSanityChecker!!.start()

    }

    private fun onClickSettings() {
        setMenuButton(MenuButton.settings)

        val ft1 = getSupportFragmentManager().beginTransaction()

        mSettingsFragment = getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_SETTINGS)
        if (mSettingsFragment == null) {
            mSettingsFragment = SettingsFragment()
        }
        ft1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        ft1.replace(R.id.content_frame, mSettingsFragment, Constants.FRAGMENT_TAG_SETTINGS)

        ft1.commit()
    }

    private fun onClickMessages() {
        setMenuButton(MenuButton.messages)

        val ft1 = getSupportFragmentManager().beginTransaction()

        mMessagesFragment = getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_CONVERSATIONS_LIST)
        if (mMessagesFragment == null) {
            mMessagesFragment = ConversationsListFragment()
        }
        ft1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        ft1.replace(R.id.content_frame, mMessagesFragment, Constants.FRAGMENT_TAG_CONVERSATIONS_LIST)
        ft1.commit()
    }

    private fun onClickRecents() {
        setMenuButton(MenuButton.recents)
        val ft1 = getSupportFragmentManager().beginTransaction()

        mCallLogFragment = getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_RECENTS)
        if (mCallLogFragment == null) {
            mCallLogFragment = CallLogListFragment()
        }
        ft1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        ft1.replace(R.id.content_frame, mCallLogFragment, Constants.FRAGMENT_TAG_RECENTS)

        ft1.commit()
    }

    private fun onClickDialpad() {
        setMenuButton(MenuButton.dialpad)
        val ft = getSupportFragmentManager().beginTransaction()

        mDialpadFragment = getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_DIALPAD)
        if (mDialpadFragment == null) {
            mDialpadFragment = DialerFragment()
        }
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        ft.replace(R.id.content_frame, mDialpadFragment, Constants.FRAGMENT_TAG_DIALPAD)

        ft.commit()
    }

    public enum class MenuButton {
        dialpad,
        recents,
        contacts,
        messages,
        settings
    }

    public fun setMenuButton(menuButton: MenuButton) {

        var dialpadDrawable: Drawable = getResources().getDrawable(R.drawable.icon_keypad_idle)
        var recentsDrawable: Drawable = getResources().getDrawable(R.drawable.icon_recents_idle)
        var contactsDrawable: Drawable = getResources().getDrawable(R.drawable.icon_contacts_idle)
        var messagesDrawable: Drawable = getResources().getDrawable(R.drawable.icon_messages_idle)
        var settingsDrawable: Drawable = getResources().getDrawable(R.drawable.icon_settings_idle)

        var dialpadColor = getResources().getColor(R.color.menu_text_color_nor)
        var recentsColor = getResources().getColor(R.color.menu_text_color_nor)
        var contactColor = getResources().getColor(R.color.menu_text_color_nor)
        var messageColor = getResources().getColor(R.color.menu_text_color_nor)
        var settingsColor = getResources().getColor(R.color.menu_text_color_nor)

        when (menuButton) {
            SipHome.MenuButton.dialpad -> {
                dialpadColor = getResources().getColor(R.color.menu_text_color)
                dialpadDrawable = getResources().getDrawable(R.drawable.icon_keypad_selected)
            }

            SipHome.MenuButton.recents -> {
                recentsColor = getResources().getColor(R.color.menu_text_color)
                recentsDrawable = getResources().getDrawable(R.drawable.icon_recents_selected)
            }

            SipHome.MenuButton.contacts -> {
                contactColor = getResources().getColor(R.color.menu_text_color)
                contactsDrawable = getResources().getDrawable(R.drawable.icon_contacts_selected)
            }

            SipHome.MenuButton.messages -> {
                messageColor = getResources().getColor(R.color.menu_text_color)
                messagesDrawable = getResources().getDrawable(R.drawable.icon_messages_selected)
            }

            SipHome.MenuButton.settings -> {
                settingsColor = getResources().getColor(R.color.menu_text_color)
                settingsDrawable = getResources().getDrawable(R.drawable.icon_more_selected)
            }
        }

        dialpadDrawable.setBounds(0, 0, dialpadDrawable.getMinimumWidth(), dialpadDrawable.getMinimumHeight())
        menu_dialpad.setCompoundDrawables(null, dialpadDrawable, null, null)


//        mDialpadBtn!!.setCompoundDrawables(null, dialpadDrawable, null, null)

        recentsDrawable.setBounds(0, 0, dialpadDrawable.getMinimumWidth(), dialpadDrawable.getMinimumHeight())
        menu_recents.setCompoundDrawables(null, recentsDrawable, null, null)

        contactsDrawable.setBounds(0, 0, dialpadDrawable.getMinimumWidth(), dialpadDrawable.getMinimumHeight())
        menu_contacts.setCompoundDrawables(null, contactsDrawable, null, null)

        messagesDrawable.setBounds(0, 0, dialpadDrawable.getMinimumWidth(), dialpadDrawable.getMinimumHeight())
        menu_messages.setCompoundDrawables(null, messagesDrawable, null, null)

        settingsDrawable.setBounds(0, 0, dialpadDrawable.getMinimumWidth(), dialpadDrawable.getMinimumHeight())
        menu_settings.setCompoundDrawables(null, settingsDrawable, null, null)

        menu_dialpad.setTextColor(dialpadColor)
        menu_recents.setTextColor(recentsColor)
        menu_contacts.setTextColor(contactColor)
        menu_messages.setTextColor(messageColor)
        menu_settings.setTextColor(settingsColor)

    }

    public fun getRBCLightFontFace(): Typeface? {
        if (mRBCLightFontFace == null) {
            mRBCLightFontFace = Typeface.createFromAsset(getAssets(), Constants.FONTS_RBC_LIGHT)
        }

        return mRBCLightFontFace
    }

    //    private FavListFragment mPhoneFavoriteFragment;

    private fun asyncSanityCheck() {
        // if(Compatibility.isCompatible(9)) {
        // // We check now if something is wrong with the gingerbread dialer
        // integration
        // Compatibility.getDialerIntegrationState(SipHome.this);
        // }

        // Nightly build check
        if (NightlyUpdater.isNightlyBuild(this)) {
            Log.d(THIS_FILE, "Sanity check : we have a nightly build here")
            val connectivityService = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni = connectivityService.getActiveNetworkInfo()
            // Only do the process if we are on wifi
            if (ni != null && ni.isConnected() && ni.getType() == ConnectivityManager.TYPE_WIFI) {
                // Only do the process if we didn't dismissed previously
                val nu = NightlyUpdater(this)

                if (!nu.ignoreCheckByUser()) {
                    val lastCheck = nu.lastCheck()
                    val current = System.currentTimeMillis()
                    val oneDay = 43200000 // 12 hours
                    if (current - oneDay > lastCheck) {
                        if (onForeground) {
                            // We have to check for an update
                            val ru = nu.getUpdaterPopup(false)
                            if (ru != null && asyncSanityChecker != null) {
                                runOnUiThread(ru)
                            }
                        }
                    }
                }
            }
        }

    }

    // Service monitoring stuff
    private fun startSipService() {
        val t = object : Thread("StartSip") {
            override fun run() {
                val serviceIntent = Intent(SipManager.INTENT_SIP_SERVICE)
                // Optional, but here we bundle so just ensure we are using csipsimple package
                serviceIntent.setPackage(this@SipHome.getPackageName())
                serviceIntent.putExtra(SipManager.EXTRA_OUTGOING_ACTIVITY, ComponentName(this@SipHome, javaClass<SipHome>()))
                startService(serviceIntent)
                postStartSipService()
            }
        }
        t.start()

    }

    private fun postStartSipService() {
        // If we have never set fast settings
        if (CustomDistribution.showFirstSettingScreen()) {
            if (!prefProviderWrapper!!.getPreferenceBooleanValue(PreferencesWrapper.HAS_ALREADY_SETUP, false)) {
                val prefsIntent = Intent(SipManager.ACTION_UI_PREFS_FAST)
                prefsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(prefsIntent)
                return
            }
        } else {
            val doFirstParams = !prefProviderWrapper!!.getPreferenceBooleanValue(PreferencesWrapper.HAS_ALREADY_SETUP, false)
            prefProviderWrapper!!.setPreferenceBooleanValue(PreferencesWrapper.HAS_ALREADY_SETUP, true)
            if (doFirstParams) {
                prefProviderWrapper!!.resetAllDefaultValues()
            }
        }

        // If we have no account yet, open account panel,
        if (!hasTriedOnceActivateAcc) {

            val c = getContentResolver().query(SipProfile.ACCOUNT_URI, arrayOf<String>(SipProfile.FIELD_ID), null, null, null)
            var accountCount = 0
            if (c != null) {
                try {
                    accountCount = c.getCount()
                } catch (e: Exception) {
                    Log.e(THIS_FILE, "Something went wrong while retrieving the account", e)
                } finally {
                    c.close()
                }
            }

            if (accountCount == 0) {
                var accountIntent: Intent? = null
                val distribWizard = CustomDistribution.getCustomDistributionWizard()
                if (distribWizard != null) {
                    accountIntent = Intent(this, javaClass<BasePrefsWizard>())
                    accountIntent.putExtra(SipProfile.FIELD_WIZARD, distribWizard.id)
                } else {
                    accountIntent = Intent(this, javaClass<AccountActivity>())
                }

                if (accountIntent != null) {
                    accountIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(accountIntent)
                    hasTriedOnceActivateAcc = true
                    return
                }
            }
            hasTriedOnceActivateAcc = true
        }
    }

    private var onForeground = false

    override fun onPause() {
        Log.d(THIS_FILE, "On Pause SIPHOME")
        onForeground = false
        if (asyncSanityChecker != null) {
            if (asyncSanityChecker!!.isAlive()) {
                asyncSanityChecker!!.interrupt()
                asyncSanityChecker = null
            }
        }
        super.onPause()

    }

    override fun onResume() {
        Log.d(THIS_FILE, "On Resume SIPHOME")
        super.onResume()
        onForeground = true

        prefProviderWrapper!!.setPreferenceBooleanValue(PreferencesWrapper.HAS_BEEN_QUIT, false)

        // Set visible the currently selected account
        //        sendFragmentVisibilityChange(mViewPager.getCurrentItem(), true);

        Log.d(THIS_FILE, "WE CAN NOW start SIP service")
        startSipService()

    }

    private fun getVisibleLeafs(v: View): ArrayList<View> {
        val res = ArrayList<View>()
        if (v.getVisibility() != View.VISIBLE) {
            return res
        }
        if (v is ViewGroup) {
            for (i in 0..v.getChildCount() - 1) {
                val subLeafs = getVisibleLeafs(v.getChildAt(i))
                res.addAll(subLeafs)
            }
            return res
        }
        res.add(v)
        return res
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }

    private val initDialerWithText: String? = null
    var initTabId: Int? = null

    override fun onDestroy() {
        disconnect(false)
        super.onDestroy()
        Log.d(THIS_FILE, "---DESTROY SIP HOME END---")

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == CHANGE_PREFS) {
            sendBroadcast(Intent(SipManager.ACTION_SIP_REQUEST_RESTART))
            BackupWrapper.getInstance(this).dataChanged()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun disconnect(quit: Boolean) {
        Log.d(THIS_FILE, "True disconnection...")
        val intent = Intent(SipManager.ACTION_OUTGOING_UNREGISTER)
        intent.putExtra(SipManager.EXTRA_OUTGOING_ACTIVITY, ComponentName(this, javaClass<SipHome>()))
        sendBroadcast(intent)
        if (quit) {
            finish()
        }
    }

    public fun showMenu() {
        if (mHeaderGroup != null) {
            val menu_lp = mHeaderGroup!!.getLayoutParams()
            menu_lp.height = mMenuHeight
            mHeaderGroup!!.setLayoutParams(menu_lp)
        }
    }

    public fun hideMenu() {
        if (mHeaderGroup != null) {
            val menu_lp = mHeaderGroup!!.getLayoutParams()
            menu_lp.height = 0
            mHeaderGroup!!.setLayoutParams(menu_lp)
        }
    }

    public fun hideMenuWithAnimator() {
        if (mHeaderGroup != null) {

            val menu_lp = mHeaderGroup!!.getLayoutParams()
            val animator = ValueAnimator.ofInt(mMenuHeight, 0).setDuration(300)

            animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
                    menu_lp.height = valueAnimator.getAnimatedValue() as Int
                    mHeaderGroup!!.setLayoutParams(menu_lp)
                }
            })

            animator.start()
        }
    }

    public fun showMenuWithAnimator() {
        if (mHeaderGroup != null) {

            val menu_lp = mHeaderGroup!!.getLayoutParams()
            if (menu_lp.height == mMenuHeight) {
                return
            }

            val animator = ValueAnimator.ofInt(0, mMenuHeight).setDuration(300)

            animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
                    menu_lp.height = valueAnimator.getAnimatedValue() as Int
                    mHeaderGroup!!.setLayoutParams(menu_lp)

                }
            })

            animator.start()
        }
    }

    public fun showLogsDialog() {
        showDialog(DIALOG_SHOW_LOGS)
    }

    private var mLogFileChecked: BooleanArray? = null

    override fun onCreateDialog(id: Int): Dialog? {

        when (id) {

            DIALOG_SHOW_LOGS -> {

                val logPath = FileUtil.getQDMobileLogsFilePath(QDMobileApplication.getContextQD())
                val logs = File(logPath).list()
                mLogFileChecked = BooleanArray(logs.size())

                return AlertDialog.Builder(this@SipHome).setTitle("QDMobile Logs")
                        .setMultiChoiceItems(logs, BooleanArray(logs.size()), object : DialogInterface.OnMultiChoiceClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int, isChecked: Boolean) {
                        if (which < mLogFileChecked!!.size()) {
                            if (mLogFileChecked != null && mLogFileChecked is BooleanArray) {
                                (mLogFileChecked as BooleanArray)[which] = isChecked
                            }

                        }
                    }
                }).setPositiveButton("Send Email", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, whichButton: Int) {

                        if (mLogFileChecked == null || mLogFileChecked!!.size() > logs.size()) {
                            return
                        }

                        val attachments = ArrayList<String>()
                        for (i in mLogFileChecked!!.indices) {
                            val isCheck = mLogFileChecked!![i]
                            if (isCheck) {
                                attachments.add(FileUtil.getQDMobileLogsFilePath(QDMobileApplication.getContextQD()) + File.separator + logs[i])
                            }
                        }

                        if (attachments == null || attachments.isEmpty()) {
                            return
                        }

                        var emailBody = ""
                        if (attachments.size() == 1) {
                            emailBody = "The attached is QDMobile log"
                        } else {
                            emailBody = "The attached are QDMobile logs"
                        }

                        emailBody += "\n\n" + Build.BRAND + " " + android.os.Build.MODEL + " Android " + android.os.Build.VERSION.SDK_INT

                        EmailSender(this@SipHome).sendEmail(arrayOf<String>(), "QDMobile Log", emailBody, attachments.toArray<String>(arrayOf<String>()))
                    }
                }).setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, whichButton: Int) {
                    }
                }).create()
            }
        }

        return null

    }

    companion object {

        private val THIS_FILE = javaClass<SipHome>().getSimpleName()

        private val CHANGE_PREFS = 1
        private val DIALOG_SHOW_LOGS = 1
    }
}
