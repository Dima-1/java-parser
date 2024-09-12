import java.io.*;
import java.util.*;

/**
 * <a href="https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.1">4.1. The ClassFile Structure</a>
 */
public class Parser {

	private final Print print;
	private int count = 0;
	private final List<ConstantPoolRecord> constants = new ArrayList<>();
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
			count = Magic.BYTES;
			Magic.getMagic(dis);
			print.u2(readU2(dis), "Minor version", ConsoleColors.BLUE, true);
			print.u2(readU2(dis), "Major version", ConsoleColors.BLUE, true);
			U2 u2 = readU2(dis);
			print.u2(u2, "Constant pool count", ConsoleColors.BLUE, true);
			readConstantPool(dis, u2.value);
			print.constantPool(constants);
			U2 accessFlags = readU2(dis);
			print.accessFlags(accessFlags, Type.CLASS);
			print.u2(readU2(dis, true), "This class");
			print.u2(readU2(dis, true), "Super class");
			u2 = readU2(dis);
			print.u2(u2, "Interfaces count", ConsoleColors.BLUE);
			readInterfaces(dis, u2.value);
			u2 = readU2(dis);
			print.u2(u2, "Fields count", ConsoleColors.BLUE);
			readFields(dis, u2.value);
			u2 = readU2(dis);
			print.u2(u2, "Methods count", ConsoleColors.BLUE);
			readMethods(dis, u2.value);
			u2 = readU2(dis);
			print.u2(u2, "Attributes count", ConsoleColors.BLUE);
			readAttributesClass(dis, u2.value);
			print.attributes(attributes);
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
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.5">4.5. Fields</a>
	 */
	private void readFields(DataInputStream dis, int fieldsCount) throws IOException {
		for (int i = 0; i < fieldsCount; i++) {
			U2 accessFlags = readU2(dis);
			print.accessFlags(accessFlags, Type.FIELD);
			print.u2(readU2(dis, true), "Name index");
			print.u2(readU2(dis, true), "Descriptor index");
			U2 u2 = readU2(dis);
			print.u2(u2, "Attributes count");
			readAttributes(dis, u2.value);
		}
	}

	/**
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.6">4.6. Methods</a>
	 */
	private void readMethods(DataInputStream dis, int methodsCount) throws IOException {
		for (int i = 0; i < methodsCount; i++) {
			U2 accessFlags = readU2(dis);
			print.accessFlags(accessFlags, Type.METHOD);
			print.u2(readU2(dis, true), "Name index");
			print.u2(readU2(dis, true), "Descriptor index");
			U2 u2 = readU2(dis);
			print.u2(u2, "Attributes count");
			readAttributes(dis, u2.value);
		}
	}

	/**
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.7">4.7. Attributes</a>
	 */
	private void readAttributes(DataInputStream dis, int attributesCount) throws IOException {
		for (int i = 0; i < attributesCount; i++) {
			print.u2(readU2(dis, true), "Attribute name index");
			U4 attributeLength = readU4(dis);
			print.u4(attributeLength);
			for (int j = 0; j < attributeLength.getValue(); j++) {
				dis.readByte();
				count++;
			}
		}
	}

	private void readAttributesClass(DataInputStream dis, int attributesCount) throws IOException {
		for (int i = 0; i < attributesCount; i++) {
			Attribute attribute = readAttribute(dis);
			attributes.put(attribute.getName(), attribute);
		}
	}

	public enum ConstantPool {

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

		ConstantPool(int tag) {
			this.tag = tag;
		}

		boolean isTwoEntriesTakeUp() {
			return this == CONSTANT_Double | this == CONSTANT_Long;
		}

		static ConstantPool getConstant(int tag) {
			for (ConstantPool cp : values()) {
				if (cp.tag == tag) {
					return cp;
				}
			}
			throw new RuntimeException("Wrong constant tag : " + tag);
		}
	}

	/**
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.4">4.4. The Constant Pool</a>
	 */
	private void readConstantPool(DataInputStream dis, int constantPoolCount) throws IOException {
		for (int i = 1; i < constantPoolCount; i++) {
			ConstantPoolRecord cpr = getCPRecord(i, dis);
			constants.add(cpr);
			if (cpr.record.isTwoEntriesTakeUp()) {
				constants.add(null);
				i++;
			}
		}
	}

	private ConstantPoolRecord getCPRecord(int idx, DataInputStream dis) throws IOException {
		int offset = count;
		int tag = dis.read();
		count++;
		ConstantPool cp = ConstantPool.getConstant(tag);

		return switch (cp) {
			case CONSTANT_Utf8 -> {
				final int length = dis.readUnsignedShort();
				count += Short.BYTES;
				count += length;
				yield new ConstantPoolUtf8Record(offset, idx, cp, new String(dis.readNBytes(length)));
			}
			case CONSTANT_Integer -> {
				int anInt = dis.readInt();
				count += Integer.BYTES;
				yield new ConstantPoolInteger(offset, idx, cp, anInt);
			}
			case CONSTANT_Float -> {
				float aFloat = dis.readFloat();
				count += Long.BYTES;
				yield new ConstantPoolFloat(offset, idx, cp, aFloat);
			}
			case CONSTANT_Long -> {
				long aLong = dis.readLong();
				count += Long.BYTES;
				yield new ConstantPoolLong(offset, idx, cp, aLong);
			}
			case CONSTANT_Double -> {
				double aDouble = dis.readDouble();
				count += Double.BYTES;
				yield new ConstantPoolDouble(offset, idx, cp, aDouble);
			}
			case CONSTANT_Class, CONSTANT_String, CONSTANT_MethodType, CONSTANT_Module, CONSTANT_Package -> {
				final int aShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolString(offset, idx, cp, aShort);
			}
			case CONSTANT_Fieldref, CONSTANT_Methodref, CONSTANT_InterfaceMethodref, CONSTANT_NameAndType -> {
				final int aShort = dis.readUnsignedShort();
				count += Short.BYTES;
				int bShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolMethodRef(offset, idx, cp, aShort, bShort);
			}
			case CONSTANT_Dynamic, CONSTANT_InvokeDynamic -> {
				final int aShort = dis.readUnsignedShort();
				count += Short.BYTES;
				int bShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolDynamic(offset, idx, cp, aShort, bShort);
			}
			case CONSTANT_MethodHandle -> {
				final int referenceKind = dis.read();
				count++;
				int bShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolMethodHandle(offset, idx, cp, referenceKind, bShort);
			}
		};
	}

	private Attribute readAttribute(DataInputStream dis) throws IOException {
		U2 attributeNameIndex = readU2(dis, true);
		String name = constants.get(attributeNameIndex.getValue() - 1).getAdditional(constants);
		U4 attributeLength = readU4(dis);

		return switch (name) {
			case "SourceFile" -> {
				final U2 aShort = readU2(dis, true);
				yield new Attribute.SourceFileAttribute(constants, attributeNameIndex, attributeLength, aShort);
			}
			case "NestMembers" -> {
				final U2 numberOf = readU2(dis);
				U2[] classes = new U2[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					classes[i] = readU2(dis, true);
				}
				yield new Attribute.NestMembersAttribute(constants, attributeNameIndex, attributeLength,
						numberOf, classes);
			}
			case "InnerClasses" -> {
				final U2 numberOf = readU2(dis);
				Attribute.InnerClass[] classes = new Attribute.InnerClass[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					classes[i] = getInnerClass(dis);
				}
				yield new Attribute.InnerClassesAttribute(constants, attributeNameIndex, attributeLength,
						numberOf, classes);
			}
			case "BootstrapMethods" -> {
				final U2 numberOf = readU2(dis);
				Attribute.BootstrapMethod[] bootstrapMethods = new Attribute.BootstrapMethod[numberOf.getValue()];
				for (int i = 0; i < numberOf.getValue(); i++) {
					bootstrapMethods[i] = getBootstrapMethod(i, dis);
				}
				yield new Attribute.BootstrapMethodsAttribute(constants, attributeNameIndex, attributeLength,
						numberOf, bootstrapMethods);
			}
			default -> {
				for (int j = 0; j < attributeLength.getValue(); j++) {
					dis.readByte();
					count++;
				}
				yield new Attribute(constants, attributeNameIndex, attributeLength);
			}
		};
	}

	private U2 readU2(DataInputStream dis) throws IOException {
		return readU2(dis, false);
	}

	private U2 readU2(DataInputStream dis, boolean addSymbolicName) throws IOException {
		String symbolic = "";
		int value = dis.readUnsignedShort();
		if (addSymbolicName && !constants.isEmpty()) {
			if (value > 0) {
				symbolic = " " + constants.get(value - 1).getAdditional(constants);
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

	public Attribute.BootstrapMethod getBootstrapMethod(int index, DataInputStream dis) throws IOException {
		final U2 bootstrapMethodRef = readU2(dis, true);
		final U2 numberOf = readU2(dis);
		U2[] bootstrapArguments = new U2[numberOf.getValue()];
		for (int i = 0; i < numberOf.getValue(); i++) {
			bootstrapArguments[i] = readU2(dis, true);
		}
		return new Attribute.BootstrapMethod(index, bootstrapMethodRef, bootstrapArguments);
	}

	public Attribute.InnerClass getInnerClass(DataInputStream dis) throws IOException {
		final U2 innerClassInfoIndex = readU2(dis, true);
		final U2 outerClassInfoIndex = readU2(dis, true);
		outerClassInfoIndex.clearZeroSymbolic();
		final U2 innerNameIndex = readU2(dis, true);
		innerNameIndex.clearZeroSymbolic();
		final U2 innerClassAccessFlags = readU2(dis, true);
		innerClassAccessFlags.clearZeroSymbolic();
		return new Attribute.InnerClass(innerClassInfoIndex, outerClassInfoIndex, innerNameIndex, innerClassAccessFlags);
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

	public abstract static class ConstantPoolRecord {
		private final int offset;
		private final int idx;
		private final ConstantPool record;

		public ConstantPoolRecord(int offset, int idx, ConstantPool record) {
			this.offset = offset;
			this.idx = idx;
			this.record = record;
		}

		abstract String getAdditional(List<ConstantPoolRecord> constants);

		public int getOffset() {
			return offset;
		}

		public int getIdx() {
			return idx;
		}

		public ConstantPool getRecord() {
			return record;
		}
	}

	private static final class ConstantPoolUtf8Record extends ConstantPoolRecord {
		private final String UTF8;

		public ConstantPoolUtf8Record(int offset, int idx, ConstantPool cp, String UTF8) {
			super(offset, idx, cp);
			this.UTF8 = UTF8;
		}

		@Override
		String getAdditional(List<ConstantPoolRecord> constants) {
			return UTF8;
		}
	}

	private static class ConstantPoolInteger extends ConstantPoolRecord {
		private final int value;

		public ConstantPoolInteger(int offset, int idx, ConstantPool cp, int value) {
			super(offset, idx, cp);
			this.value = value;
		}

		@Override
		String getAdditional(List<ConstantPoolRecord> constants) {
			return String.valueOf(value);
		}
	}

	private static class ConstantPoolFloat extends ConstantPoolRecord {
		private final float value;

		public ConstantPoolFloat(int offset, int idx, ConstantPool cp, float value) {
			super(offset, idx, cp);
			this.value = value;
		}

		@Override
		String getAdditional(List<ConstantPoolRecord> constants) {
			return String.valueOf(value);
		}
	}

	private static class ConstantPoolLong extends ConstantPoolRecord {
		private final long value;

		public ConstantPoolLong(int offset, int idx, ConstantPool cp, long value) {
			super(offset, idx, cp);
			this.value = value;
		}

		@Override
		String getAdditional(List<ConstantPoolRecord> constants) {
			return String.valueOf(value);
		}
	}

	private static class ConstantPoolDouble extends ConstantPoolRecord {
		private final double value;

		public ConstantPoolDouble(int offset, int idx, ConstantPool cp, double value) {
			super(offset, idx, cp);
			this.value = value;
		}

		@Override
		String getAdditional(List<ConstantPoolRecord> constants) {
			return String.valueOf(value);
		}
	}

	private static final class ConstantPoolString extends ConstantPoolRecord {
		private final int stringIndex;

		public ConstantPoolString(int offset, int idx, ConstantPool cp, int stringIndex) {
			super(offset, idx, cp);
			this.stringIndex = stringIndex;
		}

		@Override
		String getAdditional(List<ConstantPoolRecord> constants) {
			return "(" + stringIndex + ") " + constants.get(stringIndex - 1).getAdditional(constants);
		}
	}

	private static final class ConstantPoolMethodRef extends ConstantPoolRecord {
		private final int classIndex;
		private final int nameAndTypeIndex;

		public ConstantPoolMethodRef(int offset, int idx, ConstantPool cp, int classIndex, int nameAndTypeIndex) {
			super(offset, idx, cp);
			this.classIndex = classIndex;
			this.nameAndTypeIndex = nameAndTypeIndex;
		}

		@Override
		String getAdditional(List<ConstantPoolRecord> constants) {
			return "(" + classIndex + ") " + constants.get(classIndex - 1).getAdditional(constants)
					+ " (" + nameAndTypeIndex + ") " + constants.get(nameAndTypeIndex - 1).getAdditional(constants);
		}
	}

	private static final class ConstantPoolDynamic extends ConstantPoolRecord {
		private final int bootstrapMethodAttrIndex;
		private final int nameAndTypeIndex;

		public ConstantPoolDynamic(int offset, int idx, ConstantPool cp, int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
			super(offset, idx, cp);
			this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
			this.nameAndTypeIndex = nameAndTypeIndex;
		}

		@Override
		String getAdditional(List<ConstantPoolRecord> constants) {
			return "(" + bootstrapMethodAttrIndex + ") "
					+ " (" + nameAndTypeIndex + ") " + constants.get(nameAndTypeIndex - 1).getAdditional(constants);
		}
	}

	private static class ConstantPoolMethodHandle extends ConstantPoolRecord {

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

		public ConstantPoolMethodHandle(int offset, int idx, ConstantPool cp, int referenceKind, int referenceIndex) {
			super(offset, idx, cp);
			this.referenceKind = referenceKind;
			this.referenceIndex = referenceIndex;
		}

		@Override
		String getAdditional(List<ConstantPoolRecord> constants) {
			return MHRef.values()[referenceKind].name().replaceFirst("REF_", "")
					+ " (" + referenceIndex + ")" + constants.get(referenceIndex - 1).getAdditional(constants);
		}
	}

	public static class U2 {
		private final int offset;
		private final int value;
		private String symbolic;

		public U2(int offset, int value, String symbolic) {
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

		public void clearZeroSymbolic() {
			if (value == 0) {
				symbolic = "";
			}
		}
	}

	public static class U4 extends U2 {
		public U4(int offset, int value, String symbolic) {
			super(offset, value, symbolic);
		}
	}
}
