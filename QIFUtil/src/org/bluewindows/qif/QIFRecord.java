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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class QIFRecord {
	
	private QIFAccountType accountType;
	private LocalDate date;
	private BigDecimal amount;
	private List<String> addresses = new ArrayList<String>();
	private String optionalMessage;
	private String description = "";
	private String memo = "";
	private String number = "";
	private List<QIFCategory> categories = new ArrayList<QIFCategory>();
	private String status = "";
	
	public QIFAccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(QIFAccountType accountType) {
		this.accountType = accountType;
	}

	public LocalDate getDate() {
		return date;
	}
	
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public List<String> getAddresses() {
		return addresses;
	}

	public void addAddressLine(String addressLine) {
		if (addresses.size() < 5) {
			addresses.add(addressLine);
		}else {
			setOptionalMessage(addressLine);
		}
	}
	
	public String getOptionalMessage() {
		return optionalMessage;
	}

	public void setOptionalMessage(String optionalMessage) {
		this.optionalMessage = optionalMessage;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getMemo() {
		return memo;
	}
	
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public void addCategory(QIFCategory category) {
		categories.add(category);
	}
	
	public List<QIFCategory> getCategories() {
		return categories;
	}
	
	public void setCleared() {
		status = "X";
	}
	
	public void setReconciled() {
		status = "*";
	}
	
	public String getStatus() {
		return status;
	}
	
	public boolean equals(QIFRecord otherRecord){
		return EqualsBuilder.reflectionEquals(this, otherRecord);
	}


}
