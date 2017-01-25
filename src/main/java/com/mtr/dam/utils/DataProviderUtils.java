package com.mtr.dam.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DataProviderUtils {

	public static List<Object[]> combine(Iterator<Object[]> a, Iterator<Object[]> b) {
		Object[][] aObject = makeObjectArrayByIterator(a);
		Object[][] bObject = makeObjectArrayByIterator(b);
		List<Object[]> objectCodesList = new LinkedList<Object[]>();
		for (Object[] o1 : aObject) {
			for (Object[] o2 : bObject) {
				objectCodesList.add(concatAll(o1, o2));
			}
		}
		objectCodesList.toArray(new Object[0][0]);
		return objectCodesList;
	}

	private static Object[][] makeObjectArrayByIterator(Iterator<Object[]> iterator) {
		ArrayList<Object[]> list = new ArrayList<Object[]>();
		int i = 0;
		while (iterator.hasNext()) {
			list.add(iterator.next());
			i++;
		}
		Object[][] listObject = new Object[i][1];
		i = 0;
		for(Object[] o : list){
			listObject[i] = o;
			i++;
		}
		return listObject;
	}

	@SafeVarargs
	private static <T> T[] concatAll(T[] first, T[]... rest) {
		// calculate the total length of the final object array after the concat
		int totalLength = first.length;
		for (T[] array : rest) {
			totalLength += array.length;
		}
		// copy the first array to result array and then copy each array
		// completely to result
		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (T[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

}
