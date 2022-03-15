package org.bluewindows.qif.test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.bluewindows.qif.QIFAccountType;
import org.bluewindows.qif.QIFCallResult;
import org.bluewindows.qif.QIFCategory;
import org.bluewindows.qif.QIFExporter;
import org.bluewindows.qif.QIFRecord;

import junit.framework.TestCase;

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

public class QIFExporterTest extends TestCase {
	
	private QIFExporter exporter = new QIFExporter();
	private MockBufferedWriter bw;
	private List<QIFRecord> records;
	
	public void setUp() throws Exception {
		super.setUp();
		bw = new MockBufferedWriter(new StringWriter());
		records = new ArrayList<QIFRecord>();
	}
	
	public void testArgumentValidation() throws Exception {
		assertIllegalArgumentException(exporter.exportQIF(null, null, bw), "QIF Account Type is missing.");
		assertIllegalArgumentException(exporter.exportQIF(QIFAccountType.Bank, null, bw), "QIF record list is null.");
		assertIllegalArgumentException(exporter.exportQIF(QIFAccountType.Bank, records, bw), "QIF record list is empty.");
		QIFRecord record = new QIFRecord();
		records.add(record);
		assertIllegalArgumentException(exporter.exportQIF(QIFAccountType.Bank, records, null), "QIF output stream is null.");
	}
	
	public void testRecordValidation() throws IOException{
		QIFRecord record = new QIFRecord();
		records.add(record);
		assertIllegalStateException(exporter.exportQIF(QIFAccountType.Bank, records, bw), "Date is null on record 1.");
		records.get(0).setDate(LocalDate.MIN);
		assertIllegalStateException(exporter.exportQIF(QIFAccountType.Bank, records, bw), "Amount is null on record 1.");
	}
	
	public void testSingleCategoryTransaction() throws IOException, ParseException {
		QIFRecord record = new QIFRecord();
		record.setDate(LocalDate.parse("12/31/2000", DateTimeFormatter.ofPattern("MM/dd/yyyy")));
		record.setAmount(BigDecimal.valueOf(-1234.56));
		record.setNumber("1234");
		record.setDescription("Payee Description");
		record.setMemo("Payee Memo");
		record.addAddressLine("123 Elm St");
		record.addAddressLine("Anytown, USA");
		QIFCategory category = new QIFCategory();
		category.setName("Category");
		record.addCategory(category);
		record.setCleared();
		records.add(record);
		QIFCallResult result = exporter.exportQIF(QIFAccountType.Bank, records, bw);
		assertTrue(result.isCallOK());
		assertEquals("!Type:Bank", bw.getRecord(0));
		assertEquals("D12/31/2000", bw.getRecord(1));
		assertEquals("T-1,234.56", bw.getRecord(2));
		assertEquals("N1234", bw.getRecord(3));
		assertEquals("PPayee Description", bw.getRecord(4));
		assertEquals("MPayee Memo", bw.getRecord(5));
		assertEquals("A123 Elm St", bw.getRecord(6));
		assertEquals("AAnytown, USA", bw.getRecord(7));
		assertEquals("LCategory", bw.getRecord(8));
		assertEquals("SX", bw.getRecord(9));
		assertEquals("^", bw.getRecord(10));
	}
	
	public void testMultipleCategoryTransaction() throws IOException, ParseException {
		QIFRecord record = new QIFRecord();
		record.setDate(LocalDate.parse("12/31/2000", DateTimeFormatter.ofPattern("MM/dd/yyyy")));
		record.setAmount(BigDecimal.valueOf(-100.00));
		QIFCategory category1 = new QIFCategory();
		category1.setName("Category 1");
		category1.setMemo("Category 1 Memo");
		category1.setAmount(BigDecimal.valueOf(-60.00));
		QIFCategory category2 = new QIFCategory();
		category2.setName("Category 2");
		category2.setMemo("Category 2 Memo");
		category2.setAmount(BigDecimal.valueOf(-40.00));
		record.getCategories().add(category1);
		record.getCategories().add(category2);
		records.add(record);
		QIFCallResult result = exporter.exportQIF(QIFAccountType.Bank, records, bw);
		assertTrue(result.isCallOK());
		assertEquals("!Type:Bank", bw.getRecord(0));
		assertEquals("D12/31/2000", bw.getRecord(1));
		assertEquals("T-100.00", bw.getRecord(2));
		assertEquals("SCategory 1", bw.getRecord(3));
		assertEquals("$-60.00", bw.getRecord(4));
		assertEquals("ECategory 1 Memo", bw.getRecord(5));
		assertEquals("SCategory 2", bw.getRecord(6));
		assertEquals("$-40.00", bw.getRecord(7));
		assertEquals("ECategory 2 Memo", bw.getRecord(8));
		assertEquals("^", bw.getRecord(9));
	}
	
	private void assertIllegalArgumentException(QIFCallResult result, String message) {
		assertTrue(result.isCallBad());
		assertTrue("Wrong exception in result.", result.getException() instanceof IllegalArgumentException);
		assertEquals(message, result.getException().getMessage());
	}

	private void assertIllegalStateException(QIFCallResult result, String message) {
		assertTrue(result.isCallBad());
		assertTrue("Wrong exception in result.", result.getException() instanceof IllegalStateException);
		assertEquals(message, result.getException().getMessage());
	}

	private class MockBufferedWriter extends BufferedWriter {

		private List<String> recordList = new ArrayList<String>();
		private String record = "";
		
		public MockBufferedWriter(Writer arg0) {
			super(arg0);
		}
		
		public void write(String string){
			record = record + string;	
		}
		
		public void newLine(){
			recordList.add(record);
			record = new String("");
		}
		
		public String getRecord(int i){
			return recordList.get(i);
		}
	}

}
