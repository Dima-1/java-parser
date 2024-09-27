package com.example.jcparser.attribute;

import java.util.List;

import static com.example.jcparser.Parser.*;

public class StackMapTableAttribute extends Attribute {
	private final U2 numberOf;
	private final List<Entry> entries;

	public StackMapTableAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 numberOf,
	                              List<Entry> entries) {
		super(constants, nameIndex, length);
		this.numberOf = numberOf;
		this.entries = entries;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2 getNumberOf() {
		return numberOf;
	}

	public List<Entry> getEntries() {
		return entries;
	}

	public record Entry(TypeInfo typeInfo, StackMapFrame stackMapFrame) {
	}

	;

	public static final class TypeInfo {
		int typeInfo;
		U2 typeInfoAdditional;

		public TypeInfo(int typeInfo, U2 typeInfoAdditional) {
			this.typeInfo = typeInfo;
			this.typeInfoAdditional = typeInfoAdditional;
		}
	}

	public static final class StackMapFrame {
		int typeInfo;
	}
}
