package com.example.jcparser.attribute;

import com.example.jcparser.Parser;
import com.example.jcparser.Print;

import java.util.List;

public class LocalVariableTypeTableAttribute extends LocalVariableAttribute {
	public LocalVariableTypeTableAttribute(List<Parser.ConstantPoolEntry> constantPool, Parser.U2 attributeNameIndex, Parser.U4 attributeLength, Parser.U2 numberOf, LocalVariableAttribute.LocalVariable[] localVariables) {
		super(constantPool, attributeNameIndex, attributeLength, numberOf, localVariables);
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public record LocalVariable(Parser.U2 startPC, Parser.U2 length, Parser.U2 nameIndex, Parser.U2 descriptorIndex,
	                            Parser.U2 index)
			implements Print.Printable<AttributePrinter> {

		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}
}
