package com.example.jcparser;

public final class Options {
	private boolean constants = true;
	private boolean refs = true;

	public Options() {
	}

	public boolean needConstants() {
		return constants;
	}

	public void setConstants(boolean constants) {
		this.constants = constants;
	}

	public boolean needRefs() {
		return refs;
	}

	public void setRefs(boolean refs) {
		this.refs = refs;
	}
}
