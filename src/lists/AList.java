package lists;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A list implementation using a resizable array.
 * @author ezrastein
 * @param <V> The type stored in this list.
 *
 */
public class AList<V> implements List<V> {

	private static int INIT_CAPACITY = 8;
	private Object[] array;
	private int size;
	private long version;
	
	public AList() {
		array = new Object[INIT_CAPACITY];
		size = 0;
		version = Long.MIN_VALUE;
	}
	
	/**
	 * Resizes the array by copying all elements into a new array of {@code newSize}.
	 * @param newSize The new size of the array which must be greater than or equal to {@code size}.
	 */
	private void resize(int newSize) {
		if(newSize < INIT_CAPACITY) return;
		
		Object[] tmp = new Object[newSize];
		for(int i = 0; i < size; i++) {
			tmp[i] = array[i];
		}
		array = tmp;
	}
	
	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean contains(Object o) {
		for(int i = 0; i < size; i++) {
			if(array[i].equals(o)) return true;
		}
		return false;
	}

	

	@Override
	public Object[] toArray() {
		Object[] returned = new Object[size];
		for(int i = 0; i < size; i++) {
			returned[i] = array[i];
		}
		return returned;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if(a.length < size)
			a = (T[]) new Object[size];
		
		for(int i = 0; i < size; i++)
			a[i] = (T) array[i];
		
		if(a.length > size)
			a[size] = null;
		
		return a;
	}

	@Override
	public boolean add(V e) {
		
		if(size == array.length)
			resize(2*array.length);
		array[size] = e;
		
		size++;
		version++;
		return true;
	}

	@Override
	public boolean remove(Object o) {
		int i;
		for(i = 0; i < size; i++) {
			if(array[i].equals(o)) break;
		}
		if(i == size) return false;
		size--;
		for(; i < size; i++) {
			array[i] = array[i+1];
		}
		if(array.length/2 >= size) {
			resize(array.length/2);
		}
		version++;
		return true;
	}
	
	@Override
	public void clear() {
		size = 0;
		array = new Object[INIT_CAPACITY];
		version++;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(int index) {
		if(index < 0 || index >= size)
			throw new IndexOutOfBoundsException();
		return (V) array[index];
	}

	@SuppressWarnings("unchecked")
	@Override
	public V set(int index, V element) {
		if(index < 0 || index >= size)
			throw new IndexOutOfBoundsException();
		V old = (V) array[index];
		array[index] = element;
		version++;
		return old;
	}

	@Override
	public void add(int index, V element) {
		if(index == size) {
			add(element);
			return;
		}
		
		if(index < 0 || index > size)
			throw new IndexOutOfBoundsException();
		
		if(size == array.length)
			resize(2 * array.length);
		size++;
		for(int i = size - 1; i > index; i--) {
			array[i] = array[i - 1];
		}
		array[index] = element;
		version++;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(int index) {
		if(index < 0 || index >= size)
			throw new IndexOutOfBoundsException();
		
		V old = (V) array[index];
		size--;
		for(int i = index; i < size; i++) {
			array[i] = array[i + 1];
		}
		
		if(array.length/2 >= size) {
			resize(array.length/2);
		}
		version++;
		return old;
	}

	@Override
	public int indexOf(Object o) {
		int index;
		for(index = 0; index < size; index++) {
			if(array[index].equals(o)) return index;
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		int index;
		for(index = size - 1; index >= 0; index--) {
			if(array[index].equals(o)) return index;
		}
		return -1;
	}

	@Override
	public ListIterator<V> listIterator() {
		return new AListIterator<V>(0, version, this);
	}

	@Override
	public ListIterator<V> listIterator(int index) {
		return new AListIterator<V>(index, version, this);
	}
	
	@Override
	public Iterator<V> iterator() {
		return new AListIterator<V>(0, version, this);
	}
	
	private static class AListIterator<V> implements ListIterator<V> {
		private int index;
		private long version;
		private AList<V> list;
		
		private AListIterator(int index, long version, AList<V> list) {
			this.index = index;
			this.version = version;
			this.list = list;
		}
		
		private void checkValid() {
			if(version != list.version)
				throw new ConcurrentModificationException();
		}
		
		@Override
		public boolean hasNext() {
			checkValid();
			return (index >= 0 && index < list.size);
		}

		@SuppressWarnings("unchecked")
		@Override
		public V next() {
			checkValid();
			if(!hasNext()) throw new NoSuchElementException();
			V val = (V) list.array[index];
			index++;
			return val;
		}

		@Override
		public boolean hasPrevious() {
			checkValid();
			return (index > 0 && index <= list.size);
		}

		@SuppressWarnings("unchecked")
		@Override
		public V previous() {
			checkValid();
			if(!hasPrevious()) throw new NoSuchElementException();
			V val = (V) list.array[index - 1];
			index--;
			return val;
		}

		@Override
		public int nextIndex() {
			checkValid();
			return index;
		}

		@Override
		public int previousIndex() {
			checkValid();
			return index - 1;
		}

		@Override
		public void remove() {
			checkValid();
			// TODO Auto-generated method stub
			
		}

		@Override
		public void set(V e) {
			checkValid();
			// TODO Auto-generated method stub
			
		}

		@Override
		public void add(V e) {
			checkValid();
			// TODO Auto-generated method stub
			
		}
		
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends V> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends V> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<V> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}

}
