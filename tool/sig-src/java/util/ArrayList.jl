package java.util;

public class ArrayList implements Collection {
    private Object[] objects;
    private int size;

    public ArrayList() {
	objects = new Object[1];
	size = 0;
    }

    public int indexOf(Object o) {
	for (int i = 0; i < objects.length; i++) {
	    if (objects[i].equals(o)) {
		return i;
	    }
	}
	return -1;
    }

    public int size() {
	return size;
    }

    public Object get(int i) {
	return objects[i];
    }

    public Object remove(int i) {
	Object o = objects[i];
	objects[i] = null;
	size -= 1;
	return o;
    }

    public boolean remove(Object o) {
	if (o.equals(objects[0])) {
	    remove(0);
	    return true;
	} else {
	    return false;
	}
    }

    public boolean add(Object o) {
	boolean present = o.equals(objects[0]);
	objects[0] = o;
	size += 1;
	return present;
    }

    public boolean isEmpty() {
	return size > 0;
    }

    public boolean contains(Object o) {
	return o.equals(objects[0]);
    }

    public boolean containsAll(Collection c) {
	return contains(c);
    }

    public boolean addAll(Collection c) {
	return add(c);
    }

    public boolean retainAll(Collection c) {
	return remove(c);
    }

    public boolean removeAll(Collection c) {
	return remove(c);
    }

    public void clear() {
	removeAll(this);
    }

    private class ALIterator implements Iterator {
	private ArrayList al;

	public ALIterator(ArrayList al) {
	    this.al = al;
	}

	public boolean hasNext() {
	    return al.isEmpty();
	}

	public Object next() {
	    return al.get(0);
	}

	public void remove() {
	    al.remove(0);
	}
    }

    public Object[] toArray(Object[] array) {
	for (int i = 0; i < size; i++) {
	    array[i] = objects[i];
	}
	return array;
    }

    public Object[] toArray() {
	return objects;
    }

    public Iterator iterator() {
	return new ALIterator(this);
    }
}
