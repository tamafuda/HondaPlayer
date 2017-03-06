package jp.co.zenrin.music.model;

/**
 * @Author: Hoang Vu
 * @Date: 2017/03/05
 */

public class ChanelRadio {
    private String idChanel;
    private String nameChanel;
    private boolean isPlay;

    public ChanelRadio(String id, String name, boolean play) {
        this.idChanel = id;
        this.nameChanel = name;
        this.isPlay = play;
    }

    public String getIdChanel() {
        return idChanel;
    }

    public void setIdChanel(String idChanel) {
        this.idChanel = idChanel;
    }

    public String getNameChanel() {
        return nameChanel;
    }

    public void setNameChanel(String nameChanel) {
        this.nameChanel = nameChanel;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }
}
