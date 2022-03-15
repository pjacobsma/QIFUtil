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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;

import org.apache.commons.lang3.time.DateUtils;

public class HandleDate extends AbstractFieldHandler{

	private static final String[] GENERIC_PATTERNS = {"MM/dd/yyyy", "MM/dd''yyyy", "dd.MM.yyyy", "dd/MM/yy", "yyyy-MM-dd"};
	protected static String[] datePatterns = new String[6];
	
	//Set this class up as a Singleton
	public final static HandleDate INSTANCE = new HandleDate();

	protected HandleDate() {
		// Add the local date pattern to the generic patterns
		DateFormat dateInstance = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
		datePatterns[0] = ((SimpleDateFormat)dateInstance).toPattern();
		for (int i = 0; i < GENERIC_PATTERNS.length; i++) {
			datePatterns[i+1] = GENERIC_PATTERNS[i];
		}
	}
	
	public QIFCallResult handle(String fieldContent) {
		QIFCallResult result = new QIFCallResult();
		try {
		    result.setReturnedObject(LocalDate.ofInstant(DateUtils.parseDate(fieldContent, datePatterns).toInstant(), 
		    	ZoneId.systemDefault()));
		} catch (ParseException e) {
			result.setCallBad(new ParseException("Invalid date value: [" + fieldContent + "] found ", 0));
		}
		return result;
	}

}
