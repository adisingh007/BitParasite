package com.sass.server;

import java.io.*;
import java.net.*;
import java.util.*;

import com.sass.mode.SASSMode;
import com.sass.agent.SASSAgent;

/**
 * The SASS-Slave program will be controlled by SASS-Master through SASS-Agent.
 * SASS-Agent will be set in a SASS-Mode and with necessary details to SASS-Slave
 * and SASS-Slave will be expected to perform the required actions.
 *
 * @author Aditya R.Singh
 * @team Shushrut Sawant - Aditya Singh - Sujwal Shetty - Sanchita Shetty (S.A.S.S.)
 * @date 25/08/2016
 * @email adipratapsingh.aps@gmail.com
 * @purpose NTAL project
 */
public class SASSServerSlave {
	public static void main(String[] args) throws Exception {
		/* This loop will run forever
		   as the SASS-Slave is never expected to stop but accept connections again and
		   again from SASS-Master  */
		while(true) {
			try {				
				ServerSocket servSock = new ServerSocket(8090); // Listen to port 8090
				Socket sock = servSock.accept(); // Accept a connection
				 
				// Read the SASS-Agent object 
				ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
				SASSAgent sa = (SASSAgent) ois.readObject(); 
				
				// Perform the required action as per the SASS-Mode
				switch(sa.getMode()) {
					case SASS_READ_FILE_MODE   : sa.readFile();
									             break;
					case SASS_WRITE_FILE_MODE  : sa.writeFile();
									             break;
					case NON_SASS_MODE         : sa.executeNonSASSCommand();
											     break;				  
					default                    : System.err.println("Bad request!"); // If case of some bad request
							                     break;
				}
				
				// After performing the action required, re-send the SASS-Agent object back to SASS-Master
				ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
				oos.writeObject(sa);
				oos.flush();
				
				// Closing the connections
				sock.close();
				servSock.close();
			} catch(Exception e) {e.printStackTrace();} 
		}	
	}
}