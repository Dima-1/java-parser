package com.example.jcparser.attribute.stackmapframe;

import com.example.jcparser.Print;

import static com.example.jcparser.Parser.*;

public class StackMapFrame implements Print.Printable<StackFramePrinter> {
	private final U1 tag;

	public StackMapFrame(U1 tag) {
		this.tag = tag;
	}

	public U1 getTag() {
		return tag;
	}

	@Override
	public void print(StackFramePrinter stackFramePrinter) {
		stackFramePrinter.print(this);
	}
}
