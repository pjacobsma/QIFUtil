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

import java.math.BigDecimal;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;

public class HandleBigDecimal extends AbstractFieldHandler {

	//Set this class up as a Singleton
	public final static HandleBigDecimal INSTANCE = new HandleBigDecimal();
	protected HandleBigDecimal() {}
	
	public QIFCallResult handle(String fieldContent) {
		QIFCallResult result = new QIFCallResult();
		fieldContent = StringUtils.remove(fieldContent, ",");
		fieldContent = StringUtils.remove(fieldContent, "=");
		BigDecimal amount;
		try {
			amount = new BigDecimal(fieldContent.trim());
		} catch (Exception e) {
			result.setCallBad(new ParseException("Invalid amount value: [" + fieldContent + "] found ", 0));
			return result;
		}
		result.setReturnedObject(amount);
		return result;
	}

}
