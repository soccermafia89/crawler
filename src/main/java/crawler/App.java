package crawler;

import java.util.Date;

/**
 * Hello world!
 * 
 */
public class App {

	public static void main(String[] args) {
		System.out.println("");
		System.out.println("");
		System.out.println("Starting!");
		
		//Display time and date
		Date date = new Date();
		System.out.println(date.toString());

		System.out.println("");
		Driver driver = new Driver();
		driver.drive(args);
	}
}
