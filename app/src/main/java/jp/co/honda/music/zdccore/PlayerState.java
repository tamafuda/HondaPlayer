package jp.co.honda.music.zdccore;

/**
 * Created by v_hoang on 3/7/2017.
 */

public enum PlayerState {
    PLAY("PLAY"),
    PAUSE("PAUSE"),
    NEXT("NEXT"),
    PREVIOUS("PREVIOUS");

    private String state;

    private PlayerState(String s) {
        state = s;
    }
    public String getState() {
        return state;
    }
}
