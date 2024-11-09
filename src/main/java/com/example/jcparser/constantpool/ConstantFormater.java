package com.example.jcparser.constantpool;

import com.example.jcparser.Print;

import java.util.List;

public class ConstantFormater {
	private final Print print;
	private String formatedString;
	private boolean printGeneral = true;
	private List<ConstantPoolEntry> constants;

	public ConstantFormater(Print print) {
		this.print = print;
	}

	public void setConstantPool(List<ConstantPoolEntry> constants) {
		this.constants = constants;
	}

	public List<ConstantPoolEntry> getConstantPool() {
		return constants;
	}

	public String getFormatedString() {
		return formatedString;
	}

	public void setPrintGeneral(boolean printGeneral) {
		this.printGeneral = printGeneral;
	}

	void format(ConstantPoolEntry cpe) {
		if (printGeneral) {
			String cpName = cpe.getConstantTag().name().replaceFirst("^CONSTANT_", "");
			formatedString = String.format(print.getOffsetFormat() + " %4X %19s", cpe.getOffset(), cpe.getIdx(), cpName);
			printGeneral = false;
		}
	}

	void format(ConstantPoolUtf8 cpe) {
		formatedString += " " + String.format(Print.YELLOW_STRING, cpe.getUtf8());
	}

	void format(ConstantPoolInteger cpe) {
		formatedString += " " + cpe.getValue();
	}

	void format(ConstantPoolFloat cpe) {
		formatedString += " " + cpe.getValue();
	}

	void format(ConstantPoolLong cpe) {
		formatedString += " " + cpe.getValue();
	}

	void format(ConstantPoolDouble cpe) {
		formatedString += " " + cpe.getValue();
	}

	void format(ConstantPoolString cpe) {
		format(cpe.getStringIndex(), constants);
	}

	private void format(int idx, List<ConstantPoolEntry> cpe) {
		if (print.getOptions().needRefs()) {
			formatedString += String.format(Print.HEX_2, idx);
		}
		cpe.get(idx).format(this);
	}

	void format(ConstantPoolMethodRef cpe) {
		format(cpe.getClassIndex(), constants);
		format(cpe.getNameAndTypeIndex(), constants);
	}

	void format(ConstantPoolNameAndType cpe) {
		format(cpe.getNameIndex(), constants);
		format(cpe.getDescriptorIndex(), constants);
	}

	void format(ConstantPoolDynamic cpe) {
		formatedString += String.format(Print.HEX_2, cpe.getBootstrapMethodAttrIndex());
		format(cpe.getNameAndTypeIndex(), constants);
	}

	void format(ConstantPoolMethodHandle cpe) {
		formatedString += " " + ConstantPoolMethodHandle.MHRef.values()[cpe.getReferenceKind()].name()
				.replaceFirst("REF_", "");
		format(cpe.getReferenceIndex(), constants);
	}


	public String formatNewOnlyString(ConstantPoolEntry cpe) {
		printGeneral = false;
		formatedString = "";
		cpe.format(this);
		return formatedString.stripLeading();
	}

	public interface Formatter<T> {
		void format(T printer);
	}
}
