package io.letstry.prof.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.letstry.prof.utils.ObfuscatorUtil;

@Service
public class ProfApiService {

	private Workbook workbook = null;
	private Sheet sheet = null;
	private Map<Integer, String> columnHeaderIndex = new HashMap<>();
	private int rowIndex = 0;

	@Autowired
	private ObfuscatorUtil obfuscatorUtil;

	public Map<Integer, String> uploadService(MultipartFile multipartFile, boolean isHeaderPresent, int rowIndex)
			throws IOException {
		this.rowIndex = rowIndex;
		workbook = new XSSFWorkbook(multipartFile.getInputStream());
		sheet = workbook.getSheetAt(0);
		if (isHeaderPresent)
			sheet.getRow(rowIndex).forEach(
					cell -> columnHeaderIndex.put(cell.getColumnIndex(), cell.getRichStringCellValue().getString()));
		return columnHeaderIndex;
	}

	public ByteArrayInputStream maskSelectedColumns(MultipartFile file, boolean isHeaderPresent, int rowIndex,
			Map<Integer, String> selectedColumnHeaders) throws IOException {

		if (workbook == null) {
			uploadService(file, isHeaderPresent, rowIndex);
		}

		if (!selectedColumnHeaders.isEmpty()) {
			columnHeaderIndex = selectedColumnHeaders;
		}
		sheet.forEach(row -> {
			if (row.getRowNum() > rowIndex)
				columnHeaderIndex.forEach((index, name) -> {
					Cell cell = row.getCell(index);
					cell = cell != null ? maskvalue(cell) : null;// call maskString, maskNumeric
				});
		});

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		}
	}

	private Cell maskvalue(Cell cell) {

		switch (cell.getCellType()) {
		case STRING:
			cell.setCellValue(obfuscatorUtil.getMaskedValue(cell.getRichStringCellValue().getString()));
			break;
		case NUMERIC:
			String string1 = cell.getRichStringCellValue().toString();
			break;
		case BOOLEAN:
			break;
		case FORMULA:
			break;
		default:
		}

		return cell;
	}

	public void clearAll() {
		workbook = null;
		sheet = null;
	}
}