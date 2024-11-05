package com.example.jcparser.attribute.stackmapframe;

import com.example.jcparser.Parser;
import com.example.jcparser.Print;

public class StackFramePrinter {
	private final Print print;

	public StackFramePrinter(Print print) {
		this.print = print;
	}

	public void print(StackMapFrame frame) {
		Parser.U1 tag = frame.getTag();
		print.u1(tag, "StackMapFrame type tag", true, FrameType.getType(tag.getValue()).name().toLowerCase());
	}

	public void print(SameLocals1StackItemStackMapFrame frame) {
		printTypeInfo(frame.getStack());
	}

	public void print(SameLocals1StackItemStackMapFrameExtended frame) {
		print.u2(frame.getOffsetDelta(), "offset delta", true);
		printTypeInfo(frame.getStack());
	}

	public void print(ChopStackMapFrame frame) {
		print.u2(frame.getOffsetDelta(), "offset delta", true);
	}

	public void print(SameStackMapFrameExtended frame) {
		print.u2(frame.getOffsetDelta(), "offset delta", true);
	}

	public void print(AppendStackMapFrame frame) {
		print.u2(frame.getOffsetDelta(), "offset delta", true);
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
		print.incIndent();
		for (TypeInfo typeInfo : stack) {
			int value = typeInfo.tag().getValue();
			TypeInfo.Type tagType = TypeInfo.Type.getTagType(value);
			print.u1(typeInfo.tag(), "Type info tag", true, tagType.name());
			if (typeInfo.typeInfoAdditional() != null) {
				if (tagType == TypeInfo.Type.ITEM_Object) {
					print.u2(typeInfo.typeInfoAdditional(), "class");
				} else if (tagType == TypeInfo.Type.ITEM_Uninitialized) {
					print.u2(typeInfo.typeInfoAdditional(), "Offset", true);
				}
			}
		}
		print.decIndent();
	}
}
