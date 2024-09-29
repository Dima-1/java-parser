package com.example.jcparser.attribute.stackmapframe;

import static com.example.jcparser.Parser.*;

public final class SameLocals1StackItemStackMapFrameExtended extends StackMapFrame {
	private final U2 offsetDelta;
	private final TypeInfo[] stack;

	public SameLocals1StackItemStackMapFrameExtended(U1 tag, U2 offsetDelta, TypeInfo[] stack) {
		super(tag);
		this.offsetDelta = offsetDelta;
		this.stack = stack;
	}

	public U2 getOffsetDelta() {
		return offsetDelta;
	}

	public TypeInfo[] getStack() {
		return stack;
	}

	@Override
	public void print(StackFramePrinter stackFramePrinter) {
		super.print(stackFramePrinter);
		stackFramePrinter.print(this);
	}
}
