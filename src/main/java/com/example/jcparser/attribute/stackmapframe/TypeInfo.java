package com.example.jcparser.attribute.stackmapframe;

import com.example.jcparser.Parser.U1;
import com.example.jcparser.Parser.U2;

import java.util.Arrays;

public record TypeInfo(U1 tag, U2 typeInfoAdditional) {
	public enum Type {
		ITEM_Top(0),
		ITEM_Integer(1),
		ITEM_Float(2),
		ITEM_Double(3),
		ITEM_Long(4),
		ITEM_Null(5),
		ITEM_UninitializedThis(6),
		ITEM_Object(7),
		ITEM_Uninitialized(8);

		private final int tag;

		Type(int tag) {
			this.tag = tag;
		}

		public static Type getTagType(int tag) {
			return Arrays.stream(values()).filter(t -> tag == t.tag).findFirst()
					.orElseThrow(() -> new IllegalArgumentException("Unknown type info tag: " + tag));
		}
	}
}
