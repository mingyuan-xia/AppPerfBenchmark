package com.example.misuse;

import android.net.Uri;
import android.provider.BaseColumns;

public class NotePad {
    //ContentProvider的uri
    public static final String AUTHORITY = "com.example.misuse.NotePad";

    private NotePad(){}

    public static final class Notes implements BaseColumns {
        private Notes(){}

        public static final Uri     CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/notes");

        public static final String  CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.note";

        public static final String  CONTENT_ITME_TYPE  = "vnd.android.cursor.item/vnd.google.note";

        public static final String  DEFAULT_SORT_ORDER = "modified DESC";

        public static final String  TITLE              = "title";
        public static final String  NOTE               = "note";
        public static final String  CREATEDDATE        = "created";
        public static final String  MODIFIEDDATE       = "modified";
    }
}