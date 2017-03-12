package info.vteam.vmangaandroid.model;

/**
 * Created by YukiNoHara on 3/7/2017.
 */

public class Manga {
    private String mId;
    private String mResAvatar;
    private String mTitle;

    private String _id;

    public Manga(String id, String resAvatar, String mTitle) {
        this.mId = id;
        this.mResAvatar = resAvatar;
        this.mTitle = mTitle;
    }

    public Manga(){

    }

    public String getmId() {
        return mId;
    }

    public String getResAvatar() {
        return mResAvatar;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    @Override
    public String toString() {
        return "Manga{" +
                "mId='" + mId + '\'' +
                ", mResAvatar='" + mResAvatar + '\'' +
                ", mTitle='" + mTitle + '\'' +
                '}';
    }
}
