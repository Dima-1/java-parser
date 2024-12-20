package com.example.jcparser.attribute;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.Print;

public class InnerClassesAttribute extends Attribute {
	private final U2 numberOf;
	private final InnerClass[] innerClasses;

	public InnerClassesAttribute(U2 nameIndex, U4 length, U2 numberOf, InnerClass[] innerClasses) {
		super(nameIndex, length);
		this.numberOf = numberOf;
		this.innerClasses = innerClasses;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2 getNumberOf() {
		return numberOf;
	}

	public InnerClass[] getInnerClasses() {
		return innerClasses;
	}

	public record InnerClass(int index, U2 innerClassInfoIndex, U2 outerClassInfoIndex, U2 innerNameIndex,
	                         U2 innerClassAccessFlags) implements Print.Printable<AttributePrinter> {

		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}
}
