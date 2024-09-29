package com.example.jcparser.attribute.stackmapframe;

import com.example.jcparser.Print;

public class StackFramePrinter {
	private final Print print;

	public StackFramePrinter(Print print) {
		this.print = print;
	}

	public void print(StackMapFrame frame) {
		print.u1(frame.getTag(), "StackMapFrame type tag");
	}

	public void print(SameLocals1StackItemStackMapFrame frame) {
		printTypeInfo(frame.getStack());
	}

	public void print(SameLocals1StackItemStackMapFrameExtended frame) {
		print.u2(frame.getOffsetDelta(), "offset delta");
		printTypeInfo(frame.getStack());
	}

	public void print(ChopStackMapFrame frame) {
		print.u2(frame.getOffsetDelta(), "offset delta");
	}

	public void print(SameStackMapFrameExtended frame) {
		print.u2(frame.getOffsetDelta(), "offset delta");
	}

	public void print(AppendStackMapFrame frame) {
		print.u2(frame.getOffsetDelta(), "offset delta");
		printTypeInfo(frame.getStack());
	}

	public void print(FullStackMapFrame frame) {
		print.u2(frame.getOffsetDelta(), "offset delta");
		print.u2(frame.getNumberOfLocals(), "Number of local");
		printTypeInfo(frame.getLocals());
		print.u2(frame.getNumberOfStack(), "Number of stack");
		printTypeInfo(frame.getStack());
	}

	private void printTypeInfo(TypeInfo[] stack) {
		for (TypeInfo typeInfo : stack) {
			print.u1(typeInfo.tag(), "Type info tag");
			if (typeInfo.typeInfoAdditional() != null) {
				print.u2(typeInfo.typeInfoAdditional(), "Offset");
			}
		}
	}
}
