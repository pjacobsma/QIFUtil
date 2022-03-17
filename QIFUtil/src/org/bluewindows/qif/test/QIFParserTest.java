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

import junit.framework.TestCase;

import org.bluewindows.qif.QIFParser;

public class QIFParserTest extends TestCase{
	
	public void testConstructor() throws Exception {
		try {
			@SuppressWarnings("unused")
			QIFParser parser = new QIFParser(null);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}
	
	public void testGetCharEOF() throws Exception {
		MockInputStream mockStream = new MockInputStream("" + QIFParser.EOF);
		QIFParser parser = new QIFParser(mockStream);
		parser.getChar();
		assertFalse(parser.isNotEOF());
	}
	
	public void testGetCharQifEor() throws Exception {
		MockInputStream mockStream = new MockInputStream("" + QIFParser.QIF_EOR);
		QIFParser parser = new QIFParser(mockStream);
		parser.getChar();
		assertFalse(parser.isNotQifEor());
	}
	
	public void testGetCharLineNumber() throws Exception {
		MockInputStream mockStream = new MockInputStream("X");
		QIFParser parser = new QIFParser(mockStream);
		parser.getChar();
		assertEquals(1, parser.getLineNumber());
		
		mockStream = new MockInputStream("" + QIFParser.EOR + QIFParser.EOR + "X");
		parser = new QIFParser(mockStream);
		parser.getChar();
		parser.getChar();
		assertEquals('X', parser.getChar());
		assertEquals(3, parser.getLineNumber());
	}
	
	public void testGetCharData() throws Exception {
		MockInputStream mockStream = new MockInputStream(QIFParser.CR + "X");
		QIFParser parser = new QIFParser(mockStream);
		char actualChar = parser.getChar();
		assertEquals('X', actualChar);
	}
	
	public void testGetField() throws Exception {
		MockInputStream mockStream = new MockInputStream("" + QIFParser.EOR + "X" + QIFParser.EOR);
		QIFParser parser = new QIFParser(mockStream);
		assertEquals("X", parser.getField());
		
		String qifRecDelimiter = new String("" + QIFParser.QIF_EOR + QIFParser.EOR);
		mockStream = new MockInputStream("" + qifRecDelimiter + "X" + QIFParser.EOR + qifRecDelimiter + 
			"Y" + QIFParser.EOR + qifRecDelimiter);
		parser = new QIFParser(mockStream);
		assertEquals("X", parser.getField());
		assertFalse(parser.isNotQifEor());
		assertEquals("Y", parser.getField());
		assertFalse(parser.isNotQifEor());
		assertTrue(parser.isNotEOF());
		assertEquals("", parser.getField());
		assertFalse(parser.isNotEOF());
	}
	
	public void testGetRecord() throws Exception {
		MockInputStream mockStream = new MockInputStream("abc" + QIFParser.EOR + "def");
		QIFParser parser = new QIFParser(mockStream);
		assertEquals("abc", parser.getField());
		assertEquals("def", parser.getField());
		assertFalse(parser.isNotEOF());

		mockStream = new MockInputStream("abc" + QIFParser.EOR + QIFParser.QIF_EOR + QIFParser.EOR + "def");
		parser = new QIFParser(mockStream);
		assertEquals("abc", parser.getField());
		assertEquals("def", parser.getField());
		assertFalse(parser.isNotEOF());
	}
	
	public void testGetEmptyRecord() throws Exception {
		MockInputStream mockStream = new MockInputStream(QIFParser.EOR + "");
		QIFParser parser = new QIFParser(mockStream);
		String record = parser.getField();
		assertEquals("", record);
	}
	
	public void testGetRecordPrematureEOF() throws Exception {
		MockInputStream mockStream = new MockInputStream("abc" + QIFParser.EOF);
		QIFParser parser = new QIFParser(mockStream);
		parser.getField();
		assertFalse(parser.isNotEOF());
	}
	
}
