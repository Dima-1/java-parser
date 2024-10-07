package com.example.jcparser.attribute;

import java.util.List;

import static com.example.jcparser.Parser.*;

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
	private final U2 usesCount;
	private final U2[] uses;
	private final U2 providesCount;
	private final Provides[] provides;

	public ModuleAttribute(List<ConstantPoolEntry> constantPool, U2 attributeNameIndex, U4 attributeLength,
	                       U2 moduleNameIndex, U2 moduleFlags, U2 moduleVersionIndex,
	                       U2 requiresCount, Requires[] requires, U2 exportsCount, Exports[] exports,
	                       U2 opensCount, Opens[] opens, U2 usesCount, U2[] uses, U2 providesCount, Provides[] provides) {
		super(constantPool, attributeNameIndex, attributeLength);
		this.moduleNameIndex = moduleNameIndex;
		this.moduleFlags = moduleFlags;
		this.moduleVersionIndex = moduleVersionIndex;
		this.requiresCount = requiresCount;
		this.requires = requires;
		this.exportsCount = exportsCount;
		this.exports = exports;
		this.opensCount = opensCount;
		this.opens = opens;
		this.usesCount = usesCount;
		this.uses = uses;
		this.providesCount = providesCount;
		this.provides = provides;
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

	public U2 getUsesCount() {
		return usesCount;
	}

	public U2[] getUses() {
		return uses;
	}

	public U2 getProvidesCount() {
		return providesCount;
	}

	public Provides[] getProvides() {
		return provides;
	}

	public record Requires(int index, U2 requiresIndex, U2 accessFlag, U2 requiresVersionIndex) {
	}

	public record Exports(int index, U2 exportsIndex, U2 accessFlag, U2 exportsToCount, U2[] exportsToIndex) {
	}

	public record Opens(int index, U2 opensIndex, U2 accessFlag, U2 opensToCount, U2[] opensToIndex) {
	}

	public record Provides(int index, U2 providesIndex, U2 providesWithCount, U2[] providesWithIndex) {
	}
}
