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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MockInputStream extends InputStream {
	int bytePos;
	ArrayList<Integer> byteArray = new ArrayList<Integer>();
	
	public MockInputStream(String fileContents){
		for (int i = 0; i < fileContents.length(); i++) {
			byteArray.add(Integer.valueOf(fileContents.charAt(i)));
		}
		bytePos = -1;
		byteArray.add(Integer.valueOf(-1));
	}
	
	@Override
	public int read() throws IOException {
		return byteArray.get(++bytePos).intValue();
	}
	
	@Override
	public void reset(){
		bytePos = -1;
	}
}
