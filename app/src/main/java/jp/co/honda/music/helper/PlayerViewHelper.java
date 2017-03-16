package jp.co.honda.music.helper;

import android.view.View;

/**
 * @Author: Hoang Vu
 * @Date: 2017/03/05
 */
public class PlayerViewHelper {

    public static void setPlayPauseButtonVisibility(View mPlayButton, View mPauseButton, boolean isPlaying) {
        if (isPlaying) {
            mPauseButton.setVisibility(View.VISIBLE);
            mPlayButton.setVisibility(View.GONE);
        } else {
            mPlayButton.setVisibility(View.VISIBLE);
            mPauseButton.setVisibility(View.GONE);
        }

    }

}
