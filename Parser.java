import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*ClassFile {
	u4             magic;
	u2             minor_version;
	u2             major_version;
	u2             constant_pool_count;
	cp_info        constant_pool[constant_pool_count-1];
	u2             access_flags;
	u2             this_class;
	u2             super_class;
	u2             interfaces_count;
	u2             interfaces[interfaces_count];
	u2             fields_count;
	field_info     fields[fields_count];
	u2             methods_count;
	method_info    methods[methods_count];
	u2             attributes_count;
	attribute_info attributes[attributes_count];
}*/

public class Parser {

	private int count = 0;
	List<ConstantPoolRecord> constants = new ArrayList<>();

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Use [/path/file.class] as first argument");
			System.exit(0);
		}
		Parser parser = new Parser();
		parser.process(args[0]);
	}

	private void process(String fileName) {
		File file = new File(fileName);
		try (FileInputStream fis = new FileInputStream(file)) {
			count = Magic.BYTES;
			Magic.getMagic(fis);
			getU2(fis, "Minor version");
			getU2(fis, "Major version");
			int constantPoolCount = getU2(fis, "Constant pool count");
			readConstantPool(fis, constantPoolCount);
			getU2(fis, "Access flags");
			getU2(fis, "This class", true);
			getU2(fis, "Super class", true);
			int interfacesCount = getU2(fis, "Interfaces count");
			readInterfaces(fis, interfacesCount);
			int fieldsCount = getU2(fis, "Fields count");
			readFields(fis, fieldsCount);
			int methodsCount = getU2(fis, "Methods count");
			readMethods(fis, methodsCount);
			int attributesCount = getU2(fis, "Attributes count");
			readAttributes(fis, attributesCount);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.7
	private void readAttributes(FileInputStream fis, int attributesCount) {
		for (int i = 0; i < attributesCount; i++) {

		}
	}

	//https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.6
	private void readMethods(FileInputStream fis, int methodsCount) {
		for (int i = 0; i < methodsCount; i++) {

		}
	}

	//https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.5
	private void readFields(FileInputStream fis, int fieldsCount) throws IOException {
		for (int i = 0; i < fieldsCount; i++) {

		}
	}

	private void readInterfaces(FileInputStream fis, int interfacesCount) throws IOException {
		for (int i = 0; i < interfacesCount; i++) {
			getU2(fis, "", true);
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


	//https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.4
	private void readConstantPool(FileInputStream fis, int constantPoolCount) throws IOException {


		for (int i = 1; i < constantPoolCount; i++) {
			ConstantPoolRecord cpr = getCPRecord(i, fis);
			constants.add(cpr);
			if (cpr.cp.isTwoEntriesTakeUp()) {
				constants.add(null);
				i++;
			}
		}
		for (int i = 0; i < constants.size(); i++) {
			ConstantPoolRecord cpr = constants.get(i);
			cpr.print(constants);
			if (cpr.cp.isTwoEntriesTakeUp()) {
				i++;
			}
		}
	}

	private ConstantPoolRecord getCPRecord(int idx, FileInputStream fis) throws IOException {
		int offset = count;
		int tag = fis.read();
		count++;
		ConstantPool cp = ConstantPool.getConstant(tag);

		return switch (cp) {
			case CONSTANT_Class, CONSTANT_String, CONSTANT_MethodType, CONSTANT_Module, CONSTANT_Package -> {
				final short aShort = ByteBuffer.wrap(fis.readNBytes(Short.BYTES)).order(ByteOrder.BIG_ENDIAN).getShort();
				count += Short.BYTES;
				yield new ConstantPoolString(offset, idx, cp, aShort);
			}
			case CONSTANT_Fieldref, CONSTANT_Methodref, CONSTANT_InterfaceMethodref, CONSTANT_NameAndType -> {
				final short aShort = ByteBuffer.wrap(fis.readNBytes(Short.BYTES)).order(ByteOrder.BIG_ENDIAN).getShort();
				count += Short.BYTES;
				int bShort = ByteBuffer.wrap(fis.readNBytes(Short.BYTES)).order(ByteOrder.BIG_ENDIAN).getShort();
				count += Short.BYTES;
				yield new ConstantPoolMethodRef(offset, idx, cp, aShort, bShort);
			}
			case CONSTANT_Utf8 -> {
				final short length = ByteBuffer.wrap(fis.readNBytes(Short.BYTES)).order(ByteOrder.BIG_ENDIAN).getShort();
				count += Short.BYTES;
				count += length;
				yield new ConstantPoolUtf8Record(offset, idx, cp, new String(fis.readNBytes(length)));
			}
			case CONSTANT_MethodHandle -> {
				final int referenceKind = fis.read();
				count++;
				int bShort = ByteBuffer.wrap(fis.readNBytes(Short.BYTES)).order(ByteOrder.BIG_ENDIAN).getShort();
				count += Short.BYTES;
				yield new ConstantPoolMethodHandle(offset, idx, cp, referenceKind, bShort);
			}
			default -> {
				count += cp.length;
				fis.readNBytes(cp.length);
				yield new ConstantPoolDefault(offset, idx, cp);
			}
		};
	}

	private int getU2(FileInputStream fis, String title) throws IOException {
		return getU2(fis, title, false);
	}

	private int getU2(FileInputStream fis, String title, boolean addSymbolicName) throws IOException {
		String symbolic = "";
		int u2 = ByteBuffer.wrap(fis.readNBytes(Short.BYTES)).order(ByteOrder.BIG_ENDIAN).getShort();
		if (addSymbolicName) {
			if (u2 > 0) {
				symbolic = " " + constants.get(u2 - 1).getString(constants);
			} else {
				symbolic = "java/lang/Object";
			}
		}

		System.out.printf("%04X %s %02d%s\n", count, title, u2, symbolic);
		count += Short.BYTES;
		return u2;
	}

	private static final class Magic {
		static byte[] bytes = {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};
		static final int BYTES = bytes.length;

		private static void getMagic(FileInputStream fis) throws IOException {
			byte[] magic = fis.readNBytes(Magic.BYTES);
			if (!Arrays.equals(magic, Magic.bytes)) {
				StringBuilder sb = new StringBuilder();
				for (byte b : magic) {
					sb.append(String.format("%02X ", b));
				}
				throw new RuntimeException("Wrong magic tag : " + sb);
			}
		}
	}

	private static abstract class ConstantPoolRecord {
		int offset;
		int idx;
		ConstantPool cp;

		public ConstantPoolRecord(int offset, int idx, ConstantPool cp) {
			this.offset = offset;
			this.idx = idx;
			this.cp = cp;
		}

		void print(List<ConstantPoolRecord> constants) {
			System.out.printf("%04X %4d %19s ", offset, idx, cp.name().replaceFirst("^CONSTANT_", ""));
		}

		abstract String getString(List<ConstantPoolRecord> constants);
	}

	private static final class ConstantPoolString extends ConstantPoolRecord {
		int stringIndex;

		public ConstantPoolString(int offset, int idx, ConstantPool cp, int stringIndex) {
			super(offset, idx, cp);
			this.stringIndex = stringIndex;
		}

		@Override
		void print(List<ConstantPoolRecord> constants) {
			super.print(constants);
			System.out.println(getString(constants));
		}

		@Override
		String getString(List<ConstantPoolRecord> constants) {
			return "(" + stringIndex + ") " + constants.get(stringIndex - 1).getString(constants);
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
		void print(List<ConstantPoolRecord> constants) {
			super.print(constants);
			System.out.println(getString(constants));
		}

		@Override
		String getString(List<ConstantPoolRecord> constants) {
			return "(" + classIndex + ") " + constants.get(classIndex - 1).getString(constants)
					+ " (" + nameAndTypeIndex + ") " + constants.get(nameAndTypeIndex - 1).getString(constants);
		}
	}

	private static final class ConstantPoolUtf8Record extends ConstantPoolRecord {
		String UTF8;

		public ConstantPoolUtf8Record(int offset, int idx, ConstantPool cp, String UTF8) {
			super(offset, idx, cp);
			this.UTF8 = UTF8;
		}

		@Override
		void print(List<ConstantPoolRecord> constants) {
			super.print(constants);
			System.out.println(getString(constants));
		}

		@Override
		String getString(List<ConstantPoolRecord> constants) {
			return UTF8;
		}
	}

	private static final class ConstantPoolDefault extends ConstantPoolRecord {

		public ConstantPoolDefault(int offset, int idx, ConstantPool cp) {
			super(offset, idx, cp);
		}

		@Override
		void print(List<ConstantPoolRecord> constants) {
			super.print(constants);
			System.out.println(getString(constants));
		}

		@Override
		String getString(List<ConstantPoolRecord> constants) {
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
		void print(List<ConstantPoolRecord> constants) {
			super.print(constants);
			System.out.println(getString(constants));
		}

		@Override
		String getString(List<ConstantPoolRecord> constants) {
			return referenceKind + " (" + referenceIndex + ")" + constants.get(referenceIndex - 1).getString(constants);
		}
	}
}
