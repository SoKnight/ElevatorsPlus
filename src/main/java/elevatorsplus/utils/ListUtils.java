package elevatorsplus.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Material;

import elevatorsplus.objects.Elevator;

public class ListUtils {
	
	public static List<Elevator> getElevatorsOnPage(List<Elevator> list, int size, int page) {
		List<Elevator> empty = new ArrayList<>();
		if(list.isEmpty()) return empty;
		
		int start = size * (page - 1), end = size * page;
		
		if(start >= list.size()) return empty;
		if(end >= list.size()) end = list.size();
		
		List<Elevator> onpage = list.subList(start, end);
		return onpage;
	}
	
	public static Map<String, Integer> getCallbuttonsSubMap(Map<String, Integer> map, int size, int page) {
		Map<String, Integer> output = new HashMap<>();
		if(map.isEmpty()) return output;

		int start = size * (page - 1), end = size * page;
		if(start >= map.size()) return output;
		if(end >= map.size()) end = map.size();

		List<String> keys = map.keySet().stream().collect(Collectors.toList());
		for(int i = start; i < end; i++) {
			String key = keys.get(i);
			output.put(key, map.get(key));
		}

		return output;
	}
	
	public static Map<String, Material> getPlatformSubMap(Map<String, Material> map, int size, int page) {
		Map<String, Material> output = new HashMap<>();
		if(map.isEmpty()) return output;

		int start = size * (page - 1), end = size * page;
		if(start >= map.size()) return output;
		if(end >= map.size()) end = map.size();

		List<String> keys = map.keySet().stream().collect(Collectors.toList());
		for(int i = start; i < end; i++) {
			String key = keys.get(i);
			output.put(key, map.get(key));
		}

		return output;
	}
	
	public static Map<Integer, Integer> getFloorheightsSubMap(Map<Integer, Integer> map, int size, int page) {
		Map<Integer, Integer> output = new HashMap<>();
		if(map.isEmpty()) return output;

		int start = size * (page - 1), end = size * page - 1;
		if(start >= map.size()) return output;
		if(end >= map.size()) end = map.size();

		List<Integer> keys = map.keySet().stream().collect(Collectors.toList());
		for(int i = start; i < end; i++) {
			int key = keys.get(i);
			output.put(key, map.get(key));
		}

		return output;
	}
	
}
