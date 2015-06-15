package com.qiyue.qdmobile.zxing.result;

import android.app.Activity;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.qiyue.qdmobile.R;

/**
 * This class handles TextParsedResult as well as unknown formats. It's the fallback handler.
 */
public final class TextResultHandler extends ResultHandler {

  private static final int[] buttons = {
      R.string.button_web_search,
      R.string.button_share_by_email,
      R.string.button_share_by_sms,
      R.string.button_custom_product_search,
  };

  public TextResultHandler(Activity activity, ParsedResult result, Result rawResult) {
    super(activity, result, rawResult);
  }

  @Override
  public int getButtonCount() {
    return hasCustomProductSearch() ? buttons.length : buttons.length - 1;
  }

  @Override
  public int getButtonText(int index) {
    return buttons[index];
  }

  @Override
  public void handleButtonPress(int index) {
    String text = getResult().getDisplayResult();
    switch (index) {
      case 0:
        webSearch(text);
        break;
      case 1:
        shareByEmail(text);
        break;
      case 2:
        shareBySMS(text);
        break;
      case 3:
        openURL(fillInCustomSearchURL(text));
        break;
    }
  }

  @Override
  public int getDisplayTitle() {
    return R.string.result_text;
  }
}
