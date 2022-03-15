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

package org.bluewindows.qif.test;

import java.math.BigDecimal;
import java.text.ParseException;

import junit.framework.TestCase;

import org.bluewindows.qif.HandleBigDecimal;
import org.bluewindows.qif.QIFCallResult;

public class HandleBigDecimalTest extends TestCase{

	public void testHandleGoodData() throws Exception{
		String amountString = "-9,999.99";
		BigDecimal expectedBigDecimal = new BigDecimal("-9999.99");
		QIFCallResult result = HandleBigDecimal.INSTANCE.handle(amountString);
		assertTrue(result.isCallOK());
		assertTrue(result.getReturnedObject() instanceof BigDecimal);
		assertEquals(expectedBigDecimal, (BigDecimal)result.getReturnedObject());
	}
	
	public void testHandleBadData() throws Exception {
		String amountString = "X";
		QIFCallResult result = null;
		result = HandleBigDecimal.INSTANCE.handle(amountString);
		assertTrue(result.isCallBad());
		assertTrue(result.getException() instanceof ParseException);
		assertEquals(result.getException().getMessage(), "Invalid amount value: [" + amountString + "] found ");
	}
}
