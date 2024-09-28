package com.example.jcparser.attribute;

import java.util.Arrays;
import java.util.List;

import static com.example.jcparser.Parser.*;

public class StackMapTableAttribute extends Attribute {
	private final U2 numberOf;
	private final List<StackMapFrame> entries;

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

	public StackMapTableAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 numberOf,
	                              List<StackMapFrame> entries) {
		super(constants, nameIndex, length);
		this.numberOf = numberOf;
		this.entries = entries;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		//	printer.print(this);
	}

	public U2 getNumberOf() {
		return numberOf;
	}

	public List<StackMapFrame> getEntries() {
		return entries;
	}


	public record TypeInfo(int typeInfo, U2 typeInfoAdditional) {
	}

	public static class StackMapFrame {
	}

	public static final class SameLocals1StackItemStackMapFrame extends StackMapFrame {
		private final TypeInfo stack;

		public SameLocals1StackItemStackMapFrame(TypeInfo stack) {
			this.stack = stack;
		}

		public TypeInfo getStack() {
			return stack;
		}
	}

	public static final class SameLocals1StackItemStackMapFrameExtended extends StackMapFrame {
		private final U2 offsetDelta;
		private final TypeInfo stack;

		public SameLocals1StackItemStackMapFrameExtended(U2 offsetDelta, TypeInfo stack) {
			this.offsetDelta = offsetDelta;
			this.stack = stack;
		}

		public U2 getOffsetDelta() {
			return offsetDelta;
		}

		public TypeInfo getStack() {
			return stack;
		}
	}

	public static final class ChopStackMapFrame extends StackMapFrame {
		private final U2 offsetDelta;

		public ChopStackMapFrame(U2 offsetDelta) {
			this.offsetDelta = offsetDelta;
		}

		public U2 getOffsetDelta() {
			return offsetDelta;
		}
	}

	public static final class SameStackMapFrameExtended extends StackMapFrame {
		private final U2 offsetDelta;

		public SameStackMapFrameExtended(U2 offsetDelta) {
			this.offsetDelta = offsetDelta;
		}

		public U2 getOffsetDelta() {
			return offsetDelta;
		}
	}

	public static final class AppendStackMapFrame extends StackMapFrame {
		private final U2 offsetDelta;
		private final TypeInfo[] stack;

		public AppendStackMapFrame(U2 offsetDelta, TypeInfo[] stack) {
			this.offsetDelta = offsetDelta;
			this.stack = stack;
		}

		public U2 getOffsetDelta() {
			return offsetDelta;
		}

		public TypeInfo[] getStack() {
			return stack;
		}
	}

	public static final class FullStackMapFrame extends StackMapFrame {
		private final U2 offsetDelta;
		private final U2 numberOfLocals;
		private final TypeInfo[] locals;
		private final U2 numberOfStack;
		private final TypeInfo[] stack;

		public FullStackMapFrame(U2 offsetDelta, U2 numberOfLocals, TypeInfo[] locals, U2 numberOfStack, TypeInfo[] stack) {
			this.offsetDelta = offsetDelta;
			this.numberOfLocals = numberOfLocals;
			this.locals = locals;
			this.numberOfStack = numberOfStack;
			this.stack = stack;
		}

		public U2 getOffsetDelta() {
			return offsetDelta;
		}

		public U2 getNumberOfLocals() {
			return numberOfLocals;
		}

		public TypeInfo[] getLocals() {
			return locals;
		}

		public U2 getNumberOfStack() {
			return numberOfStack;
		}

		public TypeInfo[] getStack() {
			return stack;
		}
	}
}
