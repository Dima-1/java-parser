import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <a href="https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.1">4.1. The ClassFile Structure</a>
 */
public class Parser {

	private final Print print;
	private int count = 0;
	private final List<ConstantPoolRecord> constants = new ArrayList<>();

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
			print.u2(getU2(dis), "Minor version");
			print.u2(getU2(dis), "Major version");
			U2 u2 = getU2(dis);
			print.u2(u2, "Constant pool count");
			readConstantPool(dis, u2.value);
			print.constantPool(constants);
			U2 accessFlags = getU2(dis);
			print.accessFlags(accessFlags, Type.CLASS);
			print.u2(getU2(dis, true), "This class");
			print.u2(getU2(dis, true), "Super class");
			u2 = getU2(dis);
			print.u2(u2, "Interfaces count");
			readInterfaces(dis, u2.value);
			u2 = getU2(dis);
			print.u2(u2, "Fields count");
			readFields(dis, u2.value);
			u2 = getU2(dis);
			print.u2(u2, "Methods count");
			readMethods(dis, u2.value);
			u2 = getU2(dis);
			print.u2(u2, "Attributes count");
			readAttributes(dis, u2.value);
		} catch (IOException e) {
			e.getMessage();
		}
	}

	private void readInterfaces(DataInputStream dis, int interfacesCount) throws IOException {
		for (int i = 0; i < interfacesCount; i++) {
			print.u2(getU2(dis, true), "");
		}
	}

	/**
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.5">4.5. Fields</a>
	 */
	private void readFields(DataInputStream dis, int fieldsCount) throws IOException {
		for (int i = 0; i < fieldsCount; i++) {
			U2 accessFlags = getU2(dis);
			print.accessFlags(accessFlags, Type.FIELD);
			print.u2(getU2(dis, true), "Name index");
			print.u2(getU2(dis, true), "Descriptor index");
			U2 u2 = getU2(dis);
			print.u2(u2, "Attributes count");
			readAttributes(dis, u2.value);
		}
	}

	/**
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.6">4.6. Methods</a>
	 */
	private void readMethods(DataInputStream dis, int methodsCount) throws IOException {
		for (int i = 0; i < methodsCount; i++) {
			U2 accessFlags = getU2(dis);
			print.accessFlags(accessFlags, Type.METHOD);
			print.u2(getU2(dis, true), "Name index");
			print.u2(getU2(dis, true), "Descriptor index");
			U2 u2 = getU2(dis);
			print.u2(u2, "Attributes count");
			readAttributes(dis, u2.value);
		}
	}

	/**
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.7">4.7. Attributes</a>
	 */
	private void readAttributes(DataInputStream dis, int attributesCount) throws IOException {
		for (int i = 0; i < attributesCount; i++) {
			print.u2(getU2(dis, true), "Name index");
			U4 attributeLength = getU4(dis);
			print.u4(attributeLength);
			for (int j = 0; j < attributeLength.value; j++) {
				dis.readByte();
				count++;
			}
		}
	}

	public enum ConstantPool {

		CONSTANT_Utf8(1, 2),                //1  45.3	1.0.2
		CONSTANT_Integer(3, 4),             //3  45.3	1.0.2
		CONSTANT_Float(4, 4),               //4  45.3	1.0.2
		CONSTANT_Long(5, 8),                //5  45.3	1.0.2
		CONSTANT_Double(6, 8),              //6  45.3	1.0.2
		CONSTANT_Class(7, 2),               //7  45.3	1.0.2
		CONSTANT_String(8, 2),              //8  45.3	1.0.2
		CONSTANT_Fieldref(9, 4),            //9  45.3	1.0.2
		CONSTANT_Methodref(10, 4),          //10 45.3	1.0.2
		CONSTANT_InterfaceMethodref(11, 4), //11 45.3	1.0.2
		CONSTANT_NameAndType(12, 4),        //12 45.3	1.0.2
		CONSTANT_MethodHandle(15, 3),       //15 51.0	7
		CONSTANT_MethodType(16, 2),         //16 51.0	7
		CONSTANT_Dynamic(17, 4),            //17 55.0	11
		CONSTANT_InvokeDynamic(18, 4),      //18 51.0	7
		CONSTANT_Module(19, 2),             //19 53.0	9
		CONSTANT_Package(20, 2);            //20 53.0	9

		private final int tag;
		private final int length;

		ConstantPool(int tag, int length) {
			this.tag = tag;
			this.length = length;
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
			if (cpr.cp.isTwoEntriesTakeUp()) {
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
			case CONSTANT_Utf8 -> {
				final int length = dis.readUnsignedShort();
				count += Short.BYTES;
				count += length;
				yield new ConstantPoolUtf8Record(offset, idx, cp, new String(dis.readNBytes(length)));
			}
			case CONSTANT_MethodHandle -> {
				final int referenceKind = dis.read();
				count++;
				int bShort = dis.readUnsignedShort();
				count += Short.BYTES;
				yield new ConstantPoolMethodHandle(offset, idx, cp, referenceKind, bShort);
			}
			default -> {
				count += cp.length;
				dis.readNBytes(cp.length);
				yield new ConstantPoolDefault(offset, idx, cp);
			}
		};
	}

	private U2 getU2(DataInputStream dis) throws IOException {
		return getU2(dis, false);
	}

	private U2 getU2(DataInputStream dis, boolean addSymbolicName) throws IOException {
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

	private U4 getU4(DataInputStream dis) throws IOException {
		int value = dis.readInt();
		U4 u4 = new U4(count, value, "");
		count += Integer.BYTES;
		return u4;
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
		int offset;
		int idx;
		ConstantPool cp;

		public ConstantPoolRecord(int offset, int idx, ConstantPool cp) {
			this.offset = offset;
			this.idx = idx;
			this.cp = cp;
		}

		abstract String getAdditional(List<ConstantPoolRecord> constants);
	}

	private static final class ConstantPoolString extends ConstantPoolRecord {
		int stringIndex;

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
		int classIndex;
		int nameAndTypeIndex;

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

	private static final class ConstantPoolUtf8Record extends ConstantPoolRecord {
		String UTF8;

		public ConstantPoolUtf8Record(int offset, int idx, ConstantPool cp, String UTF8) {
			super(offset, idx, cp);
			this.UTF8 = UTF8;
		}

		@Override
		String getAdditional(List<ConstantPoolRecord> constants) {
			return UTF8;
		}
	}

	private static final class ConstantPoolDefault extends ConstantPoolRecord {

		public ConstantPoolDefault(int offset, int idx, ConstantPool cp) {
			super(offset, idx, cp);
		}

		@Override
		String getAdditional(List<ConstantPoolRecord> constants) {
			return "" + cp.length;
		}
	}

	private static class ConstantPoolMethodHandle extends ConstantPoolRecord {
		private final int referenceKind;
		private final int referenceIndex;

		public ConstantPoolMethodHandle(int offset, int idx, ConstantPool cp, int referenceKind, int referenceIndex) {
			super(offset, idx, cp);
			this.referenceKind = referenceKind;
			this.referenceIndex = referenceIndex;
		}

		@Override
		String getAdditional(List<ConstantPoolRecord> constants) {
			return referenceKind + " (" + referenceIndex + ")" + constants.get(referenceIndex - 1).getAdditional(constants);
		}
	}

	public static class U2 {
		int offset;
		int value;
		String symbolic;

		public U2(int offset, int value, String symbolic) {
			this.offset = offset;
			this.value = value;
			this.symbolic = symbolic;
		}
	}

	public static class U4 extends U2 {
		public U4(int offset, int value, String symbolic) {
			super(offset, value, symbolic);
		}
	}
}
