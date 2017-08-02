package EigenMeat.EigenMaze;

import java.util.*;

/**
 * A collection that maps an arbitrary short to an Object.
 */
public class ShortMap {
	
	private class Entry {
		private short key;
		private Object object;
		
		public Entry() {
			key = 0;
			object = null;
		}
		
		public Entry(short k, Object o) {
			key = k;
			object = o;
		}
		
		public short getKey() {
			return key;
		}
		
		public Object getObject() {
			return object;
		}
	}
	
	private List theList;
	
	public ShortMap() {
		theList = new LinkedList();
	}

	/**
	 * Adds a key and Object pair to the collection. If the key given already exists, that
	 * key-object pair will be overwritten with the new object.
	 * @param key the short to be used as a unique key.
	 * @param obj the object to be associated with the key.
	 * @return the object just added.
	 */
	public Object put(short key, Object obj) {
		Object result = get(key);
		theList.add(new Entry(key, obj));
		return result;
	}
	
	/**
	 * Gets the object associated with a unique short key.
	 * @param key the key.
	 * @return the object associated with the key given; or null if the given key is not stored.
	 */
	public Object get(short key) {
		Iterator iter = iterator();
		Entry e = null;
		for (; iter.hasNext(); e=(Entry)iter.next())
			if (e.getKey()==key)
				return e.getObject();
		return null;
	}
	
	/**
	 * Gets an iterator for this collection. No particular order is guaranteed.
	 * @return an iterator for this collection.
	 */
	public Iterator iterator() {
		return theList.listIterator(0);
	}
	
	/**
	 * Removes a key-object pair from this collection.
	 * @param key the unique short key.
	 * @return the object removed from the collection.
	 */
	public Object remove(short key) {
		Object result = get(key);
		theList.remove(result);
		return result;
	}
}
