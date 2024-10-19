package com.example.jcparser.attribute.stackmapframe;

import com.example.jcparser.Parser.U1;
import com.example.jcparser.Parser.U2;

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
