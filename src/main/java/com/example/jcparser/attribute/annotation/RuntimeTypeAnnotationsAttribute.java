package com.example.jcparser.attribute.annotation;

import com.example.jcparser.Parser.U1;
import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.Print;
import com.example.jcparser.attribute.Attribute;
import com.example.jcparser.attribute.AttributePrinter;

import java.util.Arrays;

public class RuntimeTypeAnnotationsAttribute extends Attribute {
	private final U2 numberOf;
	private final TypeAnnotation[] typeAnnotations;
	private final boolean visible;

	public RuntimeTypeAnnotationsAttribute(U2 nameIndex, U4 length, U2 numberOf,
	                                       TypeAnnotation[] typeAnnotations, boolean visible) {
		super(nameIndex, length);
		this.numberOf = numberOf;
		this.typeAnnotations = typeAnnotations;
		this.visible = visible;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2 getNumberOf() {
		return numberOf;
	}

	public TypeAnnotation[] getTypeAnnotations() {
		return typeAnnotations;
	}

	public boolean isVisible() {
		return visible;
	}

	public record TypeAnnotation(TargetInfo targetInfo, U1 typePathLength, TypePath[] typePath,
	                             RuntimeAnnotationsAttribute.Annotation annotation)
			implements Print.Printable<AttributePrinter> {

		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}

	public static class TargetInfo implements Print.Printable<AttributePrinter> {
		private final U1 targetType;

		TargetInfo(U1 targetType) {
			this.targetType = targetType;
		}

		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}

		public U1 getTargetType() {
			return targetType;
		}
	}

	public static class TypeParameterTarget extends TargetInfo {
		private final U1 typeParameterIndex;

		public TypeParameterTarget(U1 targetType, U1 typeParameterIndex) {
			super(targetType);
			this.typeParameterIndex = typeParameterIndex;
		}

		public U1 getTypeParameterIndex() {
			return typeParameterIndex;
		}
	}

	public static class SupertypeTargetClass extends TargetInfo {
		private final U2 supertypeIndex;

		public SupertypeTargetClass(U1 targetType, U2 supertypeIndex) {
			super(targetType);
			this.supertypeIndex = supertypeIndex;
		}

		public U2 getSupertypeIndex() {
			return supertypeIndex;
		}
	}

	public static class TypeParameterBoundTarget extends TargetInfo {
		private final U1 typeParameterIndex;
		private final U1 boundIndex;

		public TypeParameterBoundTarget(U1 targetType, U1 typeParameterIndex, U1 boundIndex) {
			super(targetType);
			this.typeParameterIndex = typeParameterIndex;
			this.boundIndex = boundIndex;
		}

		public U1 getTypeParameterIndex() {
			return typeParameterIndex;
		}

		public U1 getBoundIndex() {
			return boundIndex;
		}
	}

	public static class EmptyTarget extends TargetInfo {
		public EmptyTarget(U1 targetType) {
			super(targetType);
		}
	}

	public static class FormalParameterTarget extends TargetInfo {
		private final U1 formalParameterIndex;

		public FormalParameterTarget(U1 targetType, U1 formalParameterIndex) {
			super(targetType);
			this.formalParameterIndex = formalParameterIndex;
		}

		@Override
		public void print(AttributePrinter printer) {
			super.print(printer);
			printer.print(this);
		}

		public U1 getFormalParameterIndex() {
			return formalParameterIndex;
		}
	}

	public static class ThrowsTarget extends TargetInfo {
		public ThrowsTarget(U1 targetType, U2 throwsTypeIndex) {
			super(targetType);
		}
	}

	public static class LocalVarTarget extends TargetInfo {
		private final TableEntry[] table;

		public LocalVarTarget(U1 targetType, TableEntry[] table) {
			super(targetType);
			this.table = table;
		}

		public TableEntry[] getTable() {
			return table;
		}
	}

	public static class CatchTarget extends TargetInfo {
		private final U2 exceptionTableIndex;

		public CatchTarget(U1 targetType, U2 exceptionTableIndex) {
			super(targetType);
			this.exceptionTableIndex = exceptionTableIndex;
		}

		public U2 getExceptionTableIndex() {
			return exceptionTableIndex;
		}
	}

	public static class OffsetTarget extends TargetInfo {
		private final U2 offset;

		public OffsetTarget(U1 targetType, U2 offset) {
			super(targetType);
			this.offset = offset;
		}

		public U2 getOffset() {
			return offset;
		}
	}

	public static class TypeArgumentTarget extends TargetInfo {
		private final U2 offset;
		private final U1 typeArgumentIndex;

		public TypeArgumentTarget(U1 targetType, U2 offset, U1 typeArgumentIndex) {
			super(targetType);
			this.offset = offset;
			this.typeArgumentIndex = typeArgumentIndex;
		}

		public U2 getOffset() {
			return offset;
		}

		public U1 getTypeArgumentIndex() {
			return typeArgumentIndex;
		}
	}

	public record TypePath(U1 typePathKind, U1 typeArgumentIndex) {
	}

	public record TableEntry(U2 startPc, U2 length, U2 index) {
	}

	public enum TypeTargetInfo {
		TYPE_PARAMETER_TARGET(0x00, 0x01),
		SUPERTYPE_TARGET(0x10),
		TYPE_PARAMETER_BOUND_TARGET(0x11, 0x12),
		EMPTY_TARGET(0x13, 0x14, 0x15),
		FORMAL_PARAMETER_TARGET(0x16),
		THROWS_TARGET(0x17),
		LOCALVAR_TARGET(0x40, 0x41),
		CATCH_TARGET(0x42),
		OFFSET_TARGET(0x43, 0x44, 0x45, 0x46),
		TYPE_ARGUMENT_TARGET(0x47, 0x48, 0x49, 0x4A, 0x4B);

		private final int[] targetTypes;

		TypeTargetInfo(int... targetTypes) {
			this.targetTypes = targetTypes;
		}

		public static TypeTargetInfo getType(int targetType) {
			return Arrays.stream(values()).filter(v -> Arrays.stream(v.targetTypes).anyMatch(tt -> tt == targetType))
					.findFirst().orElseThrow(() -> new IllegalArgumentException(
							"Wrong targetType : " + Integer.toHexString(targetType).toUpperCase()));
		}
	}
}
