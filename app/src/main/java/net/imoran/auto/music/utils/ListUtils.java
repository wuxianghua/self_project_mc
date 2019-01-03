package net.imoran.auto.music.utils;

import net.imoran.auto.music.player.model.SongModel;

import java.util.Iterator;
import java.util.List;

public class ListUtils {

    private ListUtils() {

    }

    public static boolean isNotEmpty(List list) {
        if (list == null || list.size() == 0) return false;
        else return true;
    }

    public static boolean isEmpty(List list) {
        if (list == null || list.size() == 0) return true;
        else return false;
    }

    public static boolean hasEmpty(List list) {
        if (list == null) {
            return true;
        } else {
            boolean isEmpty = false;
            Iterator var2 = list.iterator();

            while (var2.hasNext()) {
                Object o = var2.next();
                if (o == null || o instanceof String && ((String) o).trim().equals("")) {
                    isEmpty = true;
                    break;
                }
            }

            return isEmpty;
        }
    }

    public static boolean isSameSongModelList(List<SongModel> firstList, List<SongModel> secList) {
        if (!isNotEmpty(firstList)) return false;
        if (!isNotEmpty(secList)) return false;
        if (firstList.size() != secList.size()) return false;
        for (int i = 0; i < firstList.size(); i++) {
            if (firstList.get(i).getUuid() != secList.get(i).getUuid()) {
                return false;
            }
        }
        return true;
    }
}
