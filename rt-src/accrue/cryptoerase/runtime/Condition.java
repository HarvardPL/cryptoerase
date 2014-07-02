package accrue.cryptoerase.runtime;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class Condition {
	private Deque<ErasureListener> listeners;
	
	public Condition() {
		listeners = new ConcurrentLinkedDeque<>();
	}
	
	public boolean register(ErasureListener el) {
		if (!listeners.contains(el)) {
			listeners.add(el);
			return true;
		} else {
			return false;
		}
	}
	
	public void set() {
		for (ErasureListener el : listeners) {
			el.erase();
		}
	}
}
