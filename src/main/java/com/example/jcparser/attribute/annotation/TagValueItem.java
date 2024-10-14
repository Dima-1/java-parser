package com.example.jcparser.attribute.annotation;

import java.util.Arrays;

public enum TagValueItem {
	CONST_VALUE_INDEX('B', 'C', 'D', 'F', 'I', 'J', 'S', 'Z', 's'),
	ENUM_CONST_VALUE('e'),
	CLASS_INFO_INDEX('c'),
	ANNOTATION_VALUE('@'),
	ARRAY_VALUE('[');

	final int[] tags;

	TagValueItem(int... tags) {
		this.tags = tags;
	}

	public static TagValueItem getTagValue(int tag) {
		return Arrays.stream(values()).filter(v -> Arrays.stream(v.tags).anyMatch(e -> tag == e))
				.findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown tag: " + tag));
	}
}
