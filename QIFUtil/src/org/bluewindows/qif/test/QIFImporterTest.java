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

import static org.bluewindows.qif.QIFParser.EOF;
import static org.bluewindows.qif.QIFParser.EOR;
import static org.bluewindows.qif.QIFParser.QIF_EOR;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.bluewindows.qif.QIFAccountType;
import org.bluewindows.qif.QIFCallResult;
import org.bluewindows.qif.QIFImporter;
import org.bluewindows.qif.QIFPackage;
import org.bluewindows.qif.QIFParser;
import org.bluewindows.qif.QIFRecord;

import junit.framework.TestCase;

public class QIFImporterTest extends TestCase {

	private QIFImporter importer = new QIFImporter();
	
	public void testImportBadHeaders() throws Exception {

		//Empty file
		String file = "";
		assertParseException(importer.importQIF(stream(file)), "Not a valid QIF file. No QIF header found.");
		//Missing header
		file = "abcxyz";
		assertParseException(importer.importQIF(stream(file)), "Not a valid QIF file. No QIF header found.");
		//Invalid header
		file = "!Bogus" + QIF_EOR + QIFParser.EOF;
		assertParseException(importer.importQIF(stream(file)), "Not a valid QIF file. No QIF header found.");
	}
	
	public void testImportBadFieldType() {
		String file = "!Type:Bank" + EOR + 
				"D1/31'1999" + EOR +
				"T9,999.99" + EOR +
				"ZBad Type" + EOF;
		assertParseException(importer.importQIF(stream(file)), "Invalid field type: Z found on line 4.");
	}
	
	public void testImportGoodFile() throws Exception {

		String file = " !Type:Bank " + EOR + 
			" D 1/31'1999" + EOR +
			"MMemo"  + EOR +
			"T9,999.99" + EOR +
			"PPayee" + EOR +
			"AAddress Line 1" + EOR +
			"AAddress Line 2" + EOR +
			"AAddress Line 3" + EOR +
			"AAddress Line 4" + EOR +
			"AAddress Line 5" + EOR +
			"AOptional Message" + EOR +
			"LCategory" + QIF_EOR + EOR + QIFParser.EOF;
		QIFCallResult result = importer.importQIF(stream(file));
		assertTrue(result.isCallOK());
		QIFPackage qifPackage = (QIFPackage)result.getReturnedObject();
		assertEquals(QIFAccountType.Bank, qifPackage.getAccountType());
		List<QIFRecord> recordList = qifPackage.getRecords();
		assertEquals(1, recordList.size());
		
		QIFRecord record = recordList.get(0);
		assertEquals( "Memo", record.getMemo());
		assertEquals(new BigDecimal("9999.99"), record.getAmount());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		assertEquals(LocalDate.parse("01/31/1999", formatter), record.getDate());
		assertEquals("Payee", record.getDescription() );
		assertEquals(5, record.getAddresses().size());
		assertEquals("Address Line 1", record.getAddresses().get(0));
		assertEquals("Address Line 2", record.getAddresses().get(1));
		assertEquals("Address Line 3", record.getAddresses().get(2));
		assertEquals("Address Line 4", record.getAddresses().get(3));
		assertEquals("Address Line 5", record.getAddresses().get(4));
		assertEquals("Optional Message", record.getOptionalMessage());
		assertEquals(1, record.getCategories().size());
		assertEquals("Category", record.getCategories().get(0).getName());
	}
	
	public void testImportSplitCategories() throws Exception {

		String file = "!Type:Bank" + EOR + 
			"D1/31'1999" + EOR +
			"MMemo"  + EOR +
			"T9,999.99" + EOR +
			"PPayee" + EOR +
			"SCategory 1" + EOR + 
			"ECategory 1 Memo" + EOR +
			"$-123.45" + EOR +
			"SCategory 2" + EOR + 
			"ECategory 2 Memo" + EOR +
			"$=456.78" + QIF_EOR + EOF;
		QIFCallResult result = importer.importQIF(stream(file));
		assertTrue(result.isCallOK());
		QIFPackage qifPackage = (QIFPackage)result.getReturnedObject();
		assertEquals(QIFAccountType.Bank, qifPackage.getAccountType());
		List<QIFRecord> recordList = qifPackage.getRecords();
		assertEquals(1, recordList.size());
		QIFRecord record = recordList.get(0);
		assertEquals( "Memo", record.getMemo());
		assertEquals(new BigDecimal("9999.99"), record.getAmount());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		assertEquals(LocalDate.parse("01/31/1999", formatter), record.getDate());
		assertEquals("Payee", record.getDescription() );
		assertEquals(2, record.getCategories().size());
		assertEquals("Category 1", record.getCategories().get(0).getName());
		assertEquals("Category 1 Memo", record.getCategories().get(0).getMemo());
		assertEquals(new BigDecimal("-123.45"), record.getCategories().get(0).getAmount());
	}
	
	private MockInputStream stream(String streamContents){
		return new MockInputStream(streamContents);
	}
	
	private void assertParseException(QIFCallResult result, String message) {
		assertTrue(result.isCallBad());
		assertTrue(result.getException() instanceof ParseException);
		assertEquals(message, result.getException().getMessage());
	}

}
