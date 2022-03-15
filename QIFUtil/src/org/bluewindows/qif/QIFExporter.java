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

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class QIFExporter {
	
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static final DecimalFormat AMOUNT_FORMAT = new DecimalFormat("###,##0.00");
	
	public QIFCallResult exportQIF(QIFAccountType accountType, List<QIFRecord> records, BufferedWriter bw)  {
		
		QIFCallResult result = new QIFCallResult();
		if (accountType == null){
			result.setCallBad(new IllegalArgumentException("QIF Account Type is missing."));
			return result;
		}
		if (records == null){
			result.setCallBad(new IllegalArgumentException("QIF record list is null."));
			return result;
		}
		if (records.isEmpty()){
			result.setCallBad(new IllegalArgumentException("QIF record list is empty."));
			return result;
		}
		if (bw == null){
			result.setCallBad(new IllegalArgumentException("QIF output stream is null."));
			return result;
		}
		result = validateRecord(records.get(0), 1);
		if (result.isCallBad()) return result;
		result = writeHeader(accountType, records, bw);
		if (result.isCallBad()) return result;
		for (int i = 0; i <= records.size()-1; i++) {
			result = writeRecord(records.get(i), i, bw);
			if (result.isCallBad()) break;
		}
		try {
			bw.close();
		} catch (IOException e) {
			result.setCallBad(e);
		}
		return result;
	}

	private QIFCallResult writeHeader(QIFAccountType accountType, List<QIFRecord> records, BufferedWriter bw) {
		QIFCallResult result = new QIFCallResult();
		try {
			writeLine(bw, "!Type:" + accountType.getLabel());
		} catch (IOException e) {
			result.setCallBad(e);
			return result;
		}
		return result;
	}
	
	private QIFCallResult validateRecord(QIFRecord record, int recordNumber){
		QIFCallResult result = new QIFCallResult();
		if (record.getDate() == null){ 
			result.setCallBad(new IllegalStateException("Date is null on record " + recordNumber + "."));
			return result;
		}
		if (record.getAmount() == null){ 
			result.setCallBad(new IllegalStateException("Amount is null on record " + recordNumber + "."));
			return result;
		}
		return result;
	}

	private QIFCallResult writeRecord(QIFRecord record, int recordNumber, BufferedWriter bw) {
		QIFCallResult result = validateRecord(record, recordNumber);
		if (result.isCallBad()) return result;
		try {
			writeLine(bw, "D" + DATE_FORMAT.format(record.getDate()));
			writeLine(bw, "T" + AMOUNT_FORMAT.format(record.getAmount()));
			if (!record.getNumber().isEmpty()) {
				writeLine(bw, "N" + record.getNumber());
			}
			if (!record.getDescription().isEmpty()) {
				writeLine(bw, "P" + record.getDescription());
			}
			if (!record.getMemo().isEmpty()) {
				writeLine(bw, "M" + record.getMemo());
			}
			if (!record.getAddresses().isEmpty()) {
				for (String addressLine : record.getAddresses()) {
					writeLine(bw, "A" + addressLine);
				}
			}
			if (record.getCategories().size() == 1) {
				writeLine(bw, "L" + record.getCategories().get(0).getName());
			}else if (record.getCategories().size() > 1) {
				for (QIFCategory category : record.getCategories()) {
					writeLine(bw, "S" + category.getName());
					writeLine(bw, "$" + AMOUNT_FORMAT.format(category.getAmount()));
					if (category.getMemo() != null && !category.getMemo().isEmpty()) {
						writeLine(bw, "E" + category.getMemo());
					}
				}
			}
			if (!record.getStatus().isEmpty()) {
				writeLine(bw, "S" + record.getStatus());
			}
			writeLine(bw, "^");
		} catch (IOException e) {
			result.setCallBad(e);
		}
		return result;
	}
	
	private void writeLine(BufferedWriter bw, String content) throws IOException {
		bw.write(content);
		bw.newLine();
	}

}
