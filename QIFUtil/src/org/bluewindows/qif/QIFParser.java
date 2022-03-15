/**
 * Copyright 2010 Phil Jacobsma
 * 
 * This file is part of QIFUtil.
 *
 * QIFUtil is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * QIFUtil is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with QIFUtil; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.bluewindows.qif;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.ParseException;

public class QIFParser {
	
	private InputStream inStream;
	public static final char QIF_EOR = (char)94; // Char = '^'
	public static final char CR = (char)13; //Carriage Return
	public static final char EOR = (char)10; //Line Feed - End of Record
	public static final char EOF = (char)-1; //End of file
	private char curChar = 0;
	private StringWriter stringWriter = new StringWriter();
	private int lineNumber = 1;


	public QIFParser(InputStream inStream){
		if (inStream == null){
			throw new IllegalArgumentException("InputStream is null.");
		}
		this.inStream = inStream;
	}
	
	public String getField() throws IOException, ParseException {

		stringWriter.getBuffer().setLength(0);
		if (curChar == 0) curChar = getChar(); //First call
		while (curChar != EOF && (curChar == EOR || curChar == QIF_EOR)) { //Bypass delimiters if any
			curChar = getChar();
		}
		while (curChar != EOF && curChar != EOR && curChar != QIF_EOR){ //Get field characters
			stringWriter.append(curChar);
			curChar = getChar();
		}
		while (curChar != EOF && curChar == EOR) { //Bypass EOR
			curChar = getChar();
		}
		return stringWriter.toString().trim();
	}
	
	public char getChar() throws IOException { //Does not return CR.
		
		curChar = (char)inStream.read();
		while (curChar == CR){curChar = (char)inStream.read();}
		if (curChar == EOR) lineNumber++;
		return curChar;
	}
	
	public char getCurChar(){
		return curChar;
	}

	public int getLineNumber(){
		return lineNumber;
	}
	
	public boolean isNotEOF(){
		return curChar != EOF;
	}

	public boolean isNotQifEor(){
		return curChar != QIF_EOR;
	}

}
