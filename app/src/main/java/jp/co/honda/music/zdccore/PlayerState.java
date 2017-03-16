package jp.co.honda.music.zdccore;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/25
 */
public enum PlayerState {
    NEW("NEW"),
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
