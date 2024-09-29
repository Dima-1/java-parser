package com.example.jcparser.attribute.stackmapframe;

import static com.example.jcparser.Parser.*;

public final class SameStackMapFrameExtended extends StackMapFrame {
	private final U2 offsetDelta;

	public SameStackMapFrameExtended(U1 tag, U2 offsetDelta) {
		super(tag);
		this.offsetDelta = offsetDelta;
	}

	public U2 getOffsetDelta() {
		return offsetDelta;
	}

	@Override
	public void print(StackFramePrinter stackFramePrinter) {
		super.print(stackFramePrinter);
		stackFramePrinter.print(this);
	}
}
