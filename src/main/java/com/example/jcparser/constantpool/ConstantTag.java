package com.example.jcparser.constantpool;

import java.util.Arrays;

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

	public static ConstantTag getConstant(int tag) {
		return Arrays.stream(values()).filter(v -> v.tag == tag).findFirst().orElseThrow(() ->
				new IllegalArgumentException("Unknown constant tag: " + tag));
	}
}
