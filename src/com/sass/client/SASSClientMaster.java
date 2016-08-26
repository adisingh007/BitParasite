package com.sass.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import com.sass.mode.SASSMode;
import com.sass.agent.SASSAgent;

/**
 * The SASS-Master program will control SASS-Slave through SASS-Agent.
 * This will set the SASS-Mode of operation/s to be performed on the SASS-Slave
 * and the SASS-Agent will notify it with necessary details.
 *
 * @author Aditya R.Singh
 * @team Shushrut Sawant - Aditya Singh - Sujwal Shetty - Sanchita Shetty (S.A.S.S.)
 * @date 25/08/2016
 * @email adipratapsingh.aps@gmail.com
 * @purpose NTAL project
 */
public class SASSClientMaster {

	private static final String SASS_SIGNATURE = "sass";
	private static final String SASS_READ      = "read";
	private static final String SASS_WRITE     = "write";
	private static final String SASS_EXIT      = "exit";
	
	public static void main(String[] args) {
		// IP address and the port number to connect to
		String ip = args[0];
		int port  = Integer.parseInt(args[1]);
		 
		Scanner sc = new Scanner(System.in); // To keep reading commands from the terminal
		
		/* This loop will keep running forever
		   as the SASS-Master is never expected to stop but make connections again and
		   again with SASS-Slave */
		while(true) {
			try {
				// To establish connection with the SASS-Slave
				Socket socket = new Socket(ip, port); 
				System.out.println("Socket connected to "+ip+":"+port);
				
				// Reading the command to be executed		
				String command = sc.nextLine().trim();
				
				// If not a SASS command
				if(!isSass(command))
					System.out.println(execNonSASSCommandOnSlave(command, socket)); 
				else {
					// If it is a SASS command
					String action = command.split(" ")[1]; // The SASS operation to be performed
					
					if(action.equals(SASS_EXIT))
						break;
					else if(action.equals(SASS_READ))
						readFileFromSlave(command, socket);
					else if(action.equals(SASS_WRITE))
						writeFileToSlave(command, socket);
					else
						System.err.println("Please enter a valid command!");
				}
				socket.close();
			} catch(Exception e) {e.printStackTrace();}	
		}
		sc.close();
		System.out.println("Thankyou for using SASS!");
	}
	
	
	// To check if a sass command is requested.
	private static boolean isSass(String command) {
		return command.startsWith(SASS_SIGNATURE);
	}
	
	
	// To execute Non-SASS commands on SASS-SLAVE
	private static String execNonSASSCommandOnSlave(String command, Socket socket) throws Exception {
		SASSAgent sa = new SASSAgent();
		sa.setMode(SASSMode.NON_SASS_MODE);
		sa.setCommand(command); // Setting the command to be executed on the SASS-Slave
		
		// Sending the object to SASS-Slave
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(sa);
		oos.flush();
		
		// Receiving the object back with the data
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		sa = (SASSAgent) ois.readObject();
		
		return sa.getOutput(); // Returning the output
	}
	
	
	// Will read file from SASS-Slave and get it back
	private static void readFileFromSlave(String command, Socket socket) throws Exception {
		SASSAgent sa = new SASSAgent();
		sa.setMode(SASSMode.SASS_READ_FILE_MODE);
		sa.setTargetFilePath(command.split(" ")[2]); // The file to be targeted on SASS-Slave
		
		// Sending the object to SASS-Slave
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(sa);
		oos.flush();
		
		// Receiving the object back with the data
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		sa = (SASSAgent) ois.readObject();
		
		sa.setTargetFilePath(command.split(" ")[3]); // The destination where the file is to be written on SASS-Master
		sa.writeFile();
	}
	
	
	// Will write the file on the SASS-Slave
	private static void writeFileToSlave(String command, Socket socket) throws Exception {
		SASSAgent sa = new SASSAgent();
		sa.setMode(SASSMode.SASS_WRITE_FILE_MODE);
		sa.setTargetFilePath(command.split(" ")[2]); // The file to be read from SASS-Master
		sa.readFile();
		
		// Sending the object to the SASS-Slave
		sa.setTargetFilePath(command.split(" ")[3]); // Destination file on SASS-Slave
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(sa);
		oos.flush();
		
		// Receiving the object back after performing write operation on SASS-Master
		// Do nothing with the object received. Just a part of the SASS protocol.
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		sa = (SASSAgent) ois.readObject();
	}
}