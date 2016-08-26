package com.sass.mode;

/**
 * These are the modes that SASS-Agent can be set in.
 *
 * @author Aditya R.Singh
 * @team Shushrut Sawant - Aditya Singh - Sujwal Shetty - Sanchita Shetty (S.A.S.S.)
 * @date 25/08/2016
 * @email adipratapsingh.aps@gmail.com
 * @purpose NTAL project
 */
public enum SASSMode {
	SASS_READ_FILE_MODE,  // To read a file fromSASS-Slave
	SASS_WRITE_FILE_MODE, // To write a file to SASS-Slave
	NON_SASS_MODE         // To execute shell commands on SASS-Slave
}