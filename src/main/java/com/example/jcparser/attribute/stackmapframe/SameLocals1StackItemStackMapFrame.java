package com.example.jcparser.attribute.stackmapframe;

import static com.example.jcparser.Parser.*;

public final class SameLocals1StackItemStackMapFrame extends StackMapFrame {
	private final TypeInfo[] stack;

	public SameLocals1StackItemStackMapFrame(U1 tag, TypeInfo[] stack) {
		super(tag);
		this.stack = stack;
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