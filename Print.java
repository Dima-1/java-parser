import java.util.List;

public class Print {

	public static final boolean PRINT_CONSTANT_POOL = false;

	void u2(Parser.U2 u2, String title) {
		System.out.printf("%04X %s %02d" + ConsoleColors.YELLOW + "%s" + ConsoleColors.RESET + "\n",
				u2.offset, title, u2.value, u2.symbolic);
	}

	void u4(Parser.U4 u4) {
		System.out.printf("%04X %s %02d" + ConsoleColors.YELLOW + "%s" + ConsoleColors.RESET + "\n",
				u4.offset, "Attribute length", u4.value, u4.symbolic);
	}

	public void constantPool(List<Parser.ConstantPoolRecord> constants) {
		for (int i = 0; i < constants.size(); i++) {
			printRecord(constants, i);
			Parser.ConstantPoolRecord cpr = constants.get(i);
			if (cpr.cp.isTwoEntriesTakeUp()) {
				i++;
			}
		}
	}

	void printRecord(List<Parser.ConstantPoolRecord> constants, int idx) {
		if (!PRINT_CONSTANT_POOL) {
			return;
		}
		Parser.ConstantPoolRecord cpr = constants.get(idx);
		String cpName = cpr.cp.name().replaceFirst("^CONSTANT_", "");
		System.out.printf("%04X %4d %19s %s\n", cpr.offset, cpr.idx, cpName, cpr.getAdditional(constants));
	}

	void accessFlags(Parser.U2 u2, Type type) {
		switch (type) {
		/*  ACC_PUBLIC      0x0001 	Declared public; may be accessed from outside its package.
			ACC_FINAL       0x0010 	Declared final; no subclasses allowed.
			ACC_SUPER       0x0020 	Treat superclass methods specially when invoked by the invokespecial instruction.
			ACC_INTERFACE   0x0200 	Is an interface, not a class.
			ACC_ABSTRACT    0x0400 	Declared abstract; must not be instantiated.
			ACC_SYNTHETIC   0x1000 	Declared synthetic; not present in the source code.
			ACC_ANNOTATION  0x2000 	Declared as an annotation type.
			ACC_ENUM        0x4000 	Declared as an enum type.
			ACC_MODULE      0x8000 	Is a module, not a class or interface.*/
			case CLASS -> {
				String string = Integer.toBinaryString(u2.value);
				System.out.printf("%04X %s 0x%02X %s\n", u2.offset, "Access flags", u2.value, string);
			}
			case INTERFACE -> {
				String string = Integer.toBinaryString(u2.value);
				System.out.printf("%04X %s 0x%02X %s\n", u2.offset, "Access flags", u2.value, string);
			}
		/*  ACC_PUBLIC      0x0001 	Declared public; may be accessed from outside its package.
			ACC_PRIVATE     0x0002 	Declared private; accessible only within the defining class and other classes belonging to the same nest (§5.4.4).
			ACC_PROTECTED   0x0004 	Declared protected; may be accessed within subclasses.
			ACC_STATIC      0x0008 	Declared static.
			ACC_FINAL       0x0010 	Declared final; never directly assigned to after object construction (JLS §17.5).
			ACC_VOLATILE    0x0040 	Declared volatile; cannot be cached.
			ACC_TRANSIENT   0x0080 	Declared transient; not written or read by a persistent object manager.
			ACC_SYNTHETIC   0x1000 	Declared synthetic; not present in the source code.
			ACC_ENUM        0x4000 	Declared as an element of an enum.*/
			case FIELD -> {
				String string = Integer.toBinaryString(u2.value);
				System.out.printf("%04X %s 0x%02X %s\n", u2.offset, "Access flags", u2.value, string);
			}
		/*  ACC_PUBLIC 	    0x0001 	Declared public; may be accessed from outside its package.
			ACC_PRIVATE     0x0002 	Declared private; accessible only within the defining class and other classes belonging to the same nest (§5.4.4).
			ACC_PROTECTED   0x0004 	Declared protected; may be accessed within subclasses.
			ACC_STATIC      0x0008 	Declared static.
			ACC_FINAL       0x0010 	Declared final; must not be overridden (§5.4.5).
			ACC_SYNCHRONIZED 0x0020 Declared synchronized; invocation is wrapped by a monitor use.
			ACC_BRIDGE      0x0040 	A bridge method, generated by the compiler.
			ACC_VARARGS     0x0080 	Declared with variable number of arguments.
			ACC_NATIVE      0x0100 	Declared native; implemented in a language other than the Java programming language.
			ACC_ABSTRACT    0x0400 	Declared abstract; no implementation is provided.
			ACC_STRICT      0x0800 	Declared strictfp; floating-point mode is FP-strict.
			ACC_SYNTHETIC   0x1000 	Declared synthetic; not present in the source code. */
			case METHOD -> {
				String string = Integer.toBinaryString(u2.value);
				System.out.printf("%04X %s 0x%02X %s\n", u2.offset, "Access flags", u2.value, string);
			}
		}
	}
}