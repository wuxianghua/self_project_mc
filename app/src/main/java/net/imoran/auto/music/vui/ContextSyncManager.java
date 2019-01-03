package net.imoran.auto.music.vui;

public class ContextSyncManager {
    public static final String PAGE_ID_NET_MUSIC_PLAY = "PAGE_ID_NET_MUSIC_PLAY";
    public static final String PAGE_ID_NET_MUSIC_SEARCH = "PAGE_ID_NET_MUSIC_SEARCH";
    public static final String PAGE_ID_NET_MUSIC_COLLECT = "PAGE_ID_NET_MUSIC_COLLECT";
    public static final String PAGE_ID_NET_MUSIC_LIB = "PAGE_ID_NET_MUSIC_LIB";


    private static ContextSyncManager instant;
    private String currentListQueryId;

    public static ContextSyncManager getInstant() {
        if (instant == null) {
            synchronized (ContextSyncManager.class) {
                if (instant == null) {
                    instant = new ContextSyncManager();
                }
            }
        }
        return instant;
    }

    private ContextSyncManager() {

    }

    public String getCurrentListQueryId() {
        return currentListQueryId;
    }

    public void setCurrentListQueryId(String currentListQueryId) {
        this.currentListQueryId = currentListQueryId;
    }

}
