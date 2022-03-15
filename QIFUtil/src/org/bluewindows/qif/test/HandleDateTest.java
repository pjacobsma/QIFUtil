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

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.bluewindows.qif.HandleDate;
import org.bluewindows.qif.QIFCallResult;

import junit.framework.TestCase;

public class HandleDateTest extends TestCase {
	
	private static final String[] savedPatterns = new String[]{"M/d/yy", "MM/dd/yyyy", "MM/dd''yyyy", "dd.MM.yyyy", "dd/MM/yy", "yyyy-MM-dd"};
	
	public void testHandleGenericDates() throws Exception{
		String dateString = "1/1'1970";
		LocalDate expectedDate = LocalDate.parse("1/1/1970", DateTimeFormatter.ofPattern("M/d/yyyy"));
		try {
			QIFCallResult result = HandleDate.INSTANCE.handle(dateString);
			assertEquals(expectedDate, result.getReturnedObject());
		} catch (Exception e) {
			fail();
		}

		dateString = "1/1/1970";
		expectedDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("M/d/yyyy"));
		try {
			QIFCallResult result = HandleDate.INSTANCE.handle(dateString);
			assertEquals(expectedDate, result.getReturnedObject());
		} catch (Exception e) {
			fail();
		}

		dateString = "31.1.1970";
		expectedDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd.M.yyyy"));
		try {
			QIFCallResult result = HandleDate.INSTANCE.handle(dateString);
			assertEquals(expectedDate, result.getReturnedObject());
		} catch (Exception e) {
			fail();
		}
			
		dateString = "1970-01-31";
		expectedDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		try {
			QIFCallResult result = HandleDate.INSTANCE.handle(dateString);
			assertEquals(expectedDate, result.getReturnedObject());
		} catch (Exception e) {
			fail();
		}
	}
	
	public void testHandleLocalDates() throws ParseException {
		String dateString = "31/12/20";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
		LocalDate expectedDate = LocalDate.parse(dateString, formatter);
		TestHandleDate testHandleDate = null;
		try {
			testHandleDate = new TestHandleDate();
			testHandleDate.setDatePatterns(new String[]{"dd/MM/y", "MM/dd/yyyy", "MM/dd''yyyy", "dd.MM.yyyy", "dd/MM/yy", "yyyy-mm-dd"});
			QIFCallResult result = testHandleDate.handle(dateString);
			assertEquals(expectedDate, result.getReturnedObject());
		} catch (Exception e) {
			fail();
		}
		testHandleDate.setDatePatterns(savedPatterns);
	}
	
	public void testHandleBadDate() throws Exception {
		String dateString = "";
		QIFCallResult result = HandleDate.INSTANCE.handle(dateString);
		assertTrue(result.isCallBad());
		assertTrue(result.getException() instanceof ParseException);
		assertEquals("Invalid date value: [" + dateString + "] found ", result.getException().getMessage());

		dateString = "X";
		result = HandleDate.INSTANCE.handle(dateString);
		assertTrue(result.isCallBad());
		assertTrue(result.getException() instanceof ParseException);
		assertEquals("Invalid date value: [" + dateString + "] found ", result.getException().getMessage());

		dateString = "/1'1970"; //Missing month
		result = HandleDate.INSTANCE.handle(dateString);
		assertTrue(result.isCallBad());
		assertTrue(result.getException() instanceof ParseException);
		assertEquals("Invalid date value: [" + dateString + "] found ", result.getException().getMessage());

		dateString = "1/'1970"; //Missing day
		result = HandleDate.INSTANCE.handle(dateString);
		assertTrue(result.isCallBad());
		assertTrue(result.getException() instanceof ParseException);
		assertEquals("Invalid date value: [" + dateString + "] found ", result.getException().getMessage());

		dateString = "1/1'"; //Missing year
		result = HandleDate.INSTANCE.handle(dateString);
		assertTrue(result.isCallBad());
		assertTrue(result.getException() instanceof ParseException);
		assertEquals("Invalid date value: [" + dateString + "] found ", result.getException().getMessage());
	}
	
	private class TestHandleDate extends HandleDate {
		
		public void setDatePatterns(String[] patterns) {
			TestHandleDate.datePatterns = patterns;
		}
	}

}