package com.qiyue.qdmobile.wizards.utils;

import android.annotation.TargetApi;
import android.net.http.SslError;
import android.os.Build;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@TargetApi(Build.VERSION_CODES.FROYO)
public class AccountCreationWebviewHelper8 extends AccountCreationWebviewHelper {

    private class CustomWebViewClient extends WebViewClient {
        private boolean bypassSSLErrors = false;
        private boolean bypassUrlChange = false;

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if (bypassSSLErrors) {
                handler.proceed();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (bypassUrlChange) {
                view.loadUrl(url);
                return false;
            }
            return true;
        }

        /**
         * @param bypassUrlChange the bypassUrlChange to set
         */
        public void setBypassUrlChange(boolean bypassUrlChange) {
            this.bypassUrlChange = bypassUrlChange;
        }

        /**
         * @param bypassSSLErrors the bypassSSLErrors to set
         */
        public void setBypassSSLErrors(boolean bypassSSLErrors) {
            this.bypassSSLErrors = bypassSSLErrors;
        }
    }

    private CustomWebViewClient mWvc = null;

    private void initWebViewClientIfNeeded(WebView webView) {
        if (mWvc == null) {
            mWvc = new CustomWebViewClient();
            webView.setWebViewClient(mWvc);
        }
    }

    public void setSSLNoSecure(WebView webView) {
        initWebViewClientIfNeeded(webView);
        mWvc.setBypassSSLErrors(true);
    }

    public void setAllowRedirect(WebView webView) {
        initWebViewClientIfNeeded(webView);
        mWvc.setBypassUrlChange(true);
    }
}
