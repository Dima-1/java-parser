package com.example.jcparser.attribute;

import com.example.jcparser.Print;

import java.util.List;

import static com.example.jcparser.Parser.*;

public class LineNumberTableAttribute extends Attribute {
	private final U2 numberOf;
	private final LineNumber[] lineNumberTable;

	public LineNumberTableAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 numberOf, 
	                                LineNumber[] lineNumberTable) {
		super(constants, nameIndex, length);
		this.numberOf = numberOf;
		this.lineNumberTable = lineNumberTable;
	}

	public U2 getNumberOf() {
		return numberOf;
	}

	public LineNumber[] getLineNumberTable() {
		return lineNumberTable;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public record LineNumber(U2 startPC, U2 lineNumber) implements Print.Printable<AttributePrinter> {
		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}
}
