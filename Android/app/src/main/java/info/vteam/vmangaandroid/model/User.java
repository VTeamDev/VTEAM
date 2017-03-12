package info.vteam.vmangaandroid.model;

/**
 * Created by YukiNoHara on 3/12/2017.
 */

public class User {
    private String _id;
    private String mFacebookId;
    private String mUserName;
    private String mEmail;
    private String mAvatar;
    private String mTitleReading;

    public User(String mFacebookId, String mUserName, String mEmail, String mAvatar, String titleReading) {
        this.mFacebookId = mFacebookId;
        this.mUserName = mUserName;
        this.mEmail = mEmail;
        this.mAvatar = mAvatar;
        this.mTitleReading = titleReading;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getmFacebookId() {
        return mFacebookId;
    }

    public void setmFacebookId(String mFacebookId) {
        this.mFacebookId = mFacebookId;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmAvatar() {
        return mAvatar;
    }

    public void setmAvatar(String mAvatar) {
        this.mAvatar = mAvatar;
    }

    public String getmTitleReading() {
        return mTitleReading;
    }
}
