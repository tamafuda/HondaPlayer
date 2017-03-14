package jp.co.honda.music.common;

/**
 * @Author: Hoang Vu
 * @Date:   2017/02/23
 */

public final class HondaConstants {

    public static final int PERMISSION_REQUEST_CODE = 1;
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    // Save preference define constant
    public static final String PREFERENCE_MEDIA_PLAYER = "jp.co.honda.music.HondaConstants.MEDIA_PLAYER";
    public static final String PREFERENCE_TRACK_LIST = "jp.co.honda.music.HondaConstants.TRACK_ARRAY_LIST";
    public static final String PREFERENCE_TRACK_INDEX = "jp.co.honda.music.HondaConstants.TRACK_INDEX";
    public static final String PREFERENCE_MPL_SERVICE_STATUS = "MPL_SERVICE_STATUS";
    public static final String PREFERENCE_SPINNER_SELECT_STATUS = "jp.co.honda.music.HondaConstants.PREFERENCE_SPINNER_SELECT_STATUS";

    //
    public static final String BROADCAST_UNBIND_SERVICE = "jp.co.honda.music.HondaConstants.UNBIND";
    public static final String BROADCAST_AI_RECOMMEND = "jp.co.honda.music.HondaConstants.BROADCAST_AI_RECOMMEND";
    public static final String BROADCAST_PLAY_NEW_TRACK = "jp.co.honda.music.HondaConstants.PLAY_NEW_TRACK";
    public static final String BROADCAST_PLAY_RESTORE_TRACK = "jp.co.honda.music.HondaConstants.PLAY_RESTORE_TRACK";
    public static final String BROADCAST_PLAY_NEXT_TRACK = "jp.co.honda.music.HondaConstants.BROADCAST_PLAY_NEXT_TRACK";
    public static final String BROADCAST_PLAY_PREVIOUS_TRACK = "jp.co.honda.music.HondaConstants.BROADCAST_PLAY_PREVIOUS_TRACK";
    public static final String BROADCAST_PLAY_STOP_TRACK = "jp.co.honda.music.HondaConstants.BROADCAST_PLAY_STOP_TRACK";
    public static final int WAIT_LENGTH = 5000;
    public static final int WAIT_CLOSE_LENGTH = 5000;

    public static final String DETECTED_SCREEN_FLING = "jp.co.honda.music.HondaConstants.DETECTED_SCREEN_FLING";

    public static final int FLING_RESULT_CODE = 0;

    public static final String INTENT_AIMIXAUDIO = "jp.co.honda.music.HondaConstants.AIMixAudio";
    public static final String INTENT_NOTIFY_TO_MUSICPLAY_SRC = "jp.co.honda.music.HondaConstants.MusicPlayActivity";
    public static final String INTENT_TEST_FRAGMENT = "jp.co.honda.music.HondaConstants.HomeBaseFragment";
    public static final String INTENT_AFFM_FRAGMENT = "jp.co.honda.music.HondaConstants.AMFMFragment";
    public static final String INTENT_INTERNET_RADIO_FRAGMENT = "jp.co.honda.music.HondaConstants.InternetRadioFragment";
    public static final String INTENT_IPOD_FRAGMENT = "jp.co.honda.music.HondaConstants.IPodFragment";
    public static final String INTENT_BASE = "jp.co.honda.music.HondaConstants.BasePlayerActivity";

    // NOTIFICATION
    public static final int NOTIFICATIONS_INTERVAL_IN_MINUTE = 15;
    public static final int AUDIO_FOCUS_CHANGE = 1987;


    // Define key to detect screen
    public static final String DETECTED_SCREEN_CAPSUL = "jp.co.honda.music.player.RadarMusicActivity";
    public static final String DETECTED_SCREEN_ARRANGE = "jp.co.honda.music.player.AIMixAudio";
    public static final String DETECT_FRAGMENT_IPOD = "jp.co.honda.music.fragment.IPodFragment";
    public static final String DETECT_FRAGMENT_FMAM = "jp.co.honda.music.fragment.AMFMFragment";
    public static final String DETECT_FRAGMENT_NETRADIO = "jp.co.honda.music.fragment.InternetRadioFragment";
    public static final String DETECT_FRAGMENT_BLUETOOTH = "jp.co.honda.music.fragment.BluetoothFragment";



}
