/**
 * Copyright 2010 Phil Jacobsma
 * 
 * This file is part of Figures.
 *
 * Figures is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Figures is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Figures; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.bluewindows.qif.test;

import org.bluewindows.qif.HandleAccountType;
import org.bluewindows.qif.QIFAccountType;
import org.bluewindows.qif.QIFCallResult;

import junit.framework.TestCase;

public class HandleAccountTypeTest extends TestCase {
	
	public void testHandleGoodHeader() {
		String header = "Type:Bank";
		QIFCallResult result = HandleAccountType.INSTANCE.handle(header);
		assertTrue(result.isCallOK());
		assertTrue(result.getReturnedObject() instanceof QIFAccountType);
		assertEquals(QIFAccountType.Bank, result.getReturnedObject());
	}
	
	public void testHandleBadHeader() {
		String header = "";
		QIFCallResult result = HandleAccountType.INSTANCE.handle(header);
		assertTrue(result.isCallBad());
		assertEquals("Invalid account type:  found ", result.getException().getMessage());
		
		header = "Type:Bank:Card";
		result = HandleAccountType.INSTANCE.handle(header);
		assertTrue(result.isCallBad());
		assertEquals("Invalid account type: Type:Bank:Card found ", result.getException().getMessage());
		
		header = "Test:Bank";
		result = HandleAccountType.INSTANCE.handle(header);
		assertTrue(result.isCallBad());
		assertEquals("Invalid account type: Test:Bank found ", result.getException().getMessage());
		
		header = "Type:Bogus";
		result = HandleAccountType.INSTANCE.handle(header);
		assertTrue(result.isCallBad());
		assertEquals("Invalid account type: Bogus found ", result.getException().getMessage());
	}

}
