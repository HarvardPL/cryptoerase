package accrue.cryptoerase.runtime;

import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

public final class Condition {
	private WeakHashMap<Object, Set<ErasureListener>> listeners;
	private Set<ErasureListener> staticListeners;
	
	public Condition() {
		listeners = new WeakHashMap<>();
		staticListeners = new HashSet<ErasureListener>();
	}
	
	public void register(Object parent, ErasureListener el) {
		if (!listeners.containsKey(parent)) {
			listeners.put(parent, new HashSet<ErasureListener>());
		}
		listeners.get(parent).add(el);
	}
	
	public void register(ErasureListener el) {
		staticListeners.add(el);
	}
	
	public void set() {
		for (Set<ErasureListener> els : listeners.values()) {
			for (ErasureListener el : els) {
				el.erase();
			}
		}
		for (ErasureListener el : staticListeners) {
			el.erase();
		}
	}
}
