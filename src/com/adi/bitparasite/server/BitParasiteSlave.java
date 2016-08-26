package com.adi.bitparasite.server;

import java.io.*;
import java.net.*;
import java.util.*;

import com.adi.bitparasite.mode.BitParasiteMode;
import com.adi.bitparasite.agent.BitParasiteAgent;

/**
 * The Slave program will be controlled by Master through Agent.
 * Agent will be set in a Mode and with necessary details to Slave
 * and Slave will be expected to perform the required actions.
 *
 * Disclaimer: FOR EDUCATIONAL PURPOSE ONLY!
 *
 * @author Aditya R.Singh
 * @date 25/08/2016
 * @email adipratapsingh.aps@gmail.com
 */
public class BitParasiteSlave {
	public static void main(String[] args) throws Exception {
		/* This loop will run forever
		   as the Slave is never expected to stop but accept connections again and
		   again from Master  */
		while(true) {
			try {				
				ServerSocket servSock = new ServerSocket(8090); // Listen to port 8090
				Socket sock = servSock.accept(); // Accept a connection
				 
				// Read the Agent object 
				ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
				BitParasiteAgent bpa = (BitParasiteAgent) ois.readObject(); 
				
				// Perform the required action as per the BitParasiteMode
				switch(bpa.getMode()) {
					case BIT_PARASITE_READ_FILE_MODE   : bpa.readFile();
									             		 break;
					case BIT_PARASITE_WRITE_FILE_MODE  : bpa.writeFile();
									             		 break;
					case NON_BIT_PARASITE_MODE         : bpa.executeNonBitParasiteCommand();
											     		 break;				  
					default         		           : System.err.println("Bad request!"); // If case of some bad request
							                    		 break;
				}
				
				// After performing the action required, re-send the BitParasiteAgent object back to Master
				ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
				oos.writeObject(bpa);
				oos.flush();
				
				// Closing the connections
				sock.close();
				servSock.close();
			} catch(Exception e) {e.printStackTrace();} 
		}	
	}
}