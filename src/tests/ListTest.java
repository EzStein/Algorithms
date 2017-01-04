package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import lists.AList;


public class ListTest {
	
	@Test
	public void AListRandom() {
		AList<Integer> subject = new AList<Integer>();
		random(subject);
	}
	
	private void random(List<Integer> subject) {
		Random rand = new Random();
		ArrayList<Integer> base = new ArrayList<Integer>();
		subject.clear();
		base.clear();
		for(int i = 0; i < 1000000; i++) {
			int index = rand.nextInt(base.size() + 1);
			int val = rand.nextInt(base.size() + 1);
			int last = rand.nextInt(6);
			try {
				switch(last) {
				case 0: subject.add(val + 1); base.add(val + 1); break;
				case 1: base.add(index, val); subject.add(index, val); break;
				case 2: if(!base.contains(index)) {
							assertTrue(subject.remove(index) == base.remove(index));
							break;
						}
						assertTrue(base.remove(index).equals(subject.remove(index))); break;
				case	 3: assertTrue(base.remove((Integer) val) == subject.remove((Integer) val)); break;
				case 4: assertTrue(base.set(index, val).equals(subject.set(index, val))); break;
				case 5: base.clear(); subject.clear(); break; //THIS SHOULD NOT BE REACHED.
				default: fail(); break;
				}
			} catch(IndexOutOfBoundsException iobe) {
				
			}
			val = rand.nextInt(base.size() + 1);
			assertTrue(base.equals(subject));
			assertTrue(base.size() == subject.size());
			assertTrue(base.isEmpty() == subject.isEmpty());
			assertTrue(base.indexOf(val) == subject.indexOf(val));
			assertTrue(base.lastIndexOf(val) == subject.lastIndexOf(val));
			assertTrue(Arrays.equals(base.toArray(), subject.toArray()));
		}
	}
}
