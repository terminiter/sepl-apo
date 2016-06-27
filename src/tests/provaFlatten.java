package tests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class provaFlatten {

	public static void main(String[] args) {
		
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();

		map.put("index",  new HashSet<String>(Arrays.asList("state15")));
		map.put("state2", new HashSet<String>(Arrays.asList("state3", "state14", "state20")));
		map.put("state6", new HashSet<String>(Arrays.asList("state7")));
		map.put("state7", new HashSet<String>(Arrays.asList("state8")));
		map.put("state8", new HashSet<String>(Arrays.asList("state9")));
		map.put("state9", new HashSet<String>(Arrays.asList("state10")));
		// TODO: check cycles later
		//map.put("state10", Arrays.asList("state6"));
		
		System.out.println("*** Initial Graph ***");
		System.out.println(map.toString()+"\n");
		
		map = transitiveClosure(map);
		
		System.out.print("*** Transitive Closure ***");
		System.out.println("\n"+map.toString()+"\n");
	
		map = removeDuplicates(map);
		
		System.out.print("*** Duplicates Removal ***");
		System.out.println("\n"+map.toString());
		
	}

	private static Map<String, Set<String>> removeDuplicates(
			Map<String, Set<String>> map) {
		
		Map<String, Set<String>> aNewMap = new HashMap<String, Set<String>>();
		Set<String> values = new HashSet<String>();
		Set<String> toRemove = new HashSet<String>();
		
		for (String key : map.keySet()) {
			
			values = map.get(key);
			
			for (String string : values) {
				if(map.containsKey(string)) {
					toRemove.add(string);
				}
			}		
		}
		
		aNewMap = map;
	
		for (String tr : toRemove) {
			aNewMap.remove(tr);
		}
	
		
		
		return aNewMap;
		
	}

	private static Map<String, Set<String>> transitiveClosure(Map<String, Set<String>> map) {
		
		Map<String, Set<String>> aNewMap = new HashMap<String, Set<String>>();
		Set<String> c = new HashSet<String>();
		
		for (String key : map.keySet()) {
			c = getClosure(map, key, new HashSet<String>());
			aNewMap.put(key, c);			
		}
		
		return aNewMap;
		
	}

	private static Set<String> getClosure(Map<String, Set<String>> map, String key, Set<String> v) {
		
		if(v.contains(key) || !map.containsKey(key)){
			return new HashSet<String>();
		} 

		Set<String> result = new HashSet<String>();
		v.add(key);
		
		for (String slave : map.get(key)) {
			result.addAll(getClosure(map, slave, v));
			result.add(slave);
		}

		return result;
		
	}

}
