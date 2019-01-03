package net.imoran.auto.music.utils;

public class ClickUtils {
    private static long lastClickTime = 0;
    private static long DIFF = 500;

    /**
     * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
     *
     * @return
     */
    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (lastClickTime > 0 && time - lastClickTime < DIFF) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
