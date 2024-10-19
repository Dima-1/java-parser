package com.example.jcparser.attribute;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U2Array;
import com.example.jcparser.Parser.U4;

public class ModulePackagesAttribute extends Attribute {
	private final U2Array packages;

	public ModulePackagesAttribute(U2 attributeNameIndex, U4 attributeLength, U2Array packages) {
		super(attributeNameIndex, attributeLength);
		this.packages = packages;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2Array getPackages() {
		return packages;
	}
}
