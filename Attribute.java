import java.util.List;

public class Attribute {
	private final int offset;
	private final Parser.U2 nameIndex;
	private final String name;
	private final Parser.U4 length;
	List<Parser.ConstantPoolRecord> constants;

	public Attribute(int offset, List<Parser.ConstantPoolRecord> constants, Parser.U2 nameIndex, Parser.U4 length) {
		this.offset = offset;
		this.nameIndex = nameIndex;
		name = constants.get(nameIndex.getValue() - 1).getAdditional(constants);
		this.length = length;
	}

	public int getOffset() {
		return offset;
	}

	public Parser.U2 getNameIndex() {
		return nameIndex;
	}

	public String getName() {
		return name;
	}

	public Parser.U4 getLength() {
		return length;
	}

	public Parser.U2 getAdditional() {
		return nameIndex;
	}

	public static class SourceFileAttribute extends Attribute {
		Parser.U2 sourceFileIndex;

		public SourceFileAttribute(int offset, List<Parser.ConstantPoolRecord> constants, Parser.U2 nameIndex,
		                           Parser.U4 length, Parser.U2 sourceFileIndex) {
			super(offset, constants, nameIndex, length);
			this.sourceFileIndex = sourceFileIndex;
		}

		@Override
		public Parser.U2 getAdditional() {
			return sourceFileIndex;
		}
	}
}
