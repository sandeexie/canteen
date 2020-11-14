package com.github.editor.kvstore;

import com.google.common.base.Preconditions;

import java.util.Arrays;

public class ArrayWrappers {

	public static Comparable<Object> forArray(Object a) {
		Preconditions.checkArgument(a.getClass().isArray());
		Comparable<?> ret;
		if (a instanceof int[]) {
			ret = new ComparableIntArray((int[]) a);
		} else if (a instanceof long[]) {
			ret = new ComparableLongArray((long[]) a);
		} else if (a instanceof byte[]) {
			ret = new ComparableByteArray((byte[]) a);
		} else {
			Preconditions.checkArgument(!a.getClass().getComponentType().isPrimitive());
			ret = new ComparableObjectArray((Object[]) a);
		}
		return (Comparable<Object>) ret;
	}

	private static class ComparableIntArray implements Comparable<ComparableIntArray> {

		private final int[] array;

		ComparableIntArray(int[] array) {
			this.array = array;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof ComparableIntArray)) {
				return false;
			}
			return Arrays.equals(array, ((ComparableIntArray) other).array);
		}

		@Override
		public int hashCode() {
			int code = 0;
			for (int i = 0; i < array.length; i++) {
				code = (code * 31) + array[i];
			}
			return code;
		}

		@Override
		public int compareTo(ComparableIntArray other) {
			int len = Math.min(array.length, other.array.length);
			for (int i = 0; i < len; i++) {
				int diff = array[i] - other.array[i];
				if (diff != 0) {
					return diff;
				}
			}

			return array.length - other.array.length;
		}
	}

	private static class ComparableLongArray implements Comparable<ComparableLongArray> {

		private final long[] array;

		ComparableLongArray(long[] array) {
			this.array = array;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof ComparableLongArray)) {
				return false;
			}
			return Arrays.equals(array, ((ComparableLongArray) other).array);
		}

		@Override
		public int hashCode() {
			int code = 0;
			for (int i = 0; i < array.length; i++) {
				code = (code * 31) + (int) array[i];
			}
			return code;
		}

		@Override
		public int compareTo(ComparableLongArray other) {
			int len = Math.min(array.length, other.array.length);
			for (int i = 0; i < len; i++) {
				long diff = array[i] - other.array[i];
				if (diff != 0) {
					return diff > 0 ? 1 : -1;
				}
			}

			return array.length - other.array.length;
		}
	}

	private static class ComparableByteArray implements Comparable<ComparableByteArray> {

		private final byte[] array;

		ComparableByteArray(byte[] array) {
			this.array = array;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof ComparableByteArray)) {
				return false;
			}
			return Arrays.equals(array, ((ComparableByteArray) other).array);
		}

		@Override
		public int hashCode() {
			int code = 0;
			for (int i = 0; i < array.length; i++) {
				code = (code * 31) + array[i];
			}
			return code;
		}

		@Override
		public int compareTo(ComparableByteArray other) {
			int len = Math.min(array.length, other.array.length);
			for (int i = 0; i < len; i++) {
				int diff = array[i] - other.array[i];
				if (diff != 0) {
					return diff;
				}
			}

			return array.length - other.array.length;
		}
	}

	private static class ComparableObjectArray implements Comparable<ComparableObjectArray> {

		private final Object[] array;

		ComparableObjectArray(Object[] array) {
			this.array = array;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof ComparableObjectArray)) {
				return false;
			}
			return Arrays.equals(array, ((ComparableObjectArray) other).array);
		}

		@Override
		public int hashCode() {
			int code = 0;
			for (int i = 0; i < array.length; i++) {
				code = (code * 31) + array[i].hashCode();
			}
			return code;
		}

		@Override
		@SuppressWarnings("unchecked")
		public int compareTo(ComparableObjectArray other) {
			int len = Math.min(array.length, other.array.length);
			for (int i = 0; i < len; i++) {
				int diff = ((Comparable<Object>) array[i]).compareTo((Comparable<Object>) other.array[i]);
				if (diff != 0) {
					return diff;
				}
			}

			return array.length - other.array.length;
		}
	}

}
