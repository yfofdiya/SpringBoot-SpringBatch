package com.practice.helper;

import com.practice.entity.Person;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelHelper {

    public static boolean checkExcelFormat(MultipartFile file) {
        if (file.getContentType() != null) {
            return file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        }
        return false;
    }

    public static List<Person> convertExcelToList(InputStream is) throws IOException {
        List<Person> personList = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(is);

        XSSFSheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            Iterator<Cell> cellIterator = row.iterator();
            int cId = 0;
            Person person = new Person();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (cId) {
                    case 0:
                        person.setId((int) cell.getNumericCellValue());
                        break;
                    case 1:
                        person.setFirstName(cell.getStringCellValue());
                        break;
                    case 2:
                        person.setLastName(cell.getStringCellValue());
                        break;
                    case 3:
                        person.setEmail(cell.getStringCellValue());
                        break;
                    case 4:
                        person.setGender(cell.getStringCellValue());
                        break;
                }
                cId++;
            }
            personList.add(person);
        }
        workbook.close();
        is.close();
        return personList;

    }
}

