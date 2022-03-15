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

import java.text.ParseException;


public class HandleAccountType extends AbstractFieldHandler {

	//Set this class up as a Singleton
	public final static HandleAccountType INSTANCE = new HandleAccountType();
	protected HandleAccountType() {}
	
	public QIFCallResult handle(String fieldContent) {
		QIFCallResult result = new QIFCallResult();
		String[] strings;
		strings = fieldContent.split(":");
		if (strings.length == 1){
			result.setCallBad(new ParseException("Invalid account type: " + fieldContent + " found ", 0));
		}
		if (strings.length != 2){
			result.setCallBad(new ParseException("Invalid account type: " + fieldContent + " found ", 0));
			return result;
		}
		if (! strings[0].equals("Type")){
			result.setCallBad(new ParseException("Invalid account type: " + fieldContent + " found ", 0));
			return result;
		}
		QIFAccountType accountType = null;
		try {
			accountType = QIFAccountType.valueOf(strings[1]);
		} catch (IllegalArgumentException e) {
			result.setCallBad(new ParseException("Invalid account type: " + strings[1] + " found ", 0));
			return result;
		}
		
		result.setReturnedObject(accountType);
		return result;
	}

}
