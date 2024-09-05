import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

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
			getU2(fis, "This class");
			getU2(fis, "Super class");
			int interfacesCount = getU2(fis, "Interfaces count");
			readInterfaces(fis, interfacesCount);
			int fieldsCount = getU2(fis, "Fields count");
			readFields(fis, interfacesCount);
			int methodsCount = getU2(fis, "Methods count");
			readMethods(fis, interfacesCount);
			int attributesCount = getU2(fis, "Attributes count");
			readAttributes(fis, interfacesCount);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void readAttributes(FileInputStream fis, int interfacesCount) {

	}

	private void readMethods(FileInputStream fis, int interfacesCount) {

	}

	private void readFields(FileInputStream fis, int interfacesCount) {

	}

	private void readInterfaces(FileInputStream fis, int interfacesCount) {

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

		boolean isStringIndex() {
			return this == CONSTANT_Class
					| this == CONSTANT_String | this == CONSTANT_MethodType
					| this == CONSTANT_Module | this == CONSTANT_Package;
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

	private void readConstantPool(FileInputStream fis, int constantPoolCount) throws IOException {
/*		cp_info {
			u1 tag;
			u1 info[];
		}*/

		for (int i = 2; i < constantPoolCount; i++) {
			int offset = count;
			int tag = fis.read();
			ConstantPool cp = ConstantPool.getConstant(tag);
			System.out.printf("%04X %4d %27s %4s \n", offset, i, cp.name(), getData(fis, tag));
			count++;
		}
	}

	public String getData(FileInputStream fis, int tag) throws IOException {
		ConstantPool cp = ConstantPool.getConstant(tag);
		int a = switch (cp) {
			case CONSTANT_Class, CONSTANT_String, CONSTANT_MethodType, CONSTANT_Module, CONSTANT_Package -> {
				count += Short.BYTES;
				yield ByteBuffer.wrap(fis.readNBytes(Short.BYTES)).order(ByteOrder.BIG_ENDIAN).getShort();
			}
			case CONSTANT_Utf8 -> {
				count += Short.BYTES;
				yield (int) ByteBuffer.wrap(fis.readNBytes(Short.BYTES)).order(ByteOrder.BIG_ENDIAN).getShort();
			}
			default -> {
				count += cp.length;
				fis.readNBytes(cp.length);
				yield cp.length;
			}
		};
		if (cp == ConstantPool.CONSTANT_Utf8) {
			count += a;
			return new String(fis.readNBytes(a));
		}
		if (ConstantPool.getConstant(tag).isStringIndex()) {
			return " ==> " + a;
		}
		return String.valueOf(a);
	}

	private int getU2(FileInputStream fis, String title) throws IOException {
		int u2 = ByteBuffer.wrap(fis.readNBytes(Short.BYTES)).order(ByteOrder.BIG_ENDIAN).getShort();
		System.out.printf("%04X %s %02d\n", count, title, u2);
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
}
