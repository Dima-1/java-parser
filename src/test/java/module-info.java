module com.example.jcparser.test {
	exports com.example.jcparser.test;
	requires com.example.jcparser.module;
	requires org.junit.jupiter.params;
	opens com.example.jcparser.test to org.junit.platform.commons;
	uses com.example.jcparser.test.TestClass.TestService;
	provides com.example.jcparser.test.TestClass.TestService with com.example.jcparser.test.TestClass.ConsolePrintTest;

}