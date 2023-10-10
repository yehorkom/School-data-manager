package org.example;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataManagerTest {

	DbController dbController = new DbController();
	DataGenerator dataGenerator = new DataGenerator();
	ConsoleMenu consoleMenu = new ConsoleMenu();
	@Test
	void testIsCreateTablesWork() {
		assertDoesNotThrow(() -> dbController.createTablesAndFillIn());
	}

	@Test
	void testIsLaunchingConsoleWork() {
		Scanner mockScanner = new Scanner("Exiting.");
		String simulatedUserInput = "q\n";
		System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));
		consoleMenu.launchingConsole();
		assertEquals("Exiting.", mockScanner.nextLine());
	}

	@Test
	void testIsDataGeneratorWork() {
		assertDoesNotThrow(() -> dataGenerator.generateData());
	}

	@Test
	void testIsTablesExist() throws SQLException {
		assertTrue(dbController.tablesExist());
	}

	@Test
	void testIsDropTablesWork() {
		assertDoesNotThrow(() -> dbController.dropTables());
	}
}

