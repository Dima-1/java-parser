package com.example.jcparser.attribute;

import java.util.List;

import com.example.jcparser.Parser.*;

public class ModulePackagesAttribute extends Attribute {
	private final U2Array packages;

	public ModulePackagesAttribute(List<ConstantPoolEntry> constantPool, U2 attributeNameIndex, U4 attributeLength, 
	                               U2Array packages) {
		super(constantPool, attributeNameIndex, attributeLength);
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
