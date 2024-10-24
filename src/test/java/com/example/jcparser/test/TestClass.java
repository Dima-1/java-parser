package com.example.jcparser.test;

import java.lang.annotation.*;
import java.security.InvalidParameterException;

public class TestClass {
	public static void main(String[] args) {
		TestClass testClassVar = new TestClass();
		testClassVar.testMethod();
		String res = testClassVar.testMethodWithParameters(1, "test");
	}

	@Deprecated
	private int oldCodeMethod() {
		return 10;
	}

	@TestInvisibleAnnotation(hName = "New h name", hId = 2)
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

	@TestAnnotation(name = "New name 1", id = 1)
	@TestAnnotation(name = "New name 2", id = 2)
	private String testMethodWithParameters(int i, final String test) throws InvalidParameterException {
		if (i == 0) {
			throw new InvalidParameterException(test);
		}
		return switch (i) {
			case 1 -> "One " + test;
			case 2 -> "Two " + test;
			case 33 -> "Thirty three " + test;
			default -> throw new IllegalStateException("Unexpected value: " + i);
		};
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

	@Repeatable(TestRepeatableAnnotations.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface TestAnnotation {
		String name() default "default name";

		int id() default 0;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@interface TestRepeatableAnnotations {
		TestAnnotation[] value();
	}

	@Retention(RetentionPolicy.CLASS)
	@interface TestInvisibleAnnotation {
		String hName() default "default hidden name";

		int hId() default 1;
	}
}
