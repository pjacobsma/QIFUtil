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

public class QIFCallResult {
	
	private boolean callOK = true;
	private Exception exception;
	private Object returnedObject;

	public boolean isCallOK() {
		return callOK;
	}
	
	public boolean isCallBad() {
		return (! callOK);
	}
	
	public void setCallOK() {
		exception = null;
		callOK = true;
	}
	
	public QIFCallResult setCallBad(Exception exception) {
		callOK = false;
		this.exception = exception;
		return this;
	}
	
	public Exception getException() {
		return exception;
	}

	public Object getReturnedObject() {
		return returnedObject;
	}

	public void setReturnedObject(Object returnedObject) {
		this.returnedObject = returnedObject;
	}
	
}
