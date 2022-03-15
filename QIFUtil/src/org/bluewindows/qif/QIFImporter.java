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

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;

public class QIFImporter { 
	
	private QIFPackage qifPackage;
	private QIFParser parser;
	private int lineNumber;
	
	public QIFCallResult importQIF(InputStream inStream) {
		QIFCallResult result = new QIFCallResult();
		parser = new QIFParser(inStream);
		qifPackage = new QIFPackage();
		lineNumber = 0;
		result = processHeader(inStream);
		if (result.isCallBad()) return result;
		QIFRecord emptyRecord = new QIFRecord();
		while (parser.isNotEOF()) {
			result = processRecord(parser);
			if (result.isCallBad()) return addLineNumberToMessage(result, parser.getLineNumber());
			QIFRecord record = (QIFRecord)result.getReturnedObject();
			if (! record.equals(emptyRecord)) qifPackage.getRecords().add(record);
		}
		result.setReturnedObject(qifPackage);
		return result;
	}

	private QIFCallResult processHeader(InputStream inStream) {
		QIFCallResult result = new QIFCallResult();
		String field;
		try {
			field = parser.getField();
		} catch (Exception e) {
			result.setCallBad(e);
			return result;
		}
		if (field.isBlank() || (!field.startsWith("!Type:"))) {
			return result.setCallBad(new ParseException("Not a valid QIF file. No QIF header found.", 0));
		}
		QIFRecord headerRecord = new QIFRecord();
		result = processField(field, headerRecord);
		if (result.isCallBad()) return result;
		qifPackage.setAccountType(headerRecord.getAccountType());
		return result;
	}

	private QIFCallResult processRecord(QIFParser parser) {
		QIFCallResult processresult = new QIFCallResult();
		QIFRecord record = new QIFRecord();
		QIFCallResult processFieldResult = null;
		String field = null;
		do {
			try {
				field = parser.getField();
			} catch (Exception e) {
				processresult.setCallBad(e);
				return processresult;
			}
			processFieldResult = processField(field, record);
			if (processFieldResult.isCallBad()) return processFieldResult;
		} while (parser.isNotEOF() && parser.isNotQifEor());
		processresult.setReturnedObject(record);
		return processresult;
	}
			
	private QIFCallResult processField(String field, QIFRecord record){
		QIFCallResult processFieldResult = new QIFCallResult();
		lineNumber++;
		if (field.length() < 2) return processFieldResult;
		char firstChar = field.charAt(0);
		String fieldContent = field.substring(1, field.length());
		switch (firstChar){
			case 'A': 
				processFieldResult = HandleAddress.INSTANCE.handle(fieldContent, record);
				break;
			case 'C': 
				processFieldResult = HandleStatus.INSTANCE.handle(fieldContent, record);
				break;
			case 'D': 
				processFieldResult = HandleDate.INSTANCE.handle(fieldContent);
				if (processFieldResult.isCallBad()) {
					addLineNumberToMessage(processFieldResult, lineNumber);
					return processFieldResult;
				}
				record.setDate((LocalDate)processFieldResult.getReturnedObject());
				break;
			case 'E': 
				processFieldResult = HandleSplitCategoryMemo.INSTANCE.handle(fieldContent, record);
				break;
			case 'L': 
				QIFCategory singleCategory = new QIFCategory();
				singleCategory.setName(fieldContent);
				record.addCategory(singleCategory);
				break;
			case 'M': 
				record.setMemo(fieldContent);
				break;
			case 'N': 
				record.setNumber(fieldContent);
				break;
			case 'P': 
				record.setDescription(fieldContent);
				break;
			case 'S': 
				QIFCategory splitCategory = new QIFCategory();
				splitCategory.setName(fieldContent);
				record.addCategory(splitCategory);
				break;
			case '$': 
				processFieldResult = HandleSplitCategoryAmount.INSTANCE.handle(fieldContent, record);
				break;
			case 'T': 
				processFieldResult = HandleBigDecimal.INSTANCE.handle(fieldContent);
				if (processFieldResult.isCallBad()) {
					addLineNumberToMessage(processFieldResult, lineNumber);
					return processFieldResult;
				}
				record.setAmount((BigDecimal)processFieldResult.getReturnedObject());
				break;
			case '!': 
				processFieldResult = HandleAccountType.INSTANCE.handle(fieldContent);
				if (processFieldResult.isCallBad()) {
					addLineNumberToMessage(processFieldResult, lineNumber);
					return processFieldResult;
				}
				record.setAccountType((QIFAccountType)processFieldResult.getReturnedObject());
				break;
			default:
				processFieldResult.setCallBad(new ParseException("Invalid field type: " + firstChar + " found on line " + lineNumber + ".", 0));
		}
		return processFieldResult;
	}
	
	private QIFCallResult addLineNumberToMessage(QIFCallResult result, int lineNumber){
		if (result.getException().getMessage().endsWith("found ")){
			result.setCallBad(new ParseException(result.getException().getMessage() + "on line " + lineNumber + ".", 0));
		}
		return result;
	}

}
