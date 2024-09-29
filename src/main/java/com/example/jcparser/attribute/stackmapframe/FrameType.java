package com.example.jcparser.attribute.stackmapframe;

import com.example.jcparser.attribute.StackMapTableAttribute;

import java.util.Arrays;

public enum FrameType {
	SAME(0, 63),
	SAME_LOCALS_1_STACK_ITEM(64, 127),
	SAME_LOCALS_1_STACK_ITEM_EXTENDED(247, 247),
	CHOP(248, 250),
	SAME_FRAME_EXTENDED(251, 251),
	APPEND(252, 254),
	FULL_FRAME(255, 255);

	private final int min;
	private final int max;

	FrameType(int min, int max) {
		this.min = min;
		this.max = max;
	}

	public static FrameType getType(int tag) {
		return Arrays.stream(values()).filter(v -> v.min <= tag && tag <= v.max).findFirst().orElseThrow(() ->
				new IllegalArgumentException("Unknown frame type tag: " + tag));
	}
}
