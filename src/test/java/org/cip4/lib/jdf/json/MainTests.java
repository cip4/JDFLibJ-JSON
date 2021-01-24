package org.cip4.lib.jdf.json;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MainTests {

	Main main = new Main();

	@Test
	void getJdfLibVersion() {

		// arrange

		// act
		String result = main.getJdfLibVersion();

		// assert
		Assertions.assertEquals("2.1.7", result, "Version is wrong.");

	}

}
