package com.example.jcparser.test;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.InvalidParameterException;

public class TestClass {

	public static final float FLOAT_CONSTANT_VALUE = 3.3333f;
	public static final double DOUBLE_CONSTANT_VALUE = 2.2222;
	public static final String STRING_CONSTANT_VALUE = "local string";
	private static final long LONG_CONSTANT_VALUE = (long) Integer.MAX_VALUE * 4;

	public static void main(String[] args) {
		TestClass testClassVar = new TestClass();
		testClassVar.testMethod();
		String res = testClassVar.testMethodWithParameters(1, "test");
	}

	@Deprecated
	private int oldCodeMethod() {
		return 10;
	}

	@TestInvisibleAnnotation(hName = "New invisible name", hId = 2)
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
		long aLong = (long) testInnerClass.getFieldInt() * Integer.MAX_VALUE + LONG_CONSTANT_VALUE;
		System.out.println(aLong);
		float aFloat = FLOAT_CONSTANT_VALUE;
		double aDouble = DOUBLE_CONSTANT_VALUE;
		aDouble += aFloat;
		System.out.println(aDouble);
		TestLocalClass testLocalClass = new TestLocalClass(STRING_CONSTANT_VALUE);
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
