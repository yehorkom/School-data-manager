package org.example;
public class Main {
    public static void main(String[] args) {
		DbController dbController = new DbController();
		try {
			dbController.createTablesAndFillIn();
		} catch (Exception e) {
			System.out.println("'log' Error in main method");
			throw new RuntimeException(e);
		}
		ConsoleMenu consoleMenu = new ConsoleMenu();
		consoleMenu.launchingConsole();
    }
}
