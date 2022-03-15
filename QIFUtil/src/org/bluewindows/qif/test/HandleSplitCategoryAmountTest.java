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

import java.math.BigDecimal;

import org.bluewindows.qif.HandleSplitCategoryAmount;
import org.bluewindows.qif.QIFCallResult;
import org.bluewindows.qif.QIFCategory;
import org.bluewindows.qif.QIFRecord;

import junit.framework.TestCase;

public class HandleSplitCategoryAmountTest extends TestCase {
	
	public void testHandleGoodAmount() {
		QIFRecord record = new QIFRecord();
		QIFCategory category = new QIFCategory();
		record.addCategory(category);
		String content = "-123.45";
		QIFCallResult result = HandleSplitCategoryAmount.INSTANCE.handle(content, record);
		assertTrue(result.isCallOK());
		assertEquals(1, record.getCategories().size());
		assertEquals(new BigDecimal(content), record.getCategories().get(0).getAmount());
	}
	
	public void testHandleMissingAmount() {
		QIFRecord record = new QIFRecord();
		QIFCategory category = new QIFCategory();
		record.addCategory(category);
		String content = "";
		QIFCallResult result = HandleSplitCategoryAmount.INSTANCE.handle(content, record);
		assertTrue(result.isCallBad());
		assertEquals("Split category missing amount found ", result.getException().getMessage());
	}

}
