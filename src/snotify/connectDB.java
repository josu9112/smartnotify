package snotify;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class connectDB {
	private static String user;
	private static String pw;
	private static String ip;
	public static Connection connection;
	
	public static void connectToDB() throws FileNotFoundException, SQLException {
		Scanner scan = new Scanner(new FileReader("/home/rustax/Documents/dblogin.txt"));
		String user = scan.nextLine();
		user = user.substring(user.indexOf(" ")+1);
		String pw = scan.nextLine();
		pw = pw.substring(pw.indexOf(" ")+1);
		String ip = scan.nextLine();
		ip = ip.substring(ip.indexOf(" ")+1);
		scan.close();
		connection = DriverManager.getConnection("jdbc:mariadb://" + ip + "/test?user=" + user + "&password=" + pw);
	}
}
