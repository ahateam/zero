package zyxhj.utils.data;

import java.util.Arrays;

import org.junit.Test;

import zyxhj.utils.api.ServerException;

public class TEXPTest {

	@Test
	public void testParams() {
		try {
			TEXP.Bool(TEXP.BOOL_TYPE_OR, Arrays.asList(TEXP.Match("f1", "txt1"),TEXP.Match("f2", "txt2")));
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
