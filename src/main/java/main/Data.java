package main;

import java.math.BigDecimal;

public class Data {

	private String error;
	private String type;
	private String id;
	private String cpf;
	private String cnpj;
	private String saleID;
	private String clientName;
	private String salesmanName;
	private String itensSold;
	private String salary;
	private String businessArea;
	private String salesmanOrderName;
	private BigDecimal totalSaleValue;

	public static final String SALESMAN_DATA = "SALESMAN_DATA";
	public static final String CUSTOMER_DATA = "CUSTOMER_DATA";
	public static final String SALES_DATA = "SALES_DATA";

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getSalesmanName() {
		return salesmanName;
	}

	public void setSalesmanName(String salesmanName) {
		this.salesmanName = salesmanName;
	}

	public String getItensSold() {
		return itensSold;
	}

	public void setItensSold(String itensSold) {
		this.itensSold = itensSold;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getBusinessArea() {
		return businessArea;
	}

	public void setBusinessArea(String businessArea) {
		this.businessArea = businessArea;
	}

	public BigDecimal getTotalSaleValue() {
		return totalSaleValue;
	}

	public void setTotalSaleValue(BigDecimal totalSaleValue) {
		this.totalSaleValue = totalSaleValue;
	}

	public String getSaleID() {
		return saleID;
	}

	public void setSaleID(String saleID) {
		this.saleID = saleID;
	}

	public String getSalesmanOrderName() {
		return salesmanOrderName;
	}

	public void setSalesmanOrderName(String salesmanOrderName) {
		this.salesmanOrderName = salesmanOrderName;
	}

}
