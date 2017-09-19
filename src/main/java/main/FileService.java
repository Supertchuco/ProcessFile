package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import exception.BuildFileInDataException;
import exception.CreateOutputFileException;

@Service
public class FileService {

	public static final String input_path = "C:\\tempTest";
	public static final String out_path = "C:\\tempTest\\";
	private static final String out_path_file_name = "flat_Client_Report.done.dat";
	private Map<String, String> cnpjClients = new HashMap<String, String>();
	private Map<String, String> cpfSalesman = new HashMap<String, String>();

	public List<String> readFile(String fileName, String path) {
		List<String> fileLines = new ArrayList<String>();
		// read file into stream, try-with-resources
		try (Stream<String> stream = Files.lines(Paths.get(path + fileName))) {
			// stream.forEach(System.out::println);
			stream.forEach(fileLines::add);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileLines;
	}

	public List<String> readFile(File file) throws FileNotFoundException, IOException {
		List<String> fileLines = new ArrayList<String>();
		// read file into stream, try-with-resources

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				fileLines.add(line);
			}
		}

		return fileLines;
	}

	public void deleteFile(String fileName, String path) {
		try {
			Files.delete(Paths.get(path + fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Data> buildData(List<String> fileLines) throws BuildFileInDataException {
		List<Data> dataListReturn = new ArrayList<Data>();
		Data dataReturn = null;

		try {
			for (String fileLine : fileLines) {
				String[] data = fileLine.split("ç");
				if (data.length == 4) {
					dataReturn = new Data();
					dataReturn.setId(data[0]);

					if (StringUtils.equals(data[0], "001")) {
						dataReturn.setType(Data.SALESMAN_DATA);
						dataReturn.setCpf(data[1]);
						dataReturn.setSalesmanName(data[2]);
						dataReturn.setSalary(data[3]);
						if (!cpfSalesman.containsKey(dataReturn.getCpf())) {
							cpfSalesman.put(dataReturn.getCpf(), dataReturn.getSalesmanName());
						}
					} else if (StringUtils.equals(data[0], "002")) {
						dataReturn.setType(Data.CUSTOMER_DATA);
						dataReturn.setCnpj(data[1]);
						dataReturn.setClientName(data[2]);
						dataReturn.setBusinessArea(data[3]);
						if (!cnpjClients.containsKey(dataReturn.getCnpj())) {
							cnpjClients.put(dataReturn.getCnpj(), dataReturn.getClientName());
						}
					} else if (StringUtils.equals(data[0], "003")) {
						dataReturn.setType(Data.SALES_DATA);
						dataReturn.setSaleID(data[1]);
						dataReturn.setSalesmanOrderName(data[3]);
						dataReturn.setItensSold(data[2]);
						dataReturn.setTotalSaleValue(calculateTotalSale(data[2]));
					} else {
						dataReturn.setError("error to set cpf or cpnj");
					}
				} else {
					dataReturn = new Data();
					dataReturn.setError("error the data size is wrong");
				}
				dataListReturn.add(dataReturn);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildFileInDataException();
		}
		return dataListReturn;
	}

	public BigDecimal calculateTotalSale(String soldItens) {
		String[] itens = StringUtils.split(StringUtils.substringBetween(soldItens, "[", "]"), ",");
		BigDecimal total = BigDecimal.ZERO;
		int quantity = 0;
		String[] sale;
		BigDecimal custItem = null;
		for (String item : itens) {
			sale = item.split("-");
			custItem = new BigDecimal(sale[2].replaceAll(",", ""));
			quantity = Integer.parseInt(sale[1]);
			total = total.add(calculateCost(quantity, custItem));
		}
		return total;
	}

	public BigDecimal calculateCost(int itemQuantity, BigDecimal itemPrice) {
		BigDecimal itemCost = BigDecimal.ZERO;
		BigDecimal totalCost = BigDecimal.ZERO;
		itemCost = itemPrice.multiply(new BigDecimal(itemQuantity));
		totalCost = totalCost.add(itemCost);
		return totalCost;
	}

	public void createFileOutput(DataOutputFile dataOutput) throws CreateOutputFileException {
		try {
			PrintWriter writer = new PrintWriter(out_path + out_path_file_name, "UTF-8");
			writer.println("Amount of clients in the input file: " + dataOutput.getTotalClients());
			writer.println("Amount of salesman in the input file: " + dataOutput.getTotalSalesMan());
			writer.println("ID of the most expensive sale: " + dataOutput.getMostExpensiveSaleID());
			writer.println("Most expensive sale value: " + dataOutput.getMostExpensiveSaleValue());
			writer.println("Worst salesman: " + dataOutput.getWorstSalesman());
			writer.println("Worst salesman value: " + dataOutput.getWorstSalesmanValue());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new CreateOutputFileException();
		}
	}

	public DataOutputFile buildOutputFile(List<String> fileLines) {
		DataOutputFile dataOutputFile = new DataOutputFile();
		dataOutputFile.setTotalClients(Integer.parseInt(StringUtils.substringAfter(fileLines.get(0), ":").trim()));
		dataOutputFile.setTotalSalesMan(Integer.parseInt(StringUtils.substringAfter(fileLines.get(1), ":").trim()));
		dataOutputFile.setMostExpensiveSaleID(StringUtils.substringAfter(fileLines.get(2), ":").trim());
		dataOutputFile.setMostExpensiveSaleValue(
				new BigDecimal(StringUtils.substringAfter(fileLines.get(3), ":").trim().replaceAll(",", "")));
		dataOutputFile.setWorstSalesman(StringUtils.substringAfter(fileLines.get(4), ":").trim());
		dataOutputFile.setWorstSalesmanValue(
				new BigDecimal(StringUtils.substringAfter(fileLines.get(5), ":").trim().replaceAll(",", "")));
		return dataOutputFile;
	}

	public DataOutputFile buildOutputFileData(final List<Data> dataListInfo, final String fileName) {
		int res;
		String worstSalesman = null;
		BigDecimal worstSalesmanValue = BigDecimal.ZERO;
		String mostExpensiveSaleID = null;
		BigDecimal mostExpensiveSaleValue = BigDecimal.ZERO;

		for (int index = 0; index < dataListInfo.size(); index++) {

			if (StringUtils.equals(dataListInfo.get(index).getType(), Data.SALES_DATA)) {
				try {
					res = mostExpensiveSaleValue.compareTo(dataListInfo.get(index).getTotalSaleValue());
					if (res == -1) {
						mostExpensiveSaleValue = dataListInfo.get(index).getTotalSaleValue();
						mostExpensiveSaleID = dataListInfo.get(index).getSaleID();
					} else {
						if (mostExpensiveSaleValue.compareTo(BigDecimal.ZERO) == 0) {
							worstSalesman = dataListInfo.get(index).getSalesmanName();
							worstSalesmanValue = dataListInfo.get(index).getTotalSaleValue();
						} else {
							worstSalesman = dataListInfo.get(index).getSalesmanOrderName();
							worstSalesmanValue = dataListInfo.get(index).getTotalSaleValue();
						}
					}
				} catch (Exception e) {
					System.out.println("Error to process line: [" + index + "] in file name: [" + fileName + "]");
				}
			}
		}

		DataOutputFile dataOutputFile = new DataOutputFile();
		dataOutputFile.setTotalClients(cnpjClients.size());
		dataOutputFile.setTotalSalesMan(cpfSalesman.size());
		dataOutputFile.setMostExpensiveSaleID(mostExpensiveSaleID);
		dataOutputFile.setMostExpensiveSaleValue(mostExpensiveSaleValue);
		dataOutputFile.setWorstSalesman(worstSalesman);
		dataOutputFile.setWorstSalesmanValue(worstSalesmanValue);

		return dataOutputFile;
	}

	public DataOutputFile updateOutputFile(DataOutputFile newDataOutput, DataOutputFile oldDataOutput) {
		DataOutputFile dataOutputFileReturn = new DataOutputFile();

		dataOutputFileReturn.setTotalClients(cnpjClients.size());
		dataOutputFileReturn.setTotalSalesMan(cpfSalesman.size());

		int res = oldDataOutput.getMostExpensiveSaleValue().compareTo(newDataOutput.getMostExpensiveSaleValue());
		if (res == -1) {
			dataOutputFileReturn.setMostExpensiveSaleID(newDataOutput.getMostExpensiveSaleID());
			dataOutputFileReturn.setMostExpensiveSaleValue(newDataOutput.getMostExpensiveSaleValue());
		} else {
			dataOutputFileReturn.setMostExpensiveSaleID(oldDataOutput.getMostExpensiveSaleID());
			dataOutputFileReturn.setMostExpensiveSaleValue(oldDataOutput.getMostExpensiveSaleValue());
		}
		res = oldDataOutput.getWorstSalesmanValue().compareTo(newDataOutput.getWorstSalesmanValue());
		if (res == -1) {
			dataOutputFileReturn.setWorstSalesman(newDataOutput.getWorstSalesman());
			dataOutputFileReturn.setWorstSalesmanValue(newDataOutput.getWorstSalesmanValue());
		} else {
			dataOutputFileReturn.setWorstSalesman(oldDataOutput.getWorstSalesman());
			dataOutputFileReturn.setWorstSalesmanValue(oldDataOutput.getWorstSalesmanValue());
		}
		return dataOutputFileReturn;
	}

	public void processFile(File file) {
		try {
			DataOutputFile currentDataOut = null;
			List<Data> dataList = buildData(readFile(file));

			currentDataOut = buildOutputFileData(dataList, file.getName());

			if (Files.exists(Paths.get(out_path + out_path_file_name),
					new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {
				DataOutputFile outputFileOld = buildOutputFile(readFile(out_path_file_name, out_path));
				deleteFile(out_path_file_name, out_path);
				createFileOutput(updateOutputFile(currentDataOut, outputFileOld));
			} else {
				createFileOutput(currentDataOut);
			}

		} catch (BuildFileInDataException e) {
			System.out.println("File Error");
		} catch (CreateOutputFileException e) {
			System.out.println("Create Output File Error");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
