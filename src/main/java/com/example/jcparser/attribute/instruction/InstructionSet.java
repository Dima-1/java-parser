package com.example.jcparser.attribute.instruction;

import java.util.Arrays;

public enum InstructionSet {
	AALOAD("aaload", 0x32),                     // 		arrayref, index → value 	load onto the stack a reference from an array
	AASTORE("aastore", 0x53),                   // 		arrayref, index, value → 	store a reference in an array
	ACONST_NULL("aconst_null", 0x01),           // 		→ null 	push a null reference onto the stack
	ALOAD("aload", 0x19, 1),                    // 	1: index 	→ objectref 	load a reference onto the stack from a local variable #index
	ALOAD_0("aload_0", 0x2A),                   // 		→ objectref 	load a reference onto the stack from local variable 0
	ALOAD_1("aload_1", 0x2B),                   // 		→ objectref 	load a reference onto the stack from local variable 1
	ALOAD_2("aload_2", 0x2C),                   // 		→ objectref 	load a reference onto the stack from local variable 2
	ALOAD_3("aload_3", 0x2D),                   // 		→ objectref 	load a reference onto the stack from local variable 3
	ANEWARRAY("anewarray", 0xBD, 2),            // 	2: indexbyte1, indexbyte2 	count → arrayref 	create a new array of references of length count and component type identified by the class reference index (indexbyte1 << 8 | indexbyte2) in the constant pool
	ARETURN("areturn", 0xB0),                   // 		objectref → [empty] 	return a reference from a method
	ARRAYLENGTH("arraylength", 0xBE),           // 		arrayref → length 	get the length of an array
	ASTORE("astore", 0x3A, 1),                  // 	1: index 	objectref → 	store a reference into a local variable #index
	ASTORE_0("astore_0", 0x4B),                 // 		objectref → 	store a reference into local variable 0
	ASTORE_1("astore_1", 0x4C),                 // 		objectref → 	store a reference into local variable 1
	ASTORE_2("astore_2", 0x4D),                 // 		objectref → 	store a reference into local variable 2
	ASTORE_3("astore_3", 0x4E),                 // 		objectref → 	store a reference into local variable 3
	ATHROW("athrow", 0xBF),                     // 		objectref → [empty], objectref 	throws an error or exception (notice that the rest of the stack is cleared, leaving only a reference to the Throwable)
	BALOAD("baload", 0x33),                     // 		arrayref, index → value 	load a byte or Boolean value from an array
	BASTORE("bastore", 0x54),                   // 		arrayref, index, value → 	store a byte or Boolean value into an array
	BIPUSH("bipush", 0x10, 1),                  // 	1: byte 	→ value 	push a byte onto the stack as an integer value
	//	BREAKPOINT("breakpoint", 0xCA),             // 			reserved for breakpoints in Java debuggers; should not appear in any class file
	CALOAD("caload", 0x34),                     // 		arrayref, index → value 	load a char from an array
	CASTORE("castore", 0x55),                   // 		arrayref, index, value → 	store a char into an array
	CHECKCAST("checkcast", 0xC0, 2),            // 	2: indexbyte1, indexbyte2 	objectref → objectref 	checks whether an objectref is of a certain type, the class reference of which is in the constant pool at index (indexbyte1 << 8 | indexbyte2)
	D2F("d2f", 0x90),                           // 		value → result 	convert a double to a float
	D2I("d2i", 0x8E),                           // 		value → result 	convert a double to an int
	D2L("d2l", 0x8F),                           // 		value → result 	convert a double to a long
	DADD("dadd", 0x63),                         // 		value1, value2 → result 	add two doubles
	DALOAD("daload", 0x31),                     // 		arrayref, index → value 	load a double from an array
	DASTORE("dastore", 0x52),                   // 		arrayref, index, value → 	store a double into an array
	DCMPG("dcmpg", 0x98),                       // 		value1, value2 → result 	compare two doubles, 1 on NaN
	DCMPL("dcmpl", 0x97),                       // 		value1, value2 → result 	compare two doubles, -1 on NaN
	DCONST_0("dconst_0", 0x0E),                 // 		→ 0.0 	push the constant 0.0 (a double) onto the stack
	DCONST_1("dconst_1", 0x0F),                 // 		→ 1.0 	push the constant 1.0 (a double) onto the stack
	DDIV("ddiv", 0x6F),                         // 		value1, value2 → result 	divide two doubles
	DLOAD("dload", 0x18, 1),                    // 	1: index 	→ value 	load a double value from a local variable #index
	DLOAD_0("dload_0", 0x26),                   // 		→ value 	load a double from local variable 0
	DLOAD_1("dload_1", 0x27),                   // 		→ value 	load a double from local variable 1
	DLOAD_2("dload_2", 0x28),                   // 		→ value 	load a double from local variable 2
	DLOAD_3("dload_3", 0x29),                   // 		→ value 	load a double from local variable 3
	DMUL("dmul", 0x6B),                         // 		value1, value2 → result 	multiply two doubles
	DNEG("dneg", 0x77),                         // 		value → result 	negate a double
	DREM("drem", 0x73),                         // 		value1, value2 → result 	get the remainder from a division between two doubles
	DRETURN("dreturn", 0xAF),                   // 		value → [empty] 	return a double from a method
	DSTORE("dstore", 0x39, 1),                  // 	1: index 	value → 	store a double value into a local variable #index
	DSTORE_0("dstore_0", 0x47),                 // 		value → 	store a double into local variable 0
	DSTORE_1("dstore_1", 0x48),                 // 		value → 	store a double into local variable 1
	DSTORE_2("dstore_2", 0x49),                 // 		value → 	store a double into local variable 2
	DSTORE_3("dstore_3", 0x4A),                 // 		value → 	store a double into local variable 3
	DSUB("dsub", 0x67),                         // 		value1, value2 → result 	subtract a double from another
	DUP("dup", 0x59),                           // 		value → value, value 	duplicate the value on top of the stack
	DUP_X1("dup_x1", 0x5A),                     // 		value2, value1 → value1, value2, value1 	insert a copy of the top value into the stack two values from the top. value1 and value2 must not be of the type double or long.
	DUP_X2("dup_x2", 0x5B),                     // 		value3, value2, value1 → value1, value3, value2, value1 	insert a copy of the top value into the stack two (if value2 is double or long it takes up the entry of value3, too) or three values (if value2 is neither double nor long) from the top
	DUP2("dup2", 0x5C),                         // 		{value2, value1} → {value2, value1}, {value2, value1} 	duplicate top two stack words (two values, if value1 is not double nor long; a single value, if value1 is double or long)
	DUP2_X1("dup2_x1", 0x5D),                   // 		value3, {value2, value1} → {value2, value1}, value3, {value2, value1} 	duplicate two words and insert beneath third word (see explanation above)
	DUP2_X2("dup2_x2", 0x5E),                   // 		{value4, value3}, {value2, value1} → {value2, value1}, {value4, value3}, {value2, value1} 	duplicate two words and insert beneath fourth word
	F2D("f2d", 0x8D),                           // 		value → result 	convert a float to a double
	F2I("f2i", 0x8B),                           // 		value → result 	convert a float to an int
	F2L("f2l", 0x8C),                           // 		value → result 	convert a float to a long
	FADD("fadd", 0x62),                         // 		value1, value2 → result 	add two floats
	FALOAD("faload", 0x30),                     // 		arrayref, index → value 	load a float from an array
	FASTORE("fastore", 0x51),                   // 		arrayref, index, value → 	store a float in an array
	FCMPG("fcmpg", 0x96),                       // 		value1, value2 → result 	compare two floats, 1 on NaN
	FCMPL("fcmpl", 0x95),                       // 		value1, value2 → result 	compare two floats, -1 on NaN
	FCONST_0("fconst_0", 0x0B),                 // 		→ 0.0f 	push 0.0f on the stack
	FCONST_1("fconst_1", 0x0C),                 // 		→ 1.0f 	push 1.0f on the stack
	FCONST_2("fconst_2", 0x0D),                 // 		→ 2.0f 	push 2.0f on the stack
	FDIV("fdiv", 0x6E),                         // 		value1, value2 → result 	divide two floats
	FLOAD("fload", 0x17, 1),                    // 	1: index 	→ value 	load a float value from a local variable #index
	FLOAD_0("fload_0", 0x22),                   // 		→ value 	load a float value from local variable 0
	FLOAD_1("fload_1", 0x23),                   // 		→ value 	load a float value from local variable 1
	FLOAD_2("fload_2", 0x24),                   // 		→ value 	load a float value from local variable 2
	FLOAD_3("fload_3", 0x25),                   // 		→ value 	load a float value from local variable 3
	FMUL("fmul", 0x6A),                         // 		value1, value2 → result 	multiply two floats
	FNEG("fneg", 0x76),                         // 		value → result 	negate a float
	FREM("frem", 0x72),                         // 		value1, value2 → result 	get the remainder from a division between two floats
	FRETURN("freturn", 0xAE),                   // 		value → [empty] 	return a float
	FSTORE("fstore", 0x38, 1),                  // 	1: index 	value → 	store a float value into a local variable #index
	FSTORE_0("fstore_0", 0x43),                 // 		value → 	store a float value into local variable 0
	FSTORE_1("fstore_1", 0x44),                 // 		value → 	store a float value into local variable 1
	FSTORE_2("fstore_2", 0x45),                 // 		value → 	store a float value into local variable 2
	FSTORE_3("fstore_3", 0x46),                 // 		value → 	store a float value into local variable 3
	FSUB("fsub", 0x66),                         // 		value1, value2 → result 	subtract two floats
	GETFIELD("getfield", 0xB4, 2),              // 	2: indexbyte1, indexbyte2 	objectref → value 	get a field value of an object objectref, where the field is identified by field reference in the constant pool index (indexbyte1 << 8 | indexbyte2)
	GETSTATIC("getstatic", 0xB2, 2),            // 	2: indexbyte1, indexbyte2 	→ value 	get a static field value of a class, where the field is identified by field reference in the constant pool index (indexbyte1 << 8 | indexbyte2)
	GOTO("goto", 0xA7, 2),                      // 	2: branchbyte1, branchbyte2 	[no change] 	goes to another instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
	GOTO_W("goto_w", 0xC8, 4),                  // 	4: branchbyte1, branchbyte2, branchbyte3, branchbyte4 	[no change] 	goes to another instruction at branchoffset (signed int constructed from unsigned bytes branchbyte1 << 24 | branchbyte2 << 16 | branchbyte3 << 8 | branchbyte4)
	I2B("i2b", 0x91),                           // 		value → result 	convert an int into a byte
	I2C("i2c", 0x92),                           // 		value → result 	convert an int into a character
	I2D("i2d", 0x87),                           // 		value → result 	convert an int into a double
	I2F("i2f", 0x86),                           // 		value → result 	convert an int into a float
	I2L("i2l", 0x85),                           // 		value → result 	convert an int into a long
	I2S("i2s", 0x93),                           // 		value → result 	convert an int into a short
	IADD("iadd", 0x60),                         // 		value1, value2 → result 	add two ints
	IALOAD("iaload", 0x2E),                     // 		arrayref, index → value 	load an int from an array
	IAND("iand", 0x7E),                         // 		value1, value2 → result 	perform a bitwise AND on two integers
	IASTORE("iastore", 0x4F),                   // 		arrayref, index, value → 	store an int into an array
	ICONST_M1("iconst_m1", 0x02),               // 		→ -1 	load the int value −1 onto the stack
	ICONST_0("iconst_0", 0x03),                 // 		→ 0 	load the int value 0 onto the stack
	ICONST_1("iconst_1", 0x04),                 // 		→ 1 	load the int value 1 onto the stack
	ICONST_2("iconst_2", 0x05),                 // 		→ 2 	load the int value 2 onto the stack
	ICONST_3("iconst_3", 0x06),                 // 		→ 3 	load the int value 3 onto the stack
	ICONST_4("iconst_4", 0x07),                 // 		→ 4 	load the int value 4 onto the stack
	ICONST_5("iconst_5", 0x08),                 // 		→ 5 	load the int value 5 onto the stack
	IDIV("idiv", 0x6C),                         // 		value1, value2 → result 	divide two integers
	IF_ACMPEQ("if_acmpeq", 0xA5, 2),            // 	2: branchbyte1, branchbyte2 	value1, value2 → 	if references are equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
	IF_ACMPNE("if_acmpne", 0xA6, 2),            // 	2: branchbyte1, branchbyte2 	value1, value2 → 	if references are not equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
	IF_ICMPEQ("if_icmpeq", 0x9F, 2),            // 	2: branchbyte1, branchbyte2 	value1, value2 → 	if ints are equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
	IF_ICMPGE("if_icmpge", 0xA2, 2),            // 	2: branchbyte1, branchbyte2 	value1, value2 → 	if value1 is greater than or equal to value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
	IF_ICMPGT("if_icmpgt", 0xA3, 2),            // 	2: branchbyte1, branchbyte2 	value1, value2 → 	if value1 is greater than value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
	IF_ICMPLE("if_icmple", 0xA4, 2),            // 	2: branchbyte1, branchbyte2 	value1, value2 → 	if value1 is less than or equal to value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
	IF_ICMPLT("if_icmplt", 0xA1, 2),            // 	2: branchbyte1, branchbyte2 	value1, value2 → 	if value1 is less than value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
	IF_ICMPNE("if_icmpne", 0xA0, 2),            // 	2: branchbyte1, branchbyte2 	value1, value2 → 	if ints are not equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
	IFEQ("ifeq", 0x99, 2),                      // 	2: branchbyte1, branchbyte2 	value → 	if value is 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
	IFGE("ifge", 0x9C, 2),                      // 	2: branchbyte1, branchbyte2 	value → 	if value is greater than or equal to 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
	IFGT("ifgt", 0x9D, 2),                      // 	2: branchbyte1, branchbyte2 	value → 	if value is greater than 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
	IFLE("ifle", 0x9E, 2),                      // 	2: branchbyte1, branchbyte2 	value → 	if value is less than or equal to 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
	IFLT("iflt", 0x9B, 2),                      // 	2: branchbyte1, branchbyte2 	value → 	if value is less than 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
	IFNE("ifne", 0x9A, 2),                      // 	2: branchbyte1, branchbyte2 	value → 	if value is not 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
	IFNONNULL("ifnonnull", 0xC7, 2),            // 	2: branchbyte1, branchbyte2 	value → 	if value is not null, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
	IFNULL("ifnull", 0xC6, 2),                  // 	2: branchbyte1, branchbyte2 	value → 	if value is null, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
	IINC("iinc", 0x84, 2),                      // 	2: index, const 	[No change] 	increment local variable #index by signed byte const
	ILOAD("iload", 0x15, 1),                    // 	1: index 	→ value 	load an int value from a local variable #index
	ILOAD_0("iload_0", 0x1A),                   // 		→ value 	load an int value from local variable 0
	ILOAD_1("iload_1", 0x1B),                   // 		→ value 	load an int value from local variable 1
	ILOAD_2("iload_2", 0x1C),                   // 		→ value 	load an int value from local variable 2
	ILOAD_3("iload_3", 0x1D),                   // 		→ value 	load an int value from local variable 3
	//	IMPDEP1("impdep1", 0xFE),                   // 			reserved for implementation-dependent operations within debuggers; should not appear in any class file
//	IMPDEP2("impdep2", 0xFF),                   // 			reserved for implementation-dependent operations within debuggers; should not appear in any class file
	IMUL("imul", 0x68),                         // 		value1, value2 → result 	multiply two integers
	INEG("ineg", 0x74),                         // 		value → result 	negate int
	INSTANCEOF("instanceof", 0xC1, 2),          // 	2: indexbyte1, indexbyte2 	objectref → result 	determines if an object objectref is of a given type, identified by class reference index in constant pool (indexbyte1 << 8 | indexbyte2)
	INVOKEDYNAMIC("invokedynamic", 0xBA, 4),    // 	4: indexbyte1, indexbyte2, 0, 0 	[arg1, arg2, ...] → result 	invokes a dynamic method and puts the result on the stack (might be void); the method is identified by method reference index in constant pool (indexbyte1 << 8 | indexbyte2)
	INVOKEINTERFACE("invokeinterface", 0xB9, 4),// 	4: indexbyte1, indexbyte2, count, 0 	objectref, [arg1, arg2, ...] → result 	invokes an interface method on object objectref and puts the result on the stack (might be void); the interface method is identified by method reference index in constant pool (indexbyte1 << 8 | indexbyte2)
	INVOKESPECIAL("invokespecial", 0xB7, 2),    // 	2: indexbyte1, indexbyte2 	objectref, [arg1, arg2, ...] → result 	invoke instance method on object objectref and puts the result on the stack (might be void); the method is identified by method reference index in constant pool (indexbyte1 << 8 | indexbyte2)
	INVOKESTATIC("invokestatic", 0xB8, 2),      // 	2: indexbyte1, indexbyte2 	[arg1, arg2, ...] → result 	invoke a static method and puts the result on the stack (might be void); the method is identified by method reference index in constant pool (indexbyte1 << 8 | indexbyte2)
	INVOKEVIRTUAL("invokevirtual", 0xB6, 2),    // 	2: indexbyte1, indexbyte2 	objectref, [arg1, arg2, ...] → result 	invoke virtual method on object objectref and puts the result on the stack (might be void); the method is identified by method reference index in constant pool (indexbyte1 << 8 | indexbyte2)
	IOR("ior", 0x80),                           // 		value1, value2 → result 	bitwise int OR
	IREM("irem", 0x70),                         // 		value1, value2 → result 	logical int remainder
	IRETURN("ireturn", 0xAC),                   // 		value → [empty] 	return an integer from a method
	ISHL("ishl", 0x78),                         // 		value1, value2 → result 	int shift left
	ISHR("ishr", 0x7A),                         // 		value1, value2 → result 	int arithmetic shift right
	ISTORE("istore", 0x36, 1),                  // 	1: index 	value → 	store int value into variable #index
	ISTORE_0("istore_0", 0x3B),                 // 		value → 	store int value into variable 0
	ISTORE_1("istore_1", 0x3C),                 // 		value → 	store int value into variable 1
	ISTORE_2("istore_2", 0x3D),                 // 		value → 	store int value into variable 2
	ISTORE_3("istore_3", 0x3E),                 // 		value → 	store int value into variable 3
	ISUB("isub", 0x64),                         // 		value1, value2 → result 	int subtract
	IUSHR("iushr", 0x7C),                       // 		value1, value2 → result 	int logical shift right
	IXOR("ixor", 0x82),                         // 		value1, value2 → result 	int xor
	JSR("jsr", 0xA8, 2),                        // 	2: branchbyte1, branchbyte2 	→ address 	jump to subroutine at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2) and place the return address on the stack
	JSR_W("jsr_w", 0xC9, 4),                    // 	4: branchbyte1, branchbyte2, branchbyte3, branchbyte4 	→ address 	jump to subroutine at branchoffset (signed int constructed from unsigned bytes branchbyte1 << 24 | branchbyte2 << 16 | branchbyte3 << 8 | branchbyte4) and place the return address on the stack
	L2D("l2d", 0x8A),                           // 		value → result 	convert a long to a double
	L2F("l2f", 0x89),                           // 		value → result 	convert a long to a float
	L2I("l2i", 0x88),                           // 		value → result 	convert a long to a int
	LADD("ladd", 0x61),                         // 		value1, value2 → result 	add two longs
	LALOAD("laload", 0x2F),                     // 		arrayref, index → value 	load a long from an array
	LAND("land", 0x7F),                         // 		value1, value2 → result 	bitwise AND of two longs
	LASTORE("lastore", 0x50),                   // 		arrayref, index, value → 	store a long to an array
	LCMP("lcmp", 0x94),                         // 		value1, value2 → result 	push 0 if the two longs are the same, 1 if value1 is greater than value2, -1 otherwise
	LCONST_0("lconst_0", 0x09),                 // 		→ 0L 	push 0L (the number zero with type long) onto the stack
	LCONST_1("lconst_1", 0x0A),                 // 		→ 1L 	push 1L (the number one with type long) onto the stack
	LDC("ldc", 0x12, 1),                        // 	1: index 	→ value 	push a constant #index from a constant pool (String, int, float, Class, java.lang.invoke.MethodType, java.lang.invoke.MethodHandle, or a dynamically-computed constant) onto the stack
	LDC_W("ldc_w", 0x13, 2),                    // 	2: indexbyte1, indexbyte2 	→ value 	push a constant #index from a constant pool (String, int, float, Class, java.lang.invoke.MethodType, java.lang.invoke.MethodHandle, or a dynamically-computed constant) onto the stack (wide index is constructed as indexbyte1 << 8 | indexbyte2)
	LDC2_W("ldc2_w", 0x14, 2),                  // 	2: indexbyte1, indexbyte2 	→ value 	push a constant #index from a constant pool (double, long, or a dynamically-computed constant) onto the stack (wide index is constructed as indexbyte1 << 8 | indexbyte2)
	LDIV("ldiv", 0x6D),                         // 		value1, value2 → result 	divide two longs
	LLOAD("lload", 0x16, 1),                    // 	1: index 	→ value 	load a long value from a local variable #index
	LLOAD_0("lload_0", 0x1E),                   // 		→ value 	load a long value from a local variable 0
	LLOAD_1("lload_1", 0x1F),                   // 		→ value 	load a long value from a local variable 1
	LLOAD_2("lload_2", 0x20),                   // 		→ value 	load a long value from a local variable 2
	LLOAD_3("lload_3", 0x21),                   // 		→ value 	load a long value from a local variable 3
	LMUL("lmul", 0x69),                         // 		value1, value2 → result 	multiply two longs
	LNEG("lneg", 0x75),                         // 		value → result 	negate a long
	LOOKUPSWITCH("lookupswitch", 0xAB, 8),      // 	8+: <0–3 bytes padding>, defaultbyte1, defaultbyte2, defaultbyte3, defaultbyte4, npairs1, npairs2, npairs3, npairs4, match-offset pairs... 	key → 	a target address is looked up from a table using a key and execution continues from the instruction at that address
	LOR("lor", 0x81),                           // 		value1, value2 → result 	bitwise OR of two longs
	LREM("lrem", 0x71),                         // 		value1, value2 → result 	remainder of division of two longs
	LRETURN("lreturn", 0xAD),                   // 		value → [empty] 	return a long value
	LSHL("lshl", 0x79),                         // 		value1, value2 → result 	bitwise shift left of a long value1 by int value2 positions
	LSHR("lshr", 0x7B),                         // 		value1, value2 → result 	bitwise shift right of a long value1 by int value2 positions
	LSTORE("lstore", 0x37, 1),                  // 	1: index 	value → 	store a long value in a local variable #index
	LSTORE_0("lstore_0", 0x3F),                 // 		value → 	store a long value in a local variable 0
	LSTORE_1("lstore_1", 0x40),                 // 		value → 	store a long value in a local variable 1
	LSTORE_2("lstore_2", 0x41),                 // 		value → 	store a long value in a local variable 2
	LSTORE_3("lstore_3", 0x42),                 // 		value → 	store a long value in a local variable 3
	LSUB("lsub", 0x65),                         // 		value1, value2 → result 	subtract two longs
	LUSHR("lushr", 0x7D),                       // 		value1, value2 → result 	bitwise shift right of a long value1 by int value2 positions, unsigned
	LXOR("lxor", 0x83),                         // 		value1, value2 → result 	bitwise XOR of two longs
	MONITORENTER("monitorenter", 0xC2),         // 		objectref → 	enter monitor for object ("grab the lock" – start of synchronized() section)
	MONITOREXIT("monitorexit", 0xC3),           // 		objectref → 	exit monitor for object ("release the lock" – end of synchronized() section)
	MULTIANEWARRAY("multianewarray", 0xC5, 3),  // 	3: indexbyte1, indexbyte2, dimensions 	count1, [count2,...] → arrayref 	create a new array of dimensions dimensions of type identified by class reference in constant pool index (indexbyte1 << 8 | indexbyte2); the sizes of each dimension is identified by count1, [count2, etc.]
	NEW("new", 0xBB, 2),                        // 	2: indexbyte1, indexbyte2 	→ objectref 	create new object of type identified by class reference in constant pool index (indexbyte1 << 8 | indexbyte2)
	NEWARRAY("newarray", 0xBC, 1),              // 	1: atype 	count → arrayref 	create new array with count elements of primitive type identified by atype
	NOP("nop", 0x00),                           // 		[No change] 	perform no operation
	POP("pop", 0x57),                           // 		value → 	discard the top value on the stack
	POP2("pop2", 0x58),                         // 		{value2, value1} → 	discard the top two values on the stack (or one value, if it is a double or long)
	PUTFIELD("putfield", 0xB5, 2),              // 	2: indexbyte1, indexbyte2 	objectref, value → 	set field to value in an object objectref, where the field is identified by a field reference index in constant pool (indexbyte1 << 8 | indexbyte2)
	PUTSTATIC("putstatic", 0xB3, 2),            // 	2: indexbyte1, indexbyte2 	value → 	set static field to value in a class, where the field is identified by a field reference index in constant pool (indexbyte1 << 8 | indexbyte2)
	RET("ret", 0xA9, 1),                        // 	1: index 	[No change] 	continue execution from address taken from a local variable #index (the asymmetry with jsr is intentional)
	RETURN("return", 0xB1),                     // 		→ [empty] 	return void from method
	SALOAD("saload", 0x35),                     // 		arrayref, index → value 	load short from array
	SASTORE("sastore", 0x56),                   // 		arrayref, index, value → 	store short to array
	SIPUSH("sipush", 0x11, 2),                  // 	2: byte1, byte2 	→ value 	push a short onto the stack as an integer value
	SWAP("swap", 0x5F),                         // 		value2, value1 → value1, value2 	swaps two top words on the stack (note that value1 and value2 must not be double or long)
	TABLESWITCH("tableswitch", 0xAA, 16),       // 	16+: [0–3 bytes padding], defaultbyte1, defaultbyte2, defaultbyte3, defaultbyte4, lowbyte1, lowbyte2, lowbyte3, lowbyte4, highbyte1, highbyte2, highbyte3, highbyte4, jump offsets... 	index → 	continue execution from an address in the table at offset index
	WIDE("wide", 0xC4, 3);                      // 	3/5: opcode, indexbyte1, indexbyte2 or iinc, indexbyte1, indexbyte2, countbyte1, countbyte2 	[same as for corresponding instructions] 	execute opcode, where opcode is either iload, fload, aload, lload, dload, istore, fstore, astore, lstore, dstore, or ret, but assume the index is 16 bit; or execute iinc, where the index is 16 bits and the constant to increment by is a signed 16 bit short

	private final String mnemonic;
	private final int opcode;
	private int argumentsSize = 0;

	InstructionSet(String mnemonic, int opcode) {
		this.mnemonic = mnemonic;
		this.opcode = opcode;
	}

	InstructionSet(String mnemonic, int opcode, int argumentsSize) {
		this(mnemonic, opcode);
		this.argumentsSize = argumentsSize;
	}

	public String getMnemonic() {
		return mnemonic;
	}

	public int getOpcode() {
		return opcode;
	}

	public static InstructionSet getInstruction(int opcode) {
		return Arrays.stream(values()).filter(v -> v.opcode == opcode).findFirst().orElseThrow(() ->
				new IllegalArgumentException("Unknown opcode: " + Integer.toHexString(opcode).toUpperCase()));
	}

	public static int getOperandsSize(int code) {
		return getInstruction(code).argumentsSize;
	}

	public static Type getOperandsType(int code) {
		Type type;
		type = switch (getInstruction(code)) {
			case ANEWARRAY, CHECKCAST, GETFIELD, GETSTATIC, INSTANCEOF, INVOKEDYNAMIC, INVOKESPECIAL, INVOKESTATIC,
			     INVOKEVIRTUAL, LDC_W, LDC2_W, NEW, PUTFIELD, PUTSTATIC -> Type.CP_IDX;
			case INVOKEINTERFACE, MULTIANEWARRAY -> Type.CP_IDX_COUNT;
			case LDC -> Type.CP_IDX_BYTE;
			case ALOAD_0, ALOAD_1, ALOAD_2, ALOAD_3, ASTORE_0, ASTORE_1, ASTORE_2, ASTORE_3,
			     DLOAD_0, DLOAD_1, DLOAD_2, DLOAD_3, DSTORE_0, DSTORE_1, DSTORE_2, DSTORE_3,
			     FLOAD_0, FLOAD_1, FLOAD_2, FLOAD_3, FSTORE_0, FSTORE_1, FSTORE_2, FSTORE_3,
			     ILOAD_0, ILOAD_1, ILOAD_2, ILOAD_3, ISTORE_0, ISTORE_1, ISTORE_2, ISTORE_3,
			     LLOAD_0, LLOAD_1, LLOAD_2, LLOAD_3, LSTORE_0, LSTORE_1, LSTORE_2, LSTORE_3,
			     ALOAD, ASTORE, DLOAD, DSTORE, FLOAD, FSTORE, ILOAD, ISTORE, LLOAD, LSTORE, RET -> Type.LOCAL_VAR_IDX;
			case BIPUSH, NEWARRAY -> Type.BYTE;
			default -> null;
		};
		return type;
	}

	public enum Type {
		CP_IDX,
		CP_IDX_BYTE,
		CP_IDX_COUNT,
		LOCAL_VAR_IDX,
		BYTE
	}
}
