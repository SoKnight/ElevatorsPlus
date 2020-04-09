package elevatorsplus.command;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectionsUtil {
	
	public static <T> List<T> getSubList(List<T> list, int size, int page) {
		List<T> empty = new ArrayList<>();
		if(list.isEmpty()) return empty;
		
		int start = size * (page - 1), end = size * page;
		
		if(start >= list.size()) return empty;
		if(end >= list.size()) end = list.size();
		
		List<T> onpage = list.subList(start, end);
		return onpage;
	}
	
	public static <K, V> Map<K, V> getSubMap(Map<K, V> map, int size, int page) {
		Map<K, V> output = new LinkedHashMap<>();
		if(map.isEmpty()) return output;

		int start = size * (page - 1), end = size * page;
		if(start >= map.size()) return output;
		if(end >= map.size()) end = map.size();

		List<K> keys = map.keySet().stream().collect(Collectors.toList());
		for(int i = start; i < end; i++) {
			K key = keys.get(i);
			output.put(key, map.get(key));
		}

		return output;
	}
	
}
