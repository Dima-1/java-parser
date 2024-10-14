module com.example.jcparser.test {
	exports com.example.jcparser.test;
	requires com.example.jcparser.module;
	requires org.junit.jupiter.api;
	opens com.example.jcparser.test to org.junit.platform.commons;

}