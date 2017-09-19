package main;

import java.math.BigDecimal;

public class DataOutputFile {

	private int totalClients;
	private int totalSalesMan;
	private String mostExpensiveSaleID;
	private BigDecimal mostExpensiveSaleValue;
	private String worstSalesman;
	private BigDecimal worstSalesmanValue;
	private String fileName;

	public int getTotalClients() {
		return totalClients;
	}

	public void setTotalClients(int totalClients) {
		this.totalClients = totalClients;
	}

	public int getTotalSalesMan() {
		return totalSalesMan;
	}

	public void setTotalSalesMan(int totalSalesMan) {
		this.totalSalesMan = totalSalesMan;
	}

	public String getMostExpensiveSaleID() {
		return mostExpensiveSaleID;
	}

	public void setMostExpensiveSaleID(String mostExpensiveSaleID) {
		this.mostExpensiveSaleID = mostExpensiveSaleID;
	}

	public String getWorstSalesman() {
		return worstSalesman;
	}

	public void setWorstSalesman(String worstSalesman) {
		this.worstSalesman = worstSalesman;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public BigDecimal getMostExpensiveSaleValue() {
		return mostExpensiveSaleValue;
	}

	public void setMostExpensiveSaleValue(BigDecimal mostExpensiveSaleValue) {
		this.mostExpensiveSaleValue = mostExpensiveSaleValue;
	}

	public BigDecimal getWorstSalesmanValue() {
		return worstSalesmanValue;
	}

	public void setWorstSalesmanValue(BigDecimal worstSalesmanValue) {
		this.worstSalesmanValue = worstSalesmanValue;
	}

}
