package info.vteam.vmangaandroid.model;

/**
 * Created by YukiNoHara on 3/7/2017.
 */

public class MangaInfo {
    private String mId;
    private String mResAvatar;
    private String mTitle;
    private String mCategory;
    private String mDescription;

    public MangaInfo(String mId, String mResAvatar, String mTitle, String mCategory, String mDescription) {
        this.mId = mId;
        this.mResAvatar = mResAvatar;
        this.mTitle = mTitle;
        this.mCategory = mCategory;
        this.mDescription = mDescription;
    }

    public String getmId() {
        return mId;
    }

    public String getmResAvatar() {
        return mResAvatar;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmCategory() {
        return mCategory;
    }

    public String getmDescription() {
        return mDescription;
    }

    @Override
    public String toString() {
        return "MangaInfo{" +
                "mId='" + mId + '\'' +
                ", mResAvatar='" + mResAvatar + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mCategory='" + mCategory + '\'' +
                ", mDescription='" + mDescription + '\'' +
                '}';
    }
}
