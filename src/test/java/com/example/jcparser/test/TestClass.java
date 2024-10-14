package com.example.jcparser.test;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.InvalidParameterException;

public class TestClass {
	public static void main(String[] args) {
		TestClass testClassVar = new TestClass();
		testClassVar.testMethod();
		testClassVar.testMethodWithParameters(1, "test");
	}

	@Deprecated
	private int oldCodeMethod() {
		return 10;
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

	@TestAnnotation(name = "New name 1", id = 1)
	@TestAnnotation(name = "New name 2", id = 2)
	private void testMethodWithParameters(int i, final String test) throws InvalidParameterException {
		if (i == 0) {
			throw new InvalidParameterException(test);
		}
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
}
