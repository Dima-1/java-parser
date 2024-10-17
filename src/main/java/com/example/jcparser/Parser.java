package com.example.jcparser;

import com.example.jcparser.attribute.*;
import com.example.jcparser.attribute.annotation.*;
import com.example.jcparser.attribute.stackmapframe.*;
import org.apache.commons.text.StringEscapeUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static com.example.jcparser.AccessFlag.Type;

/**
 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.1">4.1. The ClassFile Structure</a>
 */
public class Parser {

	private final Print print;
	private int count = 0;
	private final List<ConstantPoolEntry> constantPool = new ArrayList<>();
	private ConstantPoolEntry constantObject = null;
	private final Map<String, Attribute> attributes = new LinkedHashMap<>();

	public Parser(Print print) {
		this.print = print;
	}

	public static void main(String[] args) {
		File file;
		if (args.length == 0) {
			System.err.println("Use [/path/file.class] as first argument");
			System.exit(1);
		}
		String fileName = args[0];
		file = new File(fileName);
		if (!file.exists()) {
			System.err.printf("File %s not exist\n", fileName);
			System.exit(1);
		}
		Parser parser = new Parser(new Print());
		parser.process(file);
	}

	public List<ConstantPoolEntry> getConstantPool() {
		return constantPool;
	}

	private void process(File file) {

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
			readInterfaces(dis);
			readFields(dis);
			readMethods(dis);
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

	private void readInterfaces(DataInputStream dis) throws IOException {
		U2 u2 = readU2(dis);
		print.u2(u2, "Interfaces count", ConsoleColors.BLUE, true);
		int interfacesCount = u2.value;
		for (int i = 0; i < interfacesCount; i++) {
			print.u2(readU2(dis, true), "");
		}
	}

	/**
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.5">4.5. Fields</a>
	 */
	private void readFields(DataInputStream dis) throws IOException {
		readAdditionData(dis, Type.FIELD);
	}

	/**
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.6">4.6. Methods</a>
	 */
	private void readMethods(DataInputStream dis) throws IOException {
		readAdditionData(dis, Type.METHOD);
	}

	private void readAdditionData(DataInputStream dis, Type type) throws IOException {
		String title = (type == Type.FIELD ? "Fields" : "Methods") + " count";
		U2 u2 = readU2(dis);
		print.u2(u2, title, ConsoleColors.BLUE, true);
		int length = u2.value;
		for (int i = 0; i < length; i++) {
			U2 accessFlags = readU2(dis);
			print.accessFlags(accessFlags, type);
			print.u2(readU2(dis, true), "Name index");
			print.u2(readU2(dis, true), "Descriptor index");
			u2 = readU2(dis);
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
		constantPool.add(null); //The constant_pool table is indexed from 1 to constant_pool_count - 1.
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
				int length = dis.readUnsignedShort();
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
				int aShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolString(constantPool, offset, idx, constantTag, aShort);
			}
			case CONSTANT_Fieldref, CONSTANT_Methodref, CONSTANT_InterfaceMethodref -> {
				int aShort = dis.readUnsignedShort();
				count += Short.BYTES;
				int bShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolMethodRef(constantPool, offset, idx, constantTag, aShort, bShort);
			}
			case CONSTANT_NameAndType -> {
				int aShort = dis.readUnsignedShort();
				count += Short.BYTES;
				int bShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolNameAndType(constantPool, offset, idx, constantTag, aShort, bShort);
			}
			case CONSTANT_Dynamic, CONSTANT_InvokeDynamic -> {
				int aShort = dis.readUnsignedShort();
				count += Short.BYTES;
				int bShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolDynamic(constantPool, offset, idx, constantTag, aShort, bShort);
			}
			case CONSTANT_MethodHandle -> {
				int referenceKind = dis.read();
				count++;
				int bShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolMethodHandle(constantPool, offset, idx, constantTag, referenceKind, bShort);
			}
		};
	}

	private Attribute readAttribute(DataInputStream dis) throws IOException {
		U2 attributeNameIndex = readU2(dis, true);
		String name = constantPool.get(attributeNameIndex.getValue()).getAdditional();
		U4 attributeLength = readU4(dis);

		return switch (name) {
			case "ConstantValue" -> {
				U2 constantValueIndex = readU2(dis, true).check(ConstantPoolUtf8.class);
				yield new ConstantValueAttribute(constantPool, attributeNameIndex, attributeLength, constantValueIndex);
			}
			case "Code" -> {
				U2 maxStack = readU2(dis);
				U2 maxLocals = readU2(dis);
				U4 codeLength = readU4(dis);
				List<Opcode> opcodes = new ArrayList<>();
				int startCodeCount = count;
				int endCodeCount = startCodeCount + codeLength.getValue();
				do {
					opcodes.add(readOpcode(dis, startCodeCount));
				} while (count < endCodeCount);
				U2 exceptionTableLength = readU2(dis);
				ExceptionsAttribute.Exception[] exceptions = new ExceptionsAttribute.Exception[exceptionTableLength.getValue()];
				for (int i = 0; i < exceptionTableLength.getValue(); i++) {
					exceptions[i] = readException(dis);
				}
				U2 numberOf = readU2(dis);
				Map<String, Attribute> attributes = new LinkedHashMap<>(readAttributes(dis, numberOf.value));
				yield new CodeAttribute(constantPool, attributeNameIndex, attributeLength, maxStack, maxLocals,
						codeLength, opcodes, exceptionTableLength, exceptions, numberOf, attributes);
			}
			case "StackMapTable" -> {
				U2 numberOf = readU2(dis);
				List<StackMapFrame> entries = new ArrayList<>();
				for (int i = 0; i < numberOf.getValue(); i++) {
					entries.add(readStackMapFrame(dis));
				}
				yield new StackMapTableAttribute(constantPool, attributeNameIndex, attributeLength, numberOf, entries);
			}
			case "Exceptions" -> {
				U2Array exceptions = readU2Array(dis);
				yield new ExceptionsAttribute(constantPool, attributeNameIndex, attributeLength, exceptions);
			}
			case "InnerClasses" -> {
				U2 numberOf = readU2(dis);
				InnerClassesAttribute.InnerClass[] classes = new InnerClassesAttribute.InnerClass[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					classes[i] = getInnerClass(i, dis);
				}
				yield new InnerClassesAttribute(constantPool, attributeNameIndex, attributeLength,
						numberOf, classes);
			}
			case "EnclosingMethod" -> {
				U2 classIndex = readU2(dis, true);
				U2 methodIndex = readU2(dis, true);
				yield new EnclosingMethodAttribute(constantPool, attributeNameIndex, attributeLength,
						classIndex, methodIndex);
			}
			case "Synthetic", "Deprecated" -> new Attribute(constantPool, attributeNameIndex, attributeLength);
			case "Signature" -> {
				U2 aShort = readU2(dis, true);
				yield new SignatureAttribute(constantPool, attributeNameIndex, attributeLength, aShort);
			}
			case "SourceFile" -> {
				U2 aShort = readU2(dis, true);
				yield new SourceFileAttribute(constantPool, attributeNameIndex, attributeLength, aShort);
			}
			case "SourceDebugExtension" -> {
				String utf8 = new String(dis.readNBytes(attributeLength.getValue()));
				yield new SourceDebugExtensionAttribute(constantPool, attributeNameIndex, attributeLength, utf8);
			}
			case "LineNumberTable" -> {
				U2 numberOf = readU2(dis);
				LineNumberTableAttribute.LineNumber[] lineNumbers = new LineNumberTableAttribute.LineNumber[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					lineNumbers[i] = getLineNumber(i, dis);
				}
				yield new LineNumberTableAttribute(constantPool, attributeNameIndex, attributeLength, numberOf,
						lineNumbers);
			}
			case "LocalVariableTable" -> {
				U2 numberOf = readU2(dis);
				LocalVariableAttribute.LocalVariable[] localVariables
						= new LocalVariableAttribute.LocalVariable[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					localVariables[i] = getLocalVariable(dis, "Descriptor");
				}
				yield new LocalVariableTableAttribute(constantPool, attributeNameIndex, attributeLength, numberOf,
						localVariables);
			}
			case "LocalVariableTypeTable" -> {
				U2 numberOf = readU2(dis);
				LocalVariableAttribute.LocalVariable[] localVariables
						= new LocalVariableAttribute.LocalVariable[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					localVariables[i] = getLocalVariable(dis, "Signature");
				}
				yield new LocalVariableTypeTableAttribute(constantPool, attributeNameIndex, attributeLength, numberOf,
						localVariables);
			}
			case "RuntimeVisibleAnnotations" -> {
				U2 numberOf = readU2(dis);
				RuntimeVisibleAnnotationsAttribute.RuntimeVisibleAnnotation[] annotations
						= new RuntimeVisibleAnnotationsAttribute.RuntimeVisibleAnnotation[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					annotations[i] = getAnnotation(dis);
				}
				yield new RuntimeVisibleAnnotationsAttribute(constantPool, attributeNameIndex, attributeLength, numberOf,
						annotations);
			}
			case "RuntimeInvisibleAnnotations" -> {
				U2 numberOf = readU2(dis);
				RuntimeVisibleAnnotationsAttribute.RuntimeVisibleAnnotation[] annotations
						= new RuntimeVisibleAnnotationsAttribute.RuntimeVisibleAnnotation[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					annotations[i] = getAnnotation(dis);
				}
				yield new RuntimeInvisibleAnnotationsAttribute(constantPool, attributeNameIndex, attributeLength, numberOf,
						annotations);
			}
			case "BootstrapMethods" -> {
				U2 numberOf = readU2(dis);
				BootstrapMethodsAttribute.BootstrapMethod[] bootstrapMethods = new BootstrapMethodsAttribute.BootstrapMethod[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					bootstrapMethods[i] = getBootstrapMethod(i, dis);
				}
				yield new BootstrapMethodsAttribute(constantPool, attributeNameIndex, attributeLength,
						numberOf, bootstrapMethods);
			}
			case "MethodParameters" -> {
				U1 numberOf = readU1(dis);
				MethodParameterAttribute.MethodParameter[] methodParameters = new MethodParameterAttribute.MethodParameter[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					methodParameters[i] = getMethodParameter(i, dis);
				}
				yield new MethodParameterAttribute(constantPool, attributeNameIndex, attributeLength,
						numberOf, methodParameters);
			}
			case "Module" -> {
				U2 moduleNameIndex = readU2(dis, true);
				U2 moduleFlags = readU2(dis);
				U2 moduleVersionIndex = readU2(dis, true);
				U2 requiresCount = readU2(dis);
				ModuleAttribute.Requires[] requires = new ModuleAttribute.Requires[requiresCount.getValue()];
				for (int i = 0; i < requiresCount.getValue(); i++) {
					requires[i] = readRequires(i, dis);
				}
				U2 exportsCount = readU2(dis);
				ModuleAttribute.Exports[] exports = new ModuleAttribute.Exports[exportsCount.getValue()];
				for (int i = 0; i < exportsCount.getValue(); i++) {
					exports[i] = readExports(i, dis);
				}
				U2 opensCount = readU2(dis);
				ModuleAttribute.Opens[] opens = new ModuleAttribute.Opens[opensCount.getValue()];
				for (int i = 0; i < opensCount.getValue(); i++) {
					opens[i] = readOpens(i, dis);
				}
				U2Array uses = readU2Array(dis);
				U2 providesCount = readU2(dis);
				ModuleAttribute.Provides[] provides = new ModuleAttribute.Provides[providesCount.getValue()];
				for (int i = 0; i < providesCount.getValue(); i++) {
					provides[i] = readProvides(i, dis);
				}
				yield new ModuleAttribute(constantPool, attributeNameIndex, attributeLength,
						moduleNameIndex, moduleFlags, moduleVersionIndex, requiresCount, requires, exportsCount, exports,
						opensCount, opens, uses, providesCount, provides);
			}
			case "ModulePackages" -> {
				U2Array packages = readU2Array(dis);
				yield new ModulePackagesAttribute(constantPool, attributeNameIndex, attributeLength, packages);

			}
			case "ModuleMainClass" -> {
				U2 aShort = readU2(dis, true);
				yield new ModuleMainClassAttribute(constantPool, attributeNameIndex, attributeLength, aShort);
			}
			case "NestHost" -> {
				U2 aShort = readU2(dis, true);
				yield new NestHostAttribute(constantPool, attributeNameIndex, attributeLength, aShort);
			}
			case "NestMembers" -> {
				U2Array classes = readU2Array(dis);
				yield new NestMembersAttribute(constantPool, attributeNameIndex, attributeLength, classes);
			}
			default -> {
				for (int j = 0; j < attributeLength.getValue(); j++) {
					readByte(dis);
				}
				attributeLength = new U4(attributeLength.getOffset(), 0);//mark non-implemented attr, to show in the test 
				yield new Attribute(constantPool, attributeNameIndex, attributeLength);
			}
		};
	}

	private U2Array readU2Array(DataInputStream dis) throws IOException {
		U2 numberOf = readU2(dis);
		int arrayLength = numberOf.getValue();
		U2[] array = new U2[arrayLength];
		for (int i = 0; i < arrayLength; i++) {
			array[i] = readU2(dis, true);
		}
		return new U2Array(numberOf, array);
	}

	private U1 readU1(DataInputStream dis) throws IOException {
		int value = dis.readUnsignedByte();
		U1 u1 = new U1(count, value);
		count += Byte.BYTES;
		return u1;
	}

	private U2 readU2(DataInputStream dis) throws IOException {
		return readU2(dis, false);
	}

	private U2 readU2(DataInputStream dis, boolean addSymbolicName) throws IOException {
		ConstantPoolEntry cpe = null;
		int value = dis.readUnsignedShort();
		if (addSymbolicName && !constantPool.isEmpty()) {
			if (value > 0) {
				cpe = constantPool.get(value);
			} else {
				if (constantObject == null) {
					for (ConstantPoolEntry entry : constantPool) {
						if (entry != null && "java/lang/Object".equals(entry.getAdditional())) {
							constantObject = entry;
							break;
						}
					}
					if (constantObject == null) {
						constantObject = new ConstantPoolUtf8(constantPool, 0, 0, ConstantTag.CONSTANT_Utf8, "java/lang/Object");
					}
				}
				cpe = constantObject;
			}
		}
		U2 u2 = new U2(count, value, cpe);
		count += Short.BYTES;
		return u2;
	}

	private U4 readU4(DataInputStream dis) throws IOException {
		int value = dis.readInt();
		U4 u4 = new U4(count, value);
		count += Integer.BYTES;
		return u4;
	}

	private Opcode readOpcode(DataInputStream dis, int startCodeCount) throws IOException {
		int offset = count;
		int opcode = readByte(dis);
		int size = Instruction.getArgumentsSize(opcode);
		List<Integer> arguments = new ArrayList<>();
		if (opcode == Instruction.WIDE.getCode()) {
			int additionalOpcode = readByte(dis);
			size = additionalOpcode == Instruction.IINC.getCode() ? 5 : 3;
			arguments.add(additionalOpcode);
		}
		if (opcode == Instruction.TABLESWITCH.getCode()) {
			size = getFirstBytePadding(startCodeCount);
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
		if (opcode == Instruction.LOOKUPSWITCH.getCode()) {
			size = getFirstBytePadding(startCodeCount);
			readNBytes(dis, arguments, size);
			size += 2 * U4.getSize();
			U4 defaultValue = readU4(dis);
			arguments.addAll(defaultValue.getIntList());
			U4 nPairs = readU4(dis);
			arguments.addAll(nPairs.getIntList());
			size += nPairs.getValue() * 2 * U4.getSize();
		}
		readNBytes(dis, arguments, size);
		return new Opcode(offset, opcode, arguments.stream().mapToInt(i -> i).toArray());
	}

	private int getFirstBytePadding(int startCodeCount) {
		return (count - startCodeCount) % 4;
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
		U2 bootstrapMethodRef = readU2(dis, true);
		U2Array bootstrapArguments = readU2Array(dis);
		return new BootstrapMethodsAttribute.BootstrapMethod(index, bootstrapMethodRef, bootstrapArguments);
	}

	private MethodParameterAttribute.MethodParameter getMethodParameter(int index, DataInputStream dis) throws IOException {
		U2 nameIndex = readU2(dis, true);
		U2 accessFlag = readU2(dis);
		return new MethodParameterAttribute.MethodParameter(index, nameIndex, accessFlag);
	}

	private ModuleAttribute.Requires readRequires(int index, DataInputStream dis) throws IOException {
		U2 requiresIndex = readU2(dis, true);
		U2 accessFlag = readU2(dis);
		U2 requiresVersionIndex = readU2(dis, true);
		return new ModuleAttribute.Requires(index, requiresIndex, accessFlag, requiresVersionIndex);
	}

	private ModuleAttribute.Exports readExports(int index, DataInputStream dis) throws IOException {
		U2 exportsIndex = readU2(dis, true);
		U2 accessFlag = readU2(dis);
		U2Array exportsToIndex = readU2Array(dis);
		return new ModuleAttribute.Exports(index, exportsIndex, accessFlag, exportsToIndex);
	}

	private ModuleAttribute.Opens readOpens(int index, DataInputStream dis) throws IOException {
		U2 opensIndex = readU2(dis, true);
		U2 accessFlag = readU2(dis);
		U2Array opensToIndex = readU2Array(dis);
		return new ModuleAttribute.Opens(index, opensIndex, accessFlag, opensToIndex);
	}

	private ModuleAttribute.Provides readProvides(int index, DataInputStream dis) throws IOException {
		U2 providesIndex = readU2(dis, true);
		U2Array providesWithIndex = readU2Array(dis);
		return new ModuleAttribute.Provides(index, providesIndex, providesWithIndex);
	}

	public InnerClassesAttribute.InnerClass getInnerClass(int index, DataInputStream dis) throws IOException {
		U2 innerClassInfoIndex = readU2(dis, true);
		U2 outerClassInfoIndex = readU2(dis, true);
		outerClassInfoIndex.clearCpe();
		U2 innerNameIndex = readU2(dis, true);
		innerNameIndex.clearCpe();
		U2 innerClassAccessFlags = readU2(dis);
		innerClassAccessFlags.clearCpe();
		return new InnerClassesAttribute.InnerClass(index, innerClassInfoIndex, outerClassInfoIndex, innerNameIndex, innerClassAccessFlags);
	}

	public LineNumberTableAttribute.LineNumber getLineNumber(int index, DataInputStream dis) throws IOException {
		U2 startPC = readU2(dis);
		U2 lineNumber = readU2(dis);
		return new LineNumberTableAttribute.LineNumber(index, startPC, lineNumber);
	}

	private LocalVariableAttribute.LocalVariable getLocalVariable(DataInputStream dis, String descriptorTitle) throws IOException {
		U2 startPC = readU2(dis);
		U2 length = readU2(dis);
		U2 nameIndex = readU2(dis, true);
		U2 descriptorIndex = readU2(dis, true);
		U2 index = readU2(dis);
		return new LocalVariableAttribute.LocalVariable(startPC, length, nameIndex, descriptorIndex, index, descriptorTitle);
	}

	private RuntimeVisibleAnnotationsAttribute.RuntimeVisibleAnnotation getAnnotation(DataInputStream dis) throws IOException {
		U2 typeIndex = readU2(dis, true).check(ConstantPoolUtf8.class);
		U2 lengthOfPair = readU2(dis);
		ValuePair[] valuePairs = new ValuePair[lengthOfPair.getValue()];
		for (int i = 0; i < lengthOfPair.getValue(); i++) {
			valuePairs[i] = readValuePair(dis);
		}
		return new RuntimeVisibleAnnotationsAttribute.RuntimeVisibleAnnotation(
				typeIndex, lengthOfPair, valuePairs);
	}

	private ValuePair readValuePair(DataInputStream dis) throws IOException {
		U2 nameIndex = readU2(dis, true);
		ElementValue elementValue = readElementValue(dis);
		return new ValuePair(nameIndex, elementValue);
	}

	private ElementValue readElementValue(DataInputStream dis) throws IOException {
		U2 u2First = null;
		U2 u2Second = null;
		ElementValue[] elementValues = null;
		RuntimeVisibleAnnotationsAttribute.RuntimeVisibleAnnotation annotation = null;
		U1 tag = readU1(dis);
		switch (TagValueItem.getTagValue(tag.getValue())) {
			case CONST_VALUE_INDEX, CLASS_INFO_INDEX -> u2First = readU2(dis, true);
			case ENUM_CONST_VALUE -> {
				u2First = readU2(dis, true);
				u2Second = readU2(dis, true);
			}
			case ANNOTATION_VALUE -> annotation = getAnnotation(dis);
			case ARRAY_VALUE -> {
				u2First = readU2(dis, true);
				elementValues = new ElementValue[u2First.getValue()];
				for (int i = 0; i < u2First.getValue(); i++) {
					elementValues[i] = readElementValue(dis);
				}
			}
		}
		return new ElementValue(tag, u2First, u2Second, annotation, elementValues);
	}

	public ExceptionsAttribute.Exception readException(DataInputStream dis) throws IOException {
		U2 startPc = readU2(dis);
		U2 endPc = readU2(dis);
		U2 handlerPc = readU2(dis);
		U2 catchType = readU2(dis, true).check(ConstantPoolString.class);
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
			return ((ConstantPoolUtf8) constants.get(stringIndex)).getUtf8();
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
			return ((ConstantPoolString) constants.get(classIndex)).getString();
		}

		public int getNameAndTypeIndex() {
			return nameAndTypeIndex;
		}

		public String getNameAndTypeIndexString() {
			return ((ConstantPoolNameAndType) constants.get(nameAndTypeIndex)).getAdditional();
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
			return ((ConstantPoolUtf8) constants.get(nameIndex)).getUtf8();
		}

		public int getDescriptorIndex() {
			return descriptorIndex;
		}

		public String getDescriptorIndexString() {
			return ((ConstantPoolUtf8) constants.get(descriptorIndex)).getUtf8();
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
					+ " (" + toHex(nameAndTypeIndex) + ") " + constants.get(nameAndTypeIndex).getAdditional();
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
					+ " (" + toHex(referenceIndex) + ")" + constants.get(referenceIndex).getAdditional();
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

	public class U1 {
		private final int offset;
		protected final int value;

		public U1(int offset, int value) {
			this.offset = offset;
			this.value = value;
		}

		public int getOffset() {
			return offset;
		}

		public int getValue() {
			return value;
		}

		public static int getSize() {
			return 1;
		}

		public List<Integer> getIntList() {
			return Arrays.stream(getByteArray()).boxed().toList();
		}

		public int[] getByteArray() {
			return new int[]{value & 0xFF};
		}

		public U1 check(Class<?> aClass) {
			return this;
		}
	}

	public class U2 extends U1 {
		private ConstantPoolEntry cpe;

		public U2(int offset, int value, ConstantPoolEntry cpe) {
			super(offset, value);
			this.cpe = cpe;
		}

		public static int getSize() {
			return 2;
		}

		public void clearCpe() {
			if (value == 0) {
				cpe = null;
			}
		}

		public ConstantPoolEntry getCpe() {
			return cpe;
		}

		@Override
		public int[] getByteArray() {
			return new int[]{(value >> 8) & 0xFF, value & 0xFF};
		}

		@Override
		public U2 check(Class<?> clazz) {
			if (value != 0 && !clazz.isInstance(getConstantPool().get(value))) {
				String message = String.format("%04X Value = %s expected a %s actual %s",
						getOffset(), value, clazz.getName(), getConstantPool().get(value));
				throw new RuntimeException(message);
			}
			return this;
		}
	}

	public class U4 extends U2 {
		public U4(int offset, int value) {
			super(offset, value, null);
		}

		public static int getSize() {
			return 4;
		}

		@Override
		public int[] getByteArray() {
			return new int[]{(value >> 24) & 0xFF, (value >> 16) & 0xFF, (value >> 8) & 0xFF, value & 0xFF};
		}
	}

	public record U2Array(U2 numberOf, U2[] array) {
	}
}
