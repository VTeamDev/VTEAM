package info.vteam.vmangaandroid.models;

/**
 * Created by YukiNoHara on 3/10/2017.
 */

public class Manga {
    private String mId;
    private String mResAvatar;
    private String mTitle;

    public Manga(String id, String resAvatar, String mTitle) {
        this.mId = id;
        this.mResAvatar = resAvatar;
        this.mTitle = mTitle;
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

    @Override
    public String toString() {
        return "Manga{" +
                "mId='" + mId + '\'' +
                ", mResAvatar='" + mResAvatar + '\'' +
                ", mTitle='" + mTitle + '\'' +
                '}';
    }
}
