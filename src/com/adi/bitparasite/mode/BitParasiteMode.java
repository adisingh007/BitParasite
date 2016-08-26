package com.adi.bitparasite.mode;

/**
 * These are the modes that Agent can be set in.
 *
 * Disclaimer: FOR EDUCATIONAL PURPOSE ONLY!
 *
 * @author Aditya R.Singh
 * @date 25/08/2016
 * @email adipratapsingh.aps@gmail.com
 */
public enum BitParasiteMode {
	BIT_PARASITE_READ_FILE_MODE,  // To read a file from Slave
	BIT_PARASITE_WRITE_FILE_MODE, // To write a file to Slave
	NON_BIT_PARASITE_MODE         // To execute shell commands on Slave
}