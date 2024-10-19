package com.example.jcparser.attribute.stackmapframe;

import com.example.jcparser.Parser.U1;
import com.example.jcparser.Parser.U2;

public final class FullStackMapFrame extends StackMapFrame {
	private final U2 offsetDelta;
	private final U2 numberOfLocals;
	private final TypeInfo[] locals;
	private final U2 numberOfStack;
	private final TypeInfo[] stack;

	public FullStackMapFrame(U1 tag, U2 offsetDelta, U2 numberOfLocals, TypeInfo[] locals, U2 numberOfStack, TypeInfo[] stack) {
		super(tag);
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

	@Override
	public void print(StackFramePrinter stackFramePrinter) {
		super.print(stackFramePrinter);
		stackFramePrinter.print(this);
	}
}
