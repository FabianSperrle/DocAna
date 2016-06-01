package pos;

import java.util.HashMap;
import java.util.Map;

public class MapUpdaterHelper {
    public static void updateMap(Map<String, Map<String, Integer>> largeMap, String outer, String inner) {
        if (largeMap.containsKey(outer)) {
            Map<String, Integer> map = largeMap.get(outer);
            if (map.containsKey(inner)) {
                map.put(inner, map.get(inner) + 1);

            } else {
                map.put(inner, 1);
            }
        } else {
            Map<String, Integer> map = new HashMap<>();
            map.put(inner, 1);
            largeMap.put(outer, map);
        }
    }
}
