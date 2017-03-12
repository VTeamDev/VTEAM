package info.vteam.vmangaandroid.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by YukiNoHara on 3/8/2017.
 */

public class MangaContract {
    public static final String AUTHORITY = "info.vteam.vmangaandroid";

    public static final Uri BASE_URI_CONTENT = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MANGA = "manga";
    public static final String PATH_MANGA_INFO = "manga_info";
    public static final String PATH_MANGA_SEARCH = "manga_search";
    public static final String PATH_MANGA_INFO_RECENT = "manga_recent";

    public static final class MangaEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_URI_CONTENT.buildUpon()
                .appendPath(PATH_MANGA).build();

        public static final String TABLE_NAME = "manga";
        public static final String COLUMN_MANGA_ID = "manga_id";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
        public static final String COLUMN_TITLE = "title";
    }

    public static final class MangaSearchEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_URI_CONTENT.buildUpon()
                .appendPath(PATH_MANGA_SEARCH).build();

        public static final String TABLE_NAME = "manga_search";
        public static final String COLUMN_MANGA_ID = "manga_id";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
        public static final String COLUMN_TITLE = "title";
    }

    public static final class MangaInfoEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_URI_CONTENT.buildUpon()
                .appendPath(PATH_MANGA_INFO).build();

        public static final String TABLE_NAME = "manga_info";
        public static final String COLUMN_MANGAINFO_ID = "manga_id";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CATEROGY = "category";
        public static final String COLUMN_DESCRIPTION = "description";
//        public static final String COLUMN_TYPE = "type";
    }

    public static final class MangaInfoRecentEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_URI_CONTENT.buildUpon()
                .appendPath(PATH_MANGA_INFO_RECENT).build();

        public static final String TABLE_NAME = "manga_recent";
        public static final String COLUMN_MANGAINFO_ID = "manga_id";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CATEROGY = "category";
        public static final String COLUMN_DESCRIPTION = "description";
    }
}
