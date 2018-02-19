package com.barrett.evaluator;

public class Array {

	private int minIndex;
	private int maxIndex;
	private Value[] valueArray;

	public Array(int min, int max) {
		if (min > max) {
			System.err.println("Array declared with improper indices");
			System.exit(1);
		}
		valueArray = new Value[max-min+1];
		minIndex = min;
		maxIndex = max;
	}

	public boolean indexInRange(int index) {
		return index >= minIndex && index <= maxIndex;
	}

	public void update(int index, Value val) {
		if (indexInRange(index)) {
			valueArray[index - minIndex] = val;
		} else {
			System.err.println("Array Error: Attempted to access out of bounds index");
			System.exit(1);
		}
	}

	public Value get(int index) {
		if (indexInRange(index)) {
			return valueArray[index-minIndex];
		} else {
			System.err.println("Array Error: Attempted to access out of bounds index");
			System.exit(1);
			return null;
		}
	}

}
