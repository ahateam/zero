package zyxhj.flow;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Test;

public class JSEngineTest {

	@Test
	public void flowTest() throws Exception {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

		engine.eval("Java.type('zyxhj.flow.JSEngineTest.testFunc(123,\"testName\")')");
	}

	public static String testFunc(int num, String name) {
		return ">>>" + num + " & " + name;
	}

}
