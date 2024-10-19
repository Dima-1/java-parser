package com.example.jcparser.attribute.stackmapframe;

import com.example.jcparser.Parser.U1;
import com.example.jcparser.Parser.U2;

public final class AppendStackMapFrame extends StackMapFrame {
	private final U2 offsetDelta;
	private final TypeInfo[] stack;

	public AppendStackMapFrame(U1 tag, U2 offsetDelta, TypeInfo[] stack) {
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
