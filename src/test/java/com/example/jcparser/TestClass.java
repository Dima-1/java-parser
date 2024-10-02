package com.example.jcparser;

public class TestClass {
	public static void main(String[] args) {
		TestClass testClassVar = new TestClass();
		testClassVar.testMethod();
	}

	private void testMethod() {
		class TestLocalClass {

			private final String str;

			public TestLocalClass(String str) {
				this.str = str;
			}

			public String getStr() {
				return str;
			}
		}
		TestInnerClass testInnerClass = new TestInnerClass(10);
		System.out.println(testInnerClass.getFieldInt());
		TestLocalClass testLocalClass = new TestLocalClass("local string");
		System.out.println(testLocalClass.getStr());
	}

	public class TestInnerClass {
		private final int fieldInt;

		public TestInnerClass(int fieldInt) {
			this.fieldInt = fieldInt;
		}

		public int getFieldInt() {
			return fieldInt * 11;
		}
	}
}
