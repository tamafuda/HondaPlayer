package jp.co.honda.music.util;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

/**
 * @Author: Hoang Vu
 * @Date: 2017/03/03
 */

public class ComponentUtils {

    /**
     * Set underline for textview by programmatically
     * @param txtView
     */
    public static void setUnderlineTextView(TextView txtView) {
        String text = String.valueOf(txtView.getText());
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(),0,text.length(),0);
        txtView.setText(spannableString);
    }
}
