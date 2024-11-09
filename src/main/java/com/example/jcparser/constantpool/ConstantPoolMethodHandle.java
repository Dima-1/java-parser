package com.example.jcparser.constantpool;

public class ConstantPoolMethodHandle extends ConstantPoolEntry {

	public enum MHRef {
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

	public ConstantPoolMethodHandle(int offset, int idx, ConstantTag constantTag,
	                                int referenceKind, int referenceIndex) {
		super(offset, idx, constantTag);
		this.referenceKind = referenceKind;
		this.referenceIndex = referenceIndex;
	}

	public int getReferenceKind() {
		return referenceKind;
	}

	public int getReferenceIndex() {
		return referenceIndex;
	}

	@Override
	public void format(ConstantFormater formater) {
		super.format(formater);
		formater.format(this);
	}
}
