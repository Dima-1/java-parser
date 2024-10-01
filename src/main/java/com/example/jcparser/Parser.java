package com.example.jcparser;

import com.example.jcparser.attribute.*;
import com.example.jcparser.attribute.StackMapTableAttribute;
import com.example.jcparser.attribute.stackmapframe.*;
import org.apache.commons.text.StringEscapeUtils;

import java.io.*;
import java.util.*;

/**
 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.1">4.1. The ClassFile Structure</a>
 */
public class Parser {

	private final Print print;
	private int count = 0;
	private final List<ConstantPoolEntry> constantPool = new ArrayList<>();
	private final Map<String, Attribute> attributes = new LinkedHashMap<>();

	public Parser(Print print) {
		this.print = print;
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Use [/path/file.class] as first argument");
			System.exit(0);
		}
		Parser parser = new Parser(new Print());
		parser.process(args[0]);
	}

	private void process(String fileName) {
		File file = new File(fileName);
		try (FileInputStream fis = new FileInputStream(file);
		     DataInputStream dis = new DataInputStream(fis)) {
			print.setLength(file.length());
			count = Magic.BYTES;
			Magic.getMagic(dis);
			print.u2(readU2(dis), "Minor version", ConsoleColors.BLUE, true);
			print.u2(readU2(dis), "Major version", ConsoleColors.BLUE, true);
			U2 u2 = readU2(dis);
			print.u2(u2, "Constant pool count", ConsoleColors.BLUE, true);
			readConstantPool(dis, u2.value);
			print.constantPool(constantPool);
			U2 accessFlags = readU2(dis);
			print.accessFlags(accessFlags, Type.CLASS);
			print.u2(readU2(dis, true), "This class");
			print.u2(readU2(dis, true), "Super class");
			u2 = readU2(dis);
			print.u2(u2, "Interfaces count", ConsoleColors.BLUE, true);
			readInterfaces(dis, u2.value);
			u2 = readU2(dis);
			print.u2(u2, "Fields count", ConsoleColors.BLUE, true);
			readFields(dis, u2.value);
			u2 = readU2(dis);
			print.u2(u2, "Methods count", ConsoleColors.BLUE, true);
			readMethods(dis, u2.value);
			U2 attributesCount = readU2(dis);
			print.u2(attributesCount, "Attributes count", ConsoleColors.BLUE, true);
			if (attributesCount.value > 0) {
				attributes.putAll(readAttributes(dis, attributesCount.value));
				print.attributes(attributes);
			}
		} catch (IOException e) {
			e.getMessage();
		}
	}

	private void readInterfaces(DataInputStream dis, int interfacesCount) throws IOException {
		for (int i = 0; i < interfacesCount; i++) {
			print.u2(readU2(dis, true), "");
		}
	}

	/**
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.5">4.5. Fields</a>
	 */
	private void readFields(DataInputStream dis, int fieldsCount) throws IOException {
		for (int i = 0; i < fieldsCount; i++) {
			U2 accessFlags = readU2(dis);
			print.accessFlags(accessFlags, Type.FIELD);
			print.u2(readU2(dis, true), "Name index");
			print.u2(readU2(dis, true), "Descriptor index");
			U2 u2 = readU2(dis);
			print.u2(u2, "Attributes count");
			Map<String, Attribute> attributes = new LinkedHashMap<>(readAttributes(dis, u2.value));
			print.attributes(attributes);
		}
	}

	/**
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.6">4.6. Methods</a>
	 */
	private void readMethods(DataInputStream dis, int methodsCount) throws IOException {
		for (int i = 0; i < methodsCount; i++) {
			U2 accessFlags = readU2(dis);
			print.accessFlags(accessFlags, Type.METHOD);
			print.u2(readU2(dis, true), "Name index");
			print.u2(readU2(dis, true), "Descriptor index");
			U2 u2 = readU2(dis);
			print.u2(u2, "Attributes count");
			Map<String, Attribute> attributes = new LinkedHashMap<>(readAttributes(dis, u2.value));
			print.attributes(attributes);
		}
	}

	/**
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7">4.7. Attributes</a>
	 */
	private Map<String, Attribute> readAttributes(DataInputStream dis, int attributesCount) throws IOException {
		Map<String, Attribute> attributes = new LinkedHashMap<>();
		for (int i = 0; i < attributesCount; i++) {
			Attribute attribute = readAttribute(dis);
			attributes.put(attribute.getName(), attribute);
		}
		return attributes;
	}

	public enum ConstantTag {

		CONSTANT_Utf8(1),                //1  45.3	1.0.2
		CONSTANT_Integer(3),             //3  45.3	1.0.2
		CONSTANT_Float(4),               //4  45.3	1.0.2
		CONSTANT_Long(5),                //5  45.3	1.0.2
		CONSTANT_Double(6),              //6  45.3	1.0.2
		CONSTANT_Class(7),               //7  45.3	1.0.2
		CONSTANT_String(8),              //8  45.3	1.0.2
		CONSTANT_Fieldref(9),            //9  45.3	1.0.2
		CONSTANT_Methodref(10),          //10 45.3	1.0.2
		CONSTANT_InterfaceMethodref(11), //11 45.3	1.0.2
		CONSTANT_NameAndType(12),        //12 45.3	1.0.2
		CONSTANT_MethodHandle(15),       //15 51.0	7
		CONSTANT_MethodType(16),         //16 51.0	7
		CONSTANT_Dynamic(17),            //17 55.0	11
		CONSTANT_InvokeDynamic(18),      //18 51.0	7
		CONSTANT_Module(19),             //19 53.0	9
		CONSTANT_Package(20);            //20 53.0	9

		private final int tag;

		ConstantTag(int tag) {
			this.tag = tag;
		}

		public boolean isConstantClass() {
			return this == CONSTANT_Class;
		}

		public boolean isTwoEntriesTakeUp() {
			return this == CONSTANT_Double | this == CONSTANT_Long;
		}

		public boolean isConstantValueAttribute() {
			return this == CONSTANT_Integer | this == CONSTANT_Float | this == CONSTANT_Long
					| this == CONSTANT_Double | this == CONSTANT_String;
		}

		public static ConstantTag getConstant(int tag) {
			return Arrays.stream(values()).filter(v -> v.tag == tag).findFirst().orElseThrow(() ->
					new IllegalArgumentException("Unknown constant tag: " + tag));
		}
	}

	/**
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.4">4.4. The Constant Pool</a>
	 */
	private void readConstantPool(DataInputStream dis, int constantPoolCount) throws IOException {
		for (int i = 1; i < constantPoolCount; i++) {
			ConstantPoolEntry entry = createEntry(i, dis);
			constantPool.add(entry);
			if (entry.constantTag.isTwoEntriesTakeUp()) {
				constantPool.add(null);
				i++;
			}
		}
	}

	private ConstantPoolEntry createEntry(int idx, DataInputStream dis) throws IOException {
		int offset = count;
		int tag = dis.read();
		count++;
		ConstantTag constantTag = ConstantTag.getConstant(tag);

		return switch (constantTag) {
			case CONSTANT_Utf8 -> {
				final int length = dis.readUnsignedShort();
				count += Short.BYTES;
				count += length;
				yield new ConstantPoolUtf8(constantPool, offset, idx, constantTag, new String(dis.readNBytes(length)));
			}
			case CONSTANT_Integer -> {
				int anInt = dis.readInt();
				count += Integer.BYTES;
				yield new ConstantPoolInteger(constantPool, offset, idx, constantTag, anInt);
			}
			case CONSTANT_Float -> {
				float aFloat = dis.readFloat();
				count += Float.BYTES;
				yield new ConstantPoolFloat(constantPool, offset, idx, constantTag, aFloat);
			}
			case CONSTANT_Long -> {
				long aLong = dis.readLong();
				count += Long.BYTES;
				yield new ConstantPoolLong(constantPool, offset, idx, constantTag, aLong);
			}
			case CONSTANT_Double -> {
				double aDouble = dis.readDouble();
				count += Double.BYTES;
				yield new ConstantPoolDouble(constantPool, offset, idx, constantTag, aDouble);
			}
			case CONSTANT_Class, CONSTANT_String, CONSTANT_MethodType, CONSTANT_Module, CONSTANT_Package -> {
				final int aShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolString(constantPool, offset, idx, constantTag, aShort);
			}
			case CONSTANT_Fieldref, CONSTANT_Methodref, CONSTANT_InterfaceMethodref -> {
				final int aShort = dis.readUnsignedShort();
				count += Short.BYTES;
				int bShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolMethodRef(constantPool, offset, idx, constantTag, aShort, bShort);
			}
			case CONSTANT_NameAndType -> {
				final int aShort = dis.readUnsignedShort();
				count += Short.BYTES;
				int bShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolNameAndType(constantPool, offset, idx, constantTag, aShort, bShort);
			}
			case CONSTANT_Dynamic, CONSTANT_InvokeDynamic -> {
				final int aShort = dis.readUnsignedShort();
				count += Short.BYTES;
				int bShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolDynamic(constantPool, offset, idx, constantTag, aShort, bShort);
			}
			case CONSTANT_MethodHandle -> {
				final int referenceKind = dis.read();
				count++;
				int bShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolMethodHandle(constantPool, offset, idx, constantTag, referenceKind, bShort);
			}
		};
	}

	private Attribute readAttribute(DataInputStream dis) throws IOException {
		U2 attributeNameIndex = readU2(dis, true);
		String name = constantPool.get(attributeNameIndex.getValue() - 1).getAdditional();
		U4 attributeLength = readU4(dis);

		return switch (name) {
			case "ConstantValue" -> {
				final U2 aShort = readU2(dis, true);
				yield new ConstantValueAttribute(constantPool, attributeNameIndex, attributeLength, aShort);
			}
			case "Code" -> {
				final U2 maxStack = readU2(dis);
				final U2 maxLocals = readU2(dis);
				final U4 codeLength = readU4(dis);
				List<Opcode> opcodes = new ArrayList<>();
				int startCodeCount = count;
				int endCodeCount = startCodeCount + codeLength.getValue();
				do {
					opcodes.add(readOpcode(dis, startCodeCount));
				} while (count < endCodeCount);
				final U2 exceptionTableLength = readU2(dis);
				ExceptionsAttribute.Exception[] exceptions = new ExceptionsAttribute.Exception[exceptionTableLength.getValue()];
				for (int i = 0; i < exceptionTableLength.getValue(); i++) {
					exceptions[i] = readException(dis);
				}
				final U2 numberOf = readU2(dis);
				Map<String, Attribute> attributes = new LinkedHashMap<>(readAttributes(dis, numberOf.value));
				yield new CodeAttribute(constantPool, attributeNameIndex, attributeLength, maxStack, maxLocals,
						codeLength, opcodes, exceptionTableLength, exceptions, numberOf, attributes);
			}
			case "StackMapTable" -> {
				final U2 numberOf = readU2(dis);
				List<StackMapFrame> entries = new ArrayList<>();
				for (int i = 0; i < numberOf.getValue(); i++) {
					entries.add(readStackMapFrame(dis));
				}
				yield new StackMapTableAttribute(constantPool, attributeNameIndex, attributeLength, numberOf, entries);
			}
			case "Exceptions" -> {
				final U2 numberOf = readU2(dis);
				U2[] exceptions = new U2[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					exceptions[i] = readU2(dis, true);
				}
				yield new ExceptionsAttribute(constantPool, attributeNameIndex, attributeLength, numberOf, exceptions);
			}
			case "InnerClasses" -> {
				final U2 numberOf = readU2(dis);
				InnerClassesAttribute.InnerClass[] classes = new InnerClassesAttribute.InnerClass[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					classes[i] = getInnerClass(dis);
				}
				yield new InnerClassesAttribute(constantPool, attributeNameIndex, attributeLength,
						numberOf, classes);
			}
			case "EnclosingMethod" -> {
				final U2 classIndex = readU2(dis, true);
				final U2 methodIndex = readU2(dis, true);
				yield new EnclosingMethodAttribute(constantPool, attributeNameIndex, attributeLength,
						classIndex, methodIndex);
			}
			case "Synthetic" -> new Attribute(constantPool, attributeNameIndex, attributeLength);
			case "Signature" -> {
				final U2 aShort = readU2(dis, true);
				yield new SignatureAttribute(constantPool, attributeNameIndex, attributeLength, aShort);
			}
			case "SourceFile" -> {
				final U2 aShort = readU2(dis, true);
				yield new SourceFileAttribute(constantPool, attributeNameIndex, attributeLength, aShort);
			}
			case "SourceDebugExtension" -> {
				String utf8 = new String(dis.readNBytes(attributeLength.getValue()));
				yield new SourceDebugExtensionAttribute(constantPool, attributeNameIndex, attributeLength, utf8);
			}
			case "LineNumberTable" -> {
				final U2 numberOf = readU2(dis);
				LineNumberTableAttribute.LineNumber[] lineNumbers = new LineNumberTableAttribute.LineNumber[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					lineNumbers[i] = getLineNumber(dis);
				}
				yield new LineNumberTableAttribute(constantPool, attributeNameIndex, attributeLength, numberOf,
						lineNumbers);
			}
			case "LocalVariableTable" -> {
				final U2 numberOf = readU2(dis);
				LocalVariableAttribute.LocalVariable[] localVariables
						= new LocalVariableAttribute.LocalVariable[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					localVariables[i] = getLocalVariable(dis, "Descriptor");
				}
				yield new LocalVariableTableAttribute(constantPool, attributeNameIndex, attributeLength, numberOf,
						localVariables);
			}
			case "LocalVariableTypeTable" -> {
				final U2 numberOf = readU2(dis);
				LocalVariableAttribute.LocalVariable[] localVariables
						= new LocalVariableAttribute.LocalVariable[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					localVariables[i] = getLocalVariable(dis, "Signature");
				}
				yield new LocalVariableTypeTableAttribute(constantPool, attributeNameIndex, attributeLength, numberOf,
						localVariables);
			}
			case "BootstrapMethods" -> {
				final U2 numberOf = readU2(dis);
				BootstrapMethodsAttribute.BootstrapMethod[] bootstrapMethods = new BootstrapMethodsAttribute.BootstrapMethod[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					bootstrapMethods[i] = getBootstrapMethod(i, dis);
				}
				yield new BootstrapMethodsAttribute(constantPool, attributeNameIndex, attributeLength,
						numberOf, bootstrapMethods);
			}
			case "NestMembers" -> {
				final U2 numberOf = readU2(dis);
				U2[] classes = new U2[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					classes[i] = readU2(dis, true);
				}
				yield new NestMembersAttribute(constantPool, attributeNameIndex, attributeLength,
						numberOf, classes);
			}
			default -> {
				for (int j = 0; j < attributeLength.getValue(); j++) {
					readByte(dis);
				}
				yield new Attribute(constantPool, attributeNameIndex, attributeLength);
			}
		};
	}

	private U1 readU1(DataInputStream dis) throws IOException {
		int value = dis.readUnsignedByte();
		U1 u1 = new U1(count, value, "");
		count += Byte.BYTES;
		return u1;
	}

	private U2 readU2(DataInputStream dis) throws IOException {
		return readU2(dis, false);
	}

	private U2 readU2(DataInputStream dis, boolean addSymbolicName) throws IOException {
		String symbolic = "";
		int value = dis.readUnsignedShort();
		if (addSymbolicName && !constantPool.isEmpty()) {
			if (value > 0) {
				symbolic += constantPool.get(value - 1).getAdditional();
			} else {
				symbolic = "java/lang/Object";
			}
		}
		U2 u2 = new U2(count, value, symbolic);
		count += Short.BYTES;
		return u2;
	}

	private U4 readU4(DataInputStream dis) throws IOException {
		int value = dis.readInt();
		U4 u4 = new U4(count, value, "");
		count += Integer.BYTES;
		return u4;
	}

	private Opcode readOpcode(DataInputStream dis, int startCodeCount) throws IOException {
		int offset = count;
		int value = readByte(dis);
		int size = Instruction.getArgumentSize(value);
		List<Integer> arguments = new ArrayList<>();
		if (value == 0xC4) {
			int additionalOpcode = readByte(dis);
			size = additionalOpcode == 0x84 ? 5 : 3;
			arguments.add(additionalOpcode);
		}
		if (value == 0xAA) {
			size = (count - startCodeCount) % 4;
			readNBytes(dis, arguments, size);
			size += 3 * U4.getSize();
			U4 defaultValue = readU4(dis);
			arguments.addAll(defaultValue.getIntList());
			U4 low = readU4(dis);
			arguments.addAll(low.getIntList());
			U4 high = readU4(dis);
			arguments.addAll(high.getIntList());
			size += (high.getValue() - low.getValue() + 1) * U4.getSize();
		}
		if (value == 0xAB) {
			size = (count - startCodeCount) % 4;
			readNBytes(dis, arguments, size);
			size += 2 * U4.getSize();
			U4 defaultValue = readU4(dis);
			arguments.addAll(defaultValue.getIntList());
			U4 nPairs = readU4(dis);
			arguments.addAll(nPairs.getIntList());
			size += nPairs.getValue() * 2 * U4.getSize();
		}
		readNBytes(dis, arguments, size);
		return new Opcode(offset, value, arguments.stream().mapToInt(i -> i).toArray());
	}

	private int readByte(DataInputStream dis) throws IOException {
		int value = dis.readUnsignedByte();
		count += Byte.BYTES;
		return value;
	}

	private void readNBytes(DataInputStream dis, List<Integer> arguments, int n) throws IOException {
		for (int i = arguments.size(); i < n; i++) {
			arguments.add(readByte(dis));
		}
	}

	private StackMapFrame readStackMapFrame(DataInputStream dis) throws IOException {
		U1 tag = readU1(dis);
		return switch (FrameType.getType(tag.getValue())) {
			case SAME -> new StackMapFrame(tag);
			case SAME_LOCALS_1_STACK_ITEM ->
					new SameLocals1StackItemStackMapFrame(tag, new TypeInfo[]{getVerificationTypeInfo(dis)});
			case SAME_LOCALS_1_STACK_ITEM_EXTENDED -> {
				U2 offsetDelta = readU2(dis);
				yield new SameLocals1StackItemStackMapFrameExtended(tag, offsetDelta,
						new TypeInfo[]{getVerificationTypeInfo(dis)});
			}
			case CHOP -> {
				U2 offsetDelta = readU2(dis);
				yield new ChopStackMapFrame(tag, offsetDelta);
			}
			case SAME_FRAME_EXTENDED -> {
				U2 offsetDelta = readU2(dis);
				yield new SameStackMapFrameExtended(tag, offsetDelta);
			}
			case APPEND -> {
				U2 offsetDelta = readU2(dis);
				int size = tag.getValue() - 251;
				TypeInfo[] stack = new TypeInfo[size];
				for (int i = 0; i < size; i++) {
					stack[i] = getVerificationTypeInfo(dis);
				}
				yield new AppendStackMapFrame(tag, offsetDelta, stack);
			}
			case FULL_FRAME -> {
				U2 offsetDelta = readU2(dis);
				U2 numberOfLocal = readU2(dis);
				TypeInfo[] local = new TypeInfo[numberOfLocal.getValue()];
				for (int i = 0; i < local.length; i++) {
					local[i] = getVerificationTypeInfo(dis);
				}
				U2 numberOfStack = readU2(dis);
				TypeInfo[] stack = new TypeInfo[numberOfStack.getValue()];
				for (int i = 0; i < stack.length; i++) {
					stack[i] = getVerificationTypeInfo(dis);
				}
				yield new FullStackMapFrame(tag, offsetDelta, numberOfLocal, local, numberOfStack, stack);
			}
		};
	}

	private TypeInfo getVerificationTypeInfo(DataInputStream dis) throws IOException {
		U1 typeInfo = readU1(dis);
		U2 typeInfoAdditional = null;
		if (typeInfo.getValue() == 7 || typeInfo.getValue() == 8) {
			typeInfoAdditional = readU2(dis);
		}
		return new TypeInfo(typeInfo, typeInfoAdditional);
	}

	public BootstrapMethodsAttribute.BootstrapMethod getBootstrapMethod(int index, DataInputStream dis) throws IOException {
		final U2 bootstrapMethodRef = readU2(dis, true);
		final U2 numberOf = readU2(dis);
		U2[] bootstrapArguments = new U2[numberOf.getValue()];
		for (int i = 0; i < numberOf.getValue(); i++) {
			bootstrapArguments[i] = readU2(dis, true);
		}
		return new BootstrapMethodsAttribute.BootstrapMethod(index, bootstrapMethodRef, numberOf, bootstrapArguments);
	}

	public InnerClassesAttribute.InnerClass getInnerClass(DataInputStream dis) throws IOException {
		final U2 innerClassInfoIndex = readU2(dis, true);
		final U2 outerClassInfoIndex = readU2(dis, true);
		outerClassInfoIndex.clearZeroSymbolic();
		final U2 innerNameIndex = readU2(dis, true);
		innerNameIndex.clearZeroSymbolic();
		final U2 innerClassAccessFlags = readU2(dis);
		innerClassAccessFlags.clearZeroSymbolic();
		return new InnerClassesAttribute.InnerClass(innerClassInfoIndex, outerClassInfoIndex, innerNameIndex, innerClassAccessFlags);
	}

	public LineNumberTableAttribute.LineNumber getLineNumber(DataInputStream dis) throws IOException {
		final U2 startPC = readU2(dis);
		final U2 lineNumber = readU2(dis);
		return new LineNumberTableAttribute.LineNumber(startPC, lineNumber);
	}

	private LocalVariableAttribute.LocalVariable getLocalVariable(DataInputStream dis, String descriptorTitle) throws IOException {
		final U2 startPC = readU2(dis);
		final U2 length = readU2(dis);
		final U2 nameIndex = readU2(dis, true);
		final U2 descriptorIndex = readU2(dis, true);
		final U2 index = readU2(dis);
		return new LocalVariableAttribute.LocalVariable(startPC, length, nameIndex, descriptorIndex, index, descriptorTitle);
	}

	public ExceptionsAttribute.Exception readException(DataInputStream dis) throws IOException {
		final U2 startPc = readU2(dis);
		final U2 endPc = readU2(dis);
		final U2 handlerPc = readU2(dis);
		final U2 catchType = readU2(dis, true);
		return new ExceptionsAttribute.Exception(startPc, endPc, handlerPc, catchType);
	}

	private static final class Magic {
		static byte[] bytes = {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};
		static final int BYTES = bytes.length;

		private static void getMagic(DataInputStream dis) throws IOException {
			byte[] magic = dis.readNBytes(Magic.BYTES);
			if (!Arrays.equals(magic, Magic.bytes)) {
				StringBuilder sb = new StringBuilder();
				for (byte b : magic) {
					sb.append(String.format("%02X ", b));
				}
				throw new RuntimeException("Wrong magic tag : " + sb);
			}
		}
	}

	public static class ConstantPoolEntry implements Print.Printable<Print.ConstantPrinter> {
		protected final List<ConstantPoolEntry> constants;
		private final int offset;
		private final int idx;
		private final ConstantTag constantTag;

		public ConstantPoolEntry(List<ConstantPoolEntry> constants, int offset, int idx, ConstantTag constantTag) {
			this.constants = constants;
			this.offset = offset;
			this.idx = idx;
			this.constantTag = constantTag;
		}

		public String getAdditional() {
			return "";
		}

		public int getOffset() {
			return offset;
		}

		public int getIdx() {
			return idx;
		}

		public ConstantTag getConstantTag() {
			return constantTag;
		}

		@Override
		public void print(Print.ConstantPrinter printer) {
			printer.format(this);
		}

		protected String toHex(int i) {
			return Integer.toHexString(i).toUpperCase();
		}
	}

	public static final class ConstantPoolUtf8 extends ConstantPoolEntry {
		private final String utf8;

		public ConstantPoolUtf8(List<ConstantPoolEntry> constants, int offset, int idx, ConstantTag constantTag, String utf8) {
			super(constants, offset, idx, constantTag);
			this.utf8 = StringEscapeUtils.escapeJava(utf8);
		}

		public String getUtf8() {
			return utf8;
		}

		@Override
		public String getAdditional() {
			return utf8;
		}

		@Override
		public void print(Print.ConstantPrinter printer) {
			super.print(printer);
			printer.format(this);
		}
	}

	public static class ConstantPoolInteger extends ConstantPoolEntry {
		private final int value;

		public ConstantPoolInteger(List<ConstantPoolEntry> constants, int offset, int idx, ConstantTag constantTag, int value) {
			super(constants, offset, idx, constantTag);
			this.value = value;
		}

		@Override
		public String getAdditional() {
			return String.valueOf(value);
		}

		public int getValue() {
			return value;
		}

		@Override
		public void print(Print.ConstantPrinter printer) {
			super.print(printer);
			printer.format(this);
		}
	}

	public static class ConstantPoolFloat extends ConstantPoolEntry {
		private final float value;

		public ConstantPoolFloat(List<ConstantPoolEntry> constants, int offset, int idx, ConstantTag constantTag, float value) {
			super(constants, offset, idx, constantTag);
			this.value = value;
		}

		@Override
		public String getAdditional() {
			return String.valueOf(value);
		}

		public float getValue() {
			return value;
		}

		@Override
		public void print(Print.ConstantPrinter printer) {
			super.print(printer);
			printer.format(this);
		}
	}

	public static class ConstantPoolLong extends ConstantPoolEntry {
		private final long value;

		public ConstantPoolLong(List<ConstantPoolEntry> constants, int offset, int idx, ConstantTag constantTag, long value) {
			super(constants, offset, idx, constantTag);
			this.value = value;
		}

		@Override
		public String getAdditional() {
			return String.valueOf(value);
		}

		public long getValue() {
			return value;
		}

		@Override
		public void print(Print.ConstantPrinter printer) {
			super.print(printer);
			printer.format(this);
		}
	}

	public static class ConstantPoolDouble extends ConstantPoolEntry {
		private final double value;

		public ConstantPoolDouble(List<ConstantPoolEntry> constants, int offset, int idx, ConstantTag constantTag, double value) {
			super(constants, offset, idx, constantTag);
			this.value = value;
		}

		@Override
		public String getAdditional() {
			return String.valueOf(value);
		}

		public double getValue() {
			return value;
		}

		@Override
		public void print(Print.ConstantPrinter printer) {
			super.print(printer);
			printer.format(this);
		}
	}

	public static final class ConstantPoolString extends ConstantPoolEntry {
		private final int stringIndex;

		public ConstantPoolString(List<ConstantPoolEntry> constants, int offset, int idx, ConstantTag constantTag,
		                          int stringIndex) {
			super(constants, offset, idx, constantTag);
			this.stringIndex = stringIndex;
		}

		@Override
		public String getAdditional() {
			return "(" + toHex(stringIndex) + ") " + getString();
		}

		public int getStringIndex() {
			return stringIndex;
		}

		public String getString() {
			return ((ConstantPoolUtf8) constants.get(stringIndex - 1)).getUtf8();
		}

		@Override
		public void print(Print.ConstantPrinter printer) {
			super.print(printer);
			printer.format(this);
		}
	}

	public static final class ConstantPoolMethodRef extends ConstantPoolEntry {
		private final int classIndex;
		private final int nameAndTypeIndex;

		public ConstantPoolMethodRef(List<ConstantPoolEntry> constants, int offset, int idx, ConstantTag constantTag,
		                             int classIndex, int nameAndTypeIndex) {
			super(constants, offset, idx, constantTag);
			this.classIndex = classIndex;
			this.nameAndTypeIndex = nameAndTypeIndex;
		}

		@Override
		public String getAdditional() {
			return "(" + toHex(classIndex) + ") " + getClassIndexString()
					+ " (" + toHex(nameAndTypeIndex) + ") " + getNameAndTypeIndexString();
		}

		public int getClassIndex() {
			return classIndex;
		}

		public String getClassIndexString() {
			return ((ConstantPoolString) constants.get(classIndex - 1)).getString();
		}

		public int getNameAndTypeIndex() {
			return nameAndTypeIndex;
		}

		public String getNameAndTypeIndexString() {
			return ((ConstantPoolNameAndType) constants.get(nameAndTypeIndex - 1)).getAdditional();
		}

		@Override
		public void print(Print.ConstantPrinter printer) {
			super.print(printer);
			printer.format(this);
		}
	}

	public static final class ConstantPoolNameAndType extends ConstantPoolEntry {
		private final int nameIndex;
		private final int descriptorIndex;

		public ConstantPoolNameAndType(List<ConstantPoolEntry> constants, int offset, int idx, ConstantTag constantTag,
		                               int nameIndex, int descriptorIndex) {
			super(constants, offset, idx, constantTag);
			this.nameIndex = nameIndex;
			this.descriptorIndex = descriptorIndex;
		}

		@Override
		public String getAdditional() {
			return "(" + toHex(nameIndex) + ") " + getNameIndexString()
					+ " (" + toHex(descriptorIndex) + ") " + getDescriptorIndexString();
		}

		public int getNameIndex() {
			return nameIndex;
		}

		public String getNameIndexString() {
			return ((ConstantPoolUtf8) constants.get(nameIndex - 1)).getUtf8();
		}

		public int getDescriptorIndex() {
			return descriptorIndex;
		}

		public String getDescriptorIndexString() {
			return ((ConstantPoolUtf8) constants.get(descriptorIndex - 1)).getUtf8();
		}

		@Override
		public void print(Print.ConstantPrinter printer) {
			super.print(printer);
			printer.format(this);
		}
	}

	public static final class ConstantPoolDynamic extends ConstantPoolEntry {
		private final int bootstrapMethodAttrIndex;
		private final int nameAndTypeIndex;

		public ConstantPoolDynamic(List<ConstantPoolEntry> constants, int offset, int idx, ConstantTag constantTag,
		                           int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
			super(constants, offset, idx, constantTag);
			this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
			this.nameAndTypeIndex = nameAndTypeIndex;
		}

		@Override
		public String getAdditional() {
			return "(" + toHex(bootstrapMethodAttrIndex) + ") "
					+ " (" + toHex(nameAndTypeIndex) + ") " + constants.get(nameAndTypeIndex - 1).getAdditional();
		}

		public int getBootstrapMethodAttrIndex() {
			return bootstrapMethodAttrIndex;
		}

		public int getNameAndTypeIndex() {
			return nameAndTypeIndex;
		}

		@Override
		public void print(Print.ConstantPrinter printer) {
			super.print(printer);
			printer.format(this);
		}
	}

	public static class ConstantPoolMethodHandle extends ConstantPoolEntry {

		enum MHRef {
			REF_getField,           //getfield C.f:T
			REF_getStatic,          //getstatic C.f:T
			REF_putField,           //putfield C.f:T
			REF_putStatic,          //putstatic C.f:T
			REF_invokeVirtual,      //invokevirtual C.m:(A*)T
			REF_invokeStatic,       //invokestatic C.m:(A*)T
			REF_invokeSpecial,      //invokespecial C.m:(A*)T
			REF_newInvokeSpecial,   //new C; dup; invokespecial C.<init>:(A*)V
			REF_invokeInterface     //invokeinterface C.m:(A*)T
		}

		private final int referenceKind;
		private final int referenceIndex;

		public ConstantPoolMethodHandle(List<ConstantPoolEntry> constants, int offset, int idx, ConstantTag constantTag,
		                                int referenceKind, int referenceIndex) {
			super(constants, offset, idx, constantTag);
			this.referenceKind = referenceKind;
			this.referenceIndex = referenceIndex;
		}

		@Override
		public String getAdditional() {
			return MHRef.values()[referenceKind].name().replaceFirst("REF_", "")
					+ " (" + toHex(referenceIndex) + ")" + constants.get(referenceIndex - 1).getAdditional();
		}

		public int getReferenceKind() {
			return referenceKind;
		}

		public int getReferenceIndex() {
			return referenceIndex;
		}

		@Override
		public void print(Print.ConstantPrinter printer) {
			super.print(printer);
			printer.format(this);
		}
	}

	public static class U1 {
		private final int offset;
		protected final int value;
		private String symbolic;

		public U1(int offset, int value, String symbolic) {
			this.offset = offset;
			this.value = value;
			this.symbolic = symbolic;
		}

		public int getOffset() {
			return offset;
		}

		public int getValue() {
			return value;
		}

		public String getSymbolic() {
			return symbolic;
		}

		public static int getSize() {
			return 1;
		}

		public List<Integer> getIntList() {
			return Arrays.stream(getByteArray()).boxed().toList();
		}

		public void clearZeroSymbolic() {
			if (value == 0) {
				symbolic = "";
			}
		}

		public int[] getByteArray() {
			return new int[]{value & 0xFF};
		}
	}

	public static class U2 extends U1 {

		public U2(int offset, int value, String symbolic) {
			super(offset, value, symbolic);
		}

		public static int getSize() {
			return 2;
		}

		@Override
		public int[] getByteArray() {
			return new int[]{(value >> 8) & 0xFF, value & 0xFF};
		}
	}

	public static class U4 extends U2 {
		public U4(int offset, int value, String symbolic) {
			super(offset, value, symbolic);
		}

		public static int getSize() {
			return 4;
		}

		@Override
		public int[] getByteArray() {
			return new int[]{(value >> 24) & 0xFF, (value >> 16) & 0xFF, (value >> 8) & 0xFF, value & 0xFF};
		}
	}
}
