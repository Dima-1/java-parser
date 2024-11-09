package com.example.jcparser;

import com.example.jcparser.attribute.*;
import com.example.jcparser.attribute.annotation.*;
import com.example.jcparser.attribute.instruction.CodeAttribute;
import com.example.jcparser.attribute.instruction.InstructionSet;
import com.example.jcparser.attribute.instruction.Instruction;
import com.example.jcparser.attribute.stackmapframe.*;
import com.example.jcparser.constantpool.*;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.jcparser.AccessFlag.Type.*;
import static com.example.jcparser.ConsoleColors.*;
import static com.example.jcparser.ConsoleColors.addColor;

/**
 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.1">4.1. The ClassFile Structure</a>
 */
public class Parser {

	private final Print print;
	private int count = 0;
	private final List<ConstantPoolEntry> constantPool = new ArrayList<>();
	private ConstantPoolEntry constantObject = null;
	private final List<Attribute> attributes = new ArrayList<>();

	public Parser(Print print) {
		this.print = print;
		print.getConstantFormater().setConstantPool(constantPool);
	}

	public static void main(String[] args) {
		File file;
		if (args.length == 0) {
			System.err.println(
					"""
							Usage [-c] [-r] </path/file.class>
							      -c Skip the print of the constant pool
							      -r Skip the print of the indexes of the constant pool""");
			System.exit(1);
		}
		Options options = new Options();
		String fileName = "";
		for (String arg : args) {
			if ("-c".equals(arg)) {
				options.setConstants(false);
			}
			if ("-r".equals(arg)) {
				options.setRefs(false);
			}
			if (!arg.startsWith("-")) {
				fileName = arg;
			}
		}
		file = new File(fileName);
		if (!file.exists()) {
			System.err.printf("File %s not exist\n", fileName);
			System.exit(1);
		}
		Parser parser = new Parser(new Print(options));
		parser.process(file);
	}

	public List<ConstantPoolEntry> getConstantPool() {
		return constantPool;
	}

	private void process(File file) {

		try (FileInputStream fis = new FileInputStream(file);
		     DataInputStream dis = new DataInputStream(fis)) {
			print.setOffsetWidth(file.length());
			Magic.checkMagic(dis);
			print.u4(new U4(0, ByteBuffer.wrap(Magic.bytes).getInt()), addColor(BLUE, "Magic"));
			count = Magic.BYTES;
			print.u2(readU2(dis), "Minor version", BLUE, true);
			print.u2(readU2(dis), "Major version", BLUE, true);
			U2 u2 = readU2(dis);
			print.u2(u2, "Constant pool count", BLUE, true);
			readConstantPool(dis, u2.value);
			print.constantPool(constantPool);
			U2 accessFlags = readU2(dis);
			print.accessFlags(accessFlags, CLASS);
			print.u2(readU2(dis, true), "This class");
			print.u2(readU2(dis, true), "Super class");
			readInterfaces(dis);
			readFields(dis);
			readMethods(dis);
			U2 attributesCount = readU2(dis);
			print.u2(attributesCount, "Attributes count", BLUE, true);
			if (attributesCount.value > 0) {
				attributes.addAll(readAttributes(dis, attributesCount.value, null));
				print.attributes(attributes);
			}
		} catch (IOException e) {
			e.getMessage();
		}
	}

	private void readInterfaces(DataInputStream dis) throws IOException {
		U2 u2 = readU2(dis);
		print.u2(u2, "Interfaces count", BLUE, true);
		int interfacesCount = u2.value;
		for (int i = 0; i < interfacesCount; i++) {
			print.u2(readU2(dis, true), "");
		}
	}

	/**
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.5">4.5. Fields</a>
	 */
	private void readFields(DataInputStream dis) throws IOException {
		readAdditionData(dis, FIELD);
	}

	/**
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.6">4.6. Methods</a>
	 */
	private void readMethods(DataInputStream dis) throws IOException {
		readAdditionData(dis, METHOD);
	}

	private void readAdditionData(DataInputStream dis, AccessFlag.Type type) throws IOException {
		String title = (type == FIELD ? "Fields" : "Methods") + " count";
		U2 u2 = readU2(dis);
		print.u2(u2, title, BLUE, true);
		int length = u2.value;
		for (int i = 0; i < length; i++) {
			U2 accessFlags = readU2(dis);
			print.accessFlags(accessFlags, type);
			print.u2(readU2(dis, true), "Name index");
			U2 descriptor = readU2(dis, true);
			print.u2(descriptor, "Descriptor index");
			u2 = readU2(dis);
			print.u2(u2, "Attributes count");
			List<Attribute> attributes = new ArrayList<>(readAttributes(dis, u2.value, descriptor));
			print.attributes(attributes);
		}
	}

	/**
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7">4.7. Attributes</a>
	 */
	private List<Attribute> readAttributes(DataInputStream dis, int attributesCount, U2 additional) throws IOException {
		List<Attribute> attributes = new ArrayList<>();
		for (int i = 0; i < attributesCount; i++) {
			Attribute attribute = readAttribute(dis, additional);
			attributes.add(attribute);
		}
		return attributes;
	}

	/**
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.4">4.4. The Constant Pool</a>
	 */
	private void readConstantPool(DataInputStream dis, int constantPoolCount) throws IOException {
		constantPool.add(null); //The constant_pool table is indexed from 1 to constant_pool_count - 1.
		for (int i = 1; i < constantPoolCount; i++) {
			ConstantPoolEntry entry = createEntry(i, dis);
			constantPool.add(entry);
			if (entry.getConstantTag().isTwoEntriesTakeUp()) {
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
				yield new ConstantPoolUtf8(offset, idx, constantTag, new String(dis.readNBytes(length)));
			}
			case CONSTANT_Integer -> {
				int anInt = dis.readInt();
				count += Integer.BYTES;
				yield new ConstantPoolInteger(offset, idx, constantTag, anInt);
			}
			case CONSTANT_Float -> {
				float aFloat = dis.readFloat();
				count += Float.BYTES;
				yield new ConstantPoolFloat(offset, idx, constantTag, aFloat);
			}
			case CONSTANT_Long -> {
				long aLong = dis.readLong();
				count += Long.BYTES;
				yield new ConstantPoolLong(offset, idx, constantTag, aLong);
			}
			case CONSTANT_Double -> {
				double aDouble = dis.readDouble();
				count += Double.BYTES;
				yield new ConstantPoolDouble(offset, idx, constantTag, aDouble);
			}
			case CONSTANT_Class, CONSTANT_String, CONSTANT_MethodType, CONSTANT_Module, CONSTANT_Package -> {
				int aShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolString(offset, idx, constantTag, aShort);
			}
			case CONSTANT_Fieldref, CONSTANT_Methodref, CONSTANT_InterfaceMethodref -> {
				int aShort = dis.readUnsignedShort();
				count += Short.BYTES;
				int bShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolMethodRef(offset, idx, constantTag, aShort, bShort);
			}
			case CONSTANT_NameAndType -> {
				int aShort = dis.readUnsignedShort();
				count += Short.BYTES;
				int bShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolNameAndType(offset, idx, constantTag, aShort, bShort);
			}
			case CONSTANT_Dynamic, CONSTANT_InvokeDynamic -> {
				int aShort = dis.readUnsignedShort();
				count += Short.BYTES;
				int bShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolDynamic(offset, idx, constantTag, aShort, bShort);
			}
			case CONSTANT_MethodHandle -> {
				int referenceKind = dis.read();
				count++;
				int bShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolMethodHandle(offset, idx, constantTag, referenceKind, bShort);
			}
		};
	}

	/**
	 * The implementation of the attributes follows the order shown in table 4.7-A
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7-300">
	 * Table 4.7-A. Predefined class file attributes (by section)</a>
	 */
	private Attribute readAttribute(DataInputStream dis, U2 additional) throws IOException {
		U2 attributeNameIndex = readU2(dis, true);
		String name = ((ConstantPoolUtf8) constantPool.get(attributeNameIndex.getValue())).getUtf8();
		U4 attributeLength = readU4(dis);

		return switch (name) {
			case "ConstantValue" -> {
				Class<? extends ConstantPoolEntry> clazz = getClass(additional);
				U2 constantValueIndex = readU2(dis, true).check(clazz);
				yield new ConstantValueAttribute(attributeNameIndex, attributeLength, constantValueIndex);
			}
			case "Code" -> {
				U2 maxStack = readU2(dis);
				U2 maxLocals = readU2(dis);
				U4 codeLength = readU4(dis);
				List<Instruction> instructions = new ArrayList<>();
				int startCodeCount = codeLength.getOffset() + U4.BYTES;
				int endCodeCount = startCodeCount + codeLength.getValue();
				do {
					instructions.add(readInstruction(dis, startCodeCount));
				} while (count < endCodeCount);
				U2 exceptionTableLength = readU2(dis);
				ExceptionsAttribute.Exception[] exceptions = new ExceptionsAttribute.Exception[exceptionTableLength.getValue()];
				for (int i = 0; i < exceptionTableLength.getValue(); i++) {
					exceptions[i] = readException(dis);
				}
				U2 numberOf = readU2(dis);
				List<Attribute> attributes = new ArrayList<>(readAttributes(dis, numberOf.value, null));
				yield new CodeAttribute(attributeNameIndex, attributeLength, maxStack, maxLocals,
						codeLength, instructions, exceptionTableLength, exceptions, numberOf, attributes);
			}
			case "StackMapTable" -> {
				U2 numberOf = readU2(dis);
				List<StackMapFrame> entries = new ArrayList<>();
				for (int i = 0; i < numberOf.getValue(); i++) {
					entries.add(readStackMapFrame(dis));
				}
				yield new StackMapTableAttribute(attributeNameIndex, attributeLength, numberOf, entries);
			}
			case "Exceptions" -> {
				U2Array exceptions = readU2Array(dis);
				for (U2 exception : exceptions.array()) {
					ConstantPoolEntry cpe = constantPool.get(exception.getValue());
					if (!cpe.getConstantTag().isConstantClass()) {
						String message = String.format("%04X ExceptionValue = %s", exception.getOffset(), cpe.getConstantTag());
						throw new RuntimeException(message);
					}
				}
				yield new ExceptionsAttribute(attributeNameIndex, attributeLength, exceptions);
			}
			case "InnerClasses" -> {
				U2 numberOf = readU2(dis);
				InnerClassesAttribute.InnerClass[] classes = new InnerClassesAttribute.InnerClass[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					classes[i] = getInnerClass(i, dis);
				}
				yield new InnerClassesAttribute(attributeNameIndex, attributeLength,
						numberOf, classes);
			}
			case "EnclosingMethod" -> {
				U2 classIndex = readU2(dis, true);
				U2 methodIndex = readU2(dis, true);
				yield new EnclosingMethodAttribute(attributeNameIndex, attributeLength,
						classIndex, methodIndex);
			}
			case "Synthetic", "Deprecated" -> new Attribute(attributeNameIndex, attributeLength);
			case "Signature" -> {
				U2 aShort = readU2(dis, true);
				yield new SignatureAttribute(attributeNameIndex, attributeLength, aShort);
			}
			case "SourceFile" -> {
				U2 aShort = readU2(dis, true);
				yield new SourceFileAttribute(attributeNameIndex, attributeLength, aShort);
			}
			case "SourceDebugExtension" -> {
				String utf8 = new String(dis.readNBytes(attributeLength.getValue()));
				yield new SourceDebugExtensionAttribute(attributeNameIndex, attributeLength, utf8);
			}
			case "LineNumberTable" -> {
				U2 numberOf = readU2(dis);
				LineNumberTableAttribute.LineNumber[] lineNumbers = new LineNumberTableAttribute.LineNumber[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					lineNumbers[i] = getLineNumber(i, dis);
				}
				yield new LineNumberTableAttribute(attributeNameIndex, attributeLength, numberOf,
						lineNumbers);
			}
			case "LocalVariableTable" -> {
				U2 numberOf = readU2(dis);
				LocalVariableAttribute.LocalVariable[] localVariables
						= new LocalVariableAttribute.LocalVariable[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					localVariables[i] = getLocalVariable(dis, "Descriptor");
				}
				yield new LocalVariableTableAttribute(attributeNameIndex, attributeLength, numberOf,
						localVariables);
			}
			case "LocalVariableTypeTable" -> {
				U2 numberOf = readU2(dis);
				LocalVariableAttribute.LocalVariable[] localVariables
						= new LocalVariableAttribute.LocalVariable[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					localVariables[i] = getLocalVariable(dis, "Signature");
				}
				yield new LocalVariableTypeTableAttribute(attributeNameIndex, attributeLength, numberOf,
						localVariables);
			}
			case "RuntimeVisibleAnnotations" ->
					getRuntimeAnnotationsAttribute(dis, attributeNameIndex, attributeLength, true);
			case "RuntimeInvisibleAnnotations" ->
					getRuntimeAnnotationsAttribute(dis, attributeNameIndex, attributeLength, false);
			case "RuntimeVisibleParameterAnnotations" ->
					getParameterAnnotations(dis, attributeNameIndex, attributeLength, true);
			case "RuntimeInvisibleParameterAnnotations" ->
					getParameterAnnotations(dis, attributeNameIndex, attributeLength, false);
			case "RuntimeVisibleTypeAnnotations" -> null; //todo
			case "RuntimeInvisibleTypeAnnotations" -> null; //todo
			
			case "AnnotationDefault" -> {
				ElementValue elementValue = readElementValue(dis);
				yield new AnnotationDefaultAttribute(attributeNameIndex, attributeLength, elementValue);
			}
			case "BootstrapMethods" -> {
				U2 numberOf = readU2(dis);
				BootstrapMethodsAttribute.BootstrapMethod[] bootstrapMethods = new BootstrapMethodsAttribute.BootstrapMethod[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					bootstrapMethods[i] = getBootstrapMethod(i, dis);
				}
				yield new BootstrapMethodsAttribute(attributeNameIndex, attributeLength,
						numberOf, bootstrapMethods);
			}
			case "MethodParameters" -> {
				U1 numberOf = readU1(dis);
				MethodParameterAttribute.MethodParameter[] methodParameters = new MethodParameterAttribute.MethodParameter[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					methodParameters[i] = getMethodParameter(i, dis);
				}
				yield new MethodParameterAttribute(attributeNameIndex, attributeLength,
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
				yield new ModuleAttribute(attributeNameIndex, attributeLength,
						moduleNameIndex, moduleFlags, moduleVersionIndex, requiresCount, requires, exportsCount, exports,
						opensCount, opens, uses, providesCount, provides);
			}
			case "ModulePackages" -> {
				U2Array packages = readU2Array(dis);
				yield new ModulePackagesAttribute(attributeNameIndex, attributeLength, packages);
			}
			case "ModuleMainClass" -> {
				U2 aShort = readU2(dis, true);
				yield new ModuleMainClassAttribute(attributeNameIndex, attributeLength, aShort);
			}
			case "NestHost" -> {
				U2 aShort = readU2(dis, true);
				yield new NestHostAttribute(attributeNameIndex, attributeLength, aShort);
			}
			case "NestMembers" -> {
				U2Array classes = readU2Array(dis);
				yield new NestMembersAttribute(attributeNameIndex, attributeLength, classes);
			}
			case "Record" -> {
				U2 numberOf = readU2(dis);
				RecordAttribute.ComponentInfo[] components = new RecordAttribute.ComponentInfo[numberOf.value];
				for (int i = 0; i < numberOf.getValue(); i++) {
					U2 nameIndex = readU2(dis, true);
					U2 descriptionIndex = readU2(dis, true);
					U2 numberOfComponents = readU2(dis);
					List<Attribute> attributes = new ArrayList<>(readAttributes(dis, numberOfComponents.value, null));
					components[i] = new RecordAttribute.ComponentInfo(nameIndex, descriptionIndex, numberOfComponents,
							attributes);
				}
				yield new RecordAttribute(attributeNameIndex, attributeLength, numberOf, components);
			}
			case "PermittedSubclasses" -> {
				U2Array classes = readU2Array(dis);
				yield new PermittedSubclassesAttribute(attributeNameIndex, attributeLength, classes);
			}
			default -> {
				for (int j = 0; j < attributeLength.getValue(); j++) {
					readByte(dis);
				}
				attributeLength = new U4(attributeLength.getOffset(), 0);//mark non-implemented attr, to show in the test 
				yield new Attribute(attributeNameIndex, attributeLength);
			}
		};
	}

	private Class<? extends ConstantPoolEntry> getClass(U2 additional) {
		ConstantPoolEntry cpe = constantPool.get(additional.value);
		Class<? extends ConstantPoolEntry> clazz = null;
		if (cpe instanceof ConstantPoolUtf8 cpeUtf8) {
			clazz = switch (cpeUtf8.getUtf8()) {
				case "F" -> ConstantPoolFloat.class;
				case "J" -> ConstantPoolLong.class;
				case "D" -> ConstantPoolDouble.class;
				case "Ljava/lang/String;" -> ConstantPoolString.class;
				case "I", "S", "C", "B", "Z" -> ConstantPoolInteger.class;
				default -> throw new IllegalStateException("Unexpected value: " + cpeUtf8.getUtf8());
			};
		}
		return clazz;
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
		count += U1.BYTES;
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
						if (entry instanceof ConstantPoolUtf8 utf8 && "java/lang/Object".equals(utf8.getUtf8())) {
							constantObject = entry;
							break;
						}
					}
					if (constantObject == null) {
						constantObject = new ConstantPoolUtf8(0, 0, ConstantTag.CONSTANT_Utf8, "java/lang/Object");
					}
				}
				cpe = constantObject;
			}
		}
		U2 u2 = new U2(count, value, cpe);
		count += U2.BYTES;
		return u2;
	}

	private U4 readU4(DataInputStream dis) throws IOException {
		int value = dis.readInt();
		U4 u4 = new U4(count, value);
		count += U4.BYTES;
		return u4;
	}

	private Instruction readInstruction(DataInputStream dis, int startCodeCount) throws IOException {
		int offset = count;
		int opcode = readByte(dis);
		int size = InstructionSet.getOperandsSize(opcode);
		List<Integer> operands = new ArrayList<>();
		if (opcode == InstructionSet.WIDE.getOpcode()) {
			int additionalOpcode = readByte(dis);
			size = additionalOpcode == InstructionSet.IINC.getOpcode() ? 5 : 3;
			operands.add(additionalOpcode);
		}
		if (opcode == InstructionSet.TABLESWITCH.getOpcode()) {
			size = Instruction.getFirstBytePadding(offset, startCodeCount);
			readNBytes(dis, operands, size);
			size += 3 * U4.BYTES;
			U4 defaultValue = readU4(dis);
			operands.addAll(defaultValue.getIntList());
			U4 low = readU4(dis);
			operands.addAll(low.getIntList());
			U4 high = readU4(dis);
			operands.addAll(high.getIntList());
			size += (high.getValue() - low.getValue() + 1) * U4.BYTES;
		}
		if (opcode == InstructionSet.LOOKUPSWITCH.getOpcode()) {
			size = Instruction.getFirstBytePadding(offset, startCodeCount);
			readNBytes(dis, operands, size);
			size += 2 * U4.BYTES;
			U4 defaultValue = readU4(dis);
			operands.addAll(defaultValue.getIntList());
			U4 nPairs = readU4(dis);
			operands.addAll(nPairs.getIntList());
			size += nPairs.getValue() * 2 * U4.BYTES;
		}
		readNBytes(dis, operands, size);
		int[] operandsArray = operands.stream().mapToInt(i -> i).toArray();
		return new Instruction(offset, opcode, operandsArray);
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
		U1 tag = readU1(dis);
		U2 typeInfoAdditional = null;
		TypeInfo.Type tagType = TypeInfo.Type.getTagType(tag.getValue());
		if (tagType == TypeInfo.Type.ITEM_Object) {
			typeInfoAdditional = readU2(dis, true);
		} else if (tagType == TypeInfo.Type.ITEM_Uninitialized) {
			typeInfoAdditional = readU2(dis);
		}
		return new TypeInfo(tag, typeInfoAdditional);
	}

	private RuntimeAnnotationsAttribute getRuntimeAnnotationsAttribute(DataInputStream dis, U2 attributeNameIndex,
	                                                                   U4 attributeLength, boolean visible) throws IOException {
		ParameterAnnotation result = getParameterAnnotation(dis, visible);
		return new RuntimeAnnotationsAttribute(attributeNameIndex, attributeLength, result.numberOf(),
				result.annotations(), visible);
	}

	private Attribute getParameterAnnotations(DataInputStream dis, U2 attributeNameIndex,
	                                          U4 attributeLength, boolean visible) throws IOException {
		U1 numberOf = readU1(dis);
		ParameterAnnotation[] parameterAnnotations
				= new ParameterAnnotation[numberOf.getValue()];
		for (int i = 0; i < numberOf.getValue(); i++) {
			parameterAnnotations[i] = getParameterAnnotation(dis, visible);
		}
		return new RuntimeParameterAnnotationsAttribute(attributeNameIndex, attributeLength, numberOf,
				parameterAnnotations, visible);
	}

	private ParameterAnnotation getParameterAnnotation(DataInputStream dis, boolean visible) throws IOException {
		U2 numberOf = readU2(dis);
		RuntimeAnnotationsAttribute.Annotation[] annotations
				= new RuntimeAnnotationsAttribute.Annotation[numberOf.getValue()];
		for (int i = 0; i < numberOf.getValue(); i++) {
			annotations[i] = getAnnotation(dis);
		}
		return new ParameterAnnotation(numberOf, annotations, visible);
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

	private RuntimeAnnotationsAttribute.Annotation getAnnotation(DataInputStream dis) throws IOException {
		U2 typeIndex = readU2(dis, true).check(ConstantPoolUtf8.class);
		U2 lengthOfPair = readU2(dis);
		ValuePair[] valuePairs = new ValuePair[lengthOfPair.getValue()];
		for (int i = 0; i < lengthOfPair.getValue(); i++) {
			valuePairs[i] = readValuePair(dis);
		}
		return new RuntimeAnnotationsAttribute.Annotation(
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
		RuntimeAnnotationsAttribute.Annotation annotation = null;
		U1 tag = readU1(dis);
		switch (TagValueItem.getTagValue(tag.getValue())) {
			case CONST_VALUE_INDEX, CLASS_INFO_INDEX -> u2First = readU2(dis, true);
			case ENUM_CONST_VALUE -> {
				u2First = readU2(dis, true);
				u2Second = readU2(dis, true);
			}
			case ANNOTATION_VALUE -> annotation = getAnnotation(dis);
			case ARRAY_VALUE -> {
				u2First = readU2(dis);
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

	public static final class Magic {
		static final byte[] bytes = {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};
		static final int BYTES = bytes.length;

		private static void checkMagic(DataInputStream dis) throws IOException {
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

	public static class U1 {
		public static final int BYTES = 1;
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
		public static final int BYTES = 2;
		private ConstantPoolEntry cpe;

		public U2(int offset, int value, ConstantPoolEntry cpe) {
			super(offset, value);
			this.cpe = cpe;
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
			if (value != 0 && clazz != null && !clazz.isInstance(getConstantPool().get(value))) {
				String message = String.format("%04X Value = %s expected a %s actual %s",
						getOffset(), value, clazz.getName(), getConstantPool().get(value));
				throw new RuntimeException(message);
			}
			return this;
		}
	}

	public class U4 extends U2 {
		public static final int BYTES = 4;

		public U4(int offset, int value) {
			super(offset, value, null);
		}

		@Override
		public int[] getByteArray() {
			return new int[]{(value >> 24) & 0xFF, (value >> 16) & 0xFF, (value >> 8) & 0xFF, value & 0xFF};
		}
	}

	public record U2Array(U2 numberOf, U2[] array) {
	}
}
