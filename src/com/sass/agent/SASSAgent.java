package com.sass.agent;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;

import com.sass.mode.SASSMode;

/**
 * The SASS-Agent, which will be set in a particular SASS-MODE and other necessary
 * details, will notify the SASS-Slave to perform the required action.
 * Actions can be:
 * 		1. Non-SASS commands :- Commands apart from file read and file write
 *      2. SASS commands :- File operations(Read and write)
 *
 * @author Aditya R.Singh
 * @team Shushrut Sawant - Aditya Singh - Sujwal Shetty - Sanchita Shetty (S.A.S.S.)
 * @date 25/08/2016
 * @email adipratapsingh.aps@gmail.com
 * @purpose NTAL project
 */
public class SASSAgent implements Serializable {
	private String targetFilePath;
	private byte[] data;
	private SASSMode mode;
	
	private String output;
	private String command;
	
	
	
	// To set the mode
	public void setMode(SASSMode mode) {this.mode = mode;}
	
	// To get the mode
	public SASSMode getMode() {return mode;}
	
	
	
	/************************************************************************************/
	
	
	
	// To set the entire file path
	public void setTargetFilePath(String targetFilePath) {
		// If file path starts with " or '
		if(targetFilePath.startsWith("\"") || targetFilePath.startsWith("\""))
			targetFilePath = targetFilePath.substring(1);
			
		// If file path ends with " or '	
		if(targetFilePath.endsWith("\"") || targetFilePath.endsWith("\'"))	
			targetFilePath = targetFilePath.substring(0, targetFilePath.length() - 1);
			
		this.targetFilePath = targetFilePath;
	}
	
	// To read the file
	public void readFile() throws IOException {
		File file = new File(targetFilePath);
		data = new byte[(int)file.length()]; // Create an empty byte array of the size of the file
		
		// Read the file and store it in byte arrays
		FileInputStream fis = new FileInputStream(file);
		fis.read(data);
		fis.close();
	}
	
	// To write the file
	public void writeFile() throws IOException {
		String[] fileChunks = targetFilePath.split("/");
		
		// Remove the actual file name and just get the path
		targetFilePath = "";
		for(int i = 0; i < fileChunks.length - 1; i++)
			targetFilePath += fileChunks[i]+"/";
		
		// Create the missing directories	
		File directories = new File(targetFilePath);
		if(!directories.exists())
			directories.mkdirs();
			
		// Write the data to the file 	
		File file = new File(targetFilePath+"/"+fileChunks[fileChunks.length - 1]);	
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(data);
		fos.flush();
		fos.close();
	}
	
	/************************************************************************************/
	
	// To set the command
	public void setCommand(String command) {this.command = command;}
	
	// To get output
	public String getOutput() {return output;}
	
	// To execute NON-SASS command
	public void executeNonSASSCommand() throws Exception {
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
		
		Scanner sc = new Scanner(p.exitValue() == 0 ? p.getInputStream() : p.getErrorStream());
		output = "";
		
		while(sc.hasNextLine())
			output += sc.nextLine() + "\r\n";
		
		sc.close();
	}
}