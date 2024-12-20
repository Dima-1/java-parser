package com.example.jcparser.attribute;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U2Array;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.Print;

public class ModuleAttribute extends Attribute {
	private final U2 moduleNameIndex;
	private final U2 moduleFlags;
	private final U2 moduleVersionIndex;
	private final U2 requiresCount;
	private final Requires[] requires;
	private final U2 exportsCount;
	private final Exports[] exports;
	private final U2 opensCount;
	private final Opens[] opens;
	private final U2Array uses;
	private final U2 providesCount;
	private final Provides[] provides;

	public ModuleAttribute(U2 attributeNameIndex, U4 attributeLength, U2 moduleNameIndex, U2 moduleFlags,
	                       U2 moduleVersionIndex, U2 requiresCount, Requires[] requires, U2 exportsCount, Exports[] exports,
	                       U2 opensCount, Opens[] opens, U2Array uses, U2 providesCount, Provides[] provides) {
		super(attributeNameIndex, attributeLength);
		this.moduleNameIndex = moduleNameIndex;
		this.moduleFlags = moduleFlags;
		this.moduleVersionIndex = moduleVersionIndex;
		this.requiresCount = requiresCount;
		this.requires = requires;
		this.exportsCount = exportsCount;
		this.exports = exports;
		this.opensCount = opensCount;
		this.opens = opens;
		this.uses = uses;
		this.providesCount = providesCount;
		this.provides = provides;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2 getModuleNameIndex() {
		return moduleNameIndex;
	}

	public U2 getModuleFlags() {
		return moduleFlags;
	}

	public U2 getModuleVersionIndex() {
		return moduleVersionIndex;
	}

	public U2 getRequiresCount() {
		return requiresCount;
	}

	public Requires[] getRequires() {
		return requires;
	}

	public U2 getExportsCount() {
		return exportsCount;
	}

	public Exports[] getExports() {
		return exports;
	}

	public U2 getOpensCount() {
		return opensCount;
	}

	public Opens[] getOpens() {
		return opens;
	}

	public U2Array getUses() {
		return uses;
	}

	public U2 getProvidesCount() {
		return providesCount;
	}

	public Provides[] getProvides() {
		return provides;
	}

	public record Requires(int index, U2 requiresIndex, U2 accessFlag, U2 requiresVersionIndex)
			implements Print.Printable<AttributePrinter> {

		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}

	public record Exports(int index, U2 exportsIndex, U2 accessFlag, U2Array exportsToIndex)
			implements Print.Printable<AttributePrinter> {
		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}

	public record Opens(int index, U2 opensIndex, U2 accessFlag, U2Array opensToIndex)
			implements Print.Printable<AttributePrinter> {
		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}

	public record Provides(int index, U2 providesIndex, U2Array providesWithIndex)
			implements Print.Printable<AttributePrinter> {
		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}
}
