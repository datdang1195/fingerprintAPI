package com.ekino.team2.punctuality.service;

import com.ekino.team2.punctuality.entity.Attendance;
import com.ekino.team2.punctuality.entity.Employee;
import com.ekino.team2.punctuality.entity.User;
import com.ekino.team2.punctuality.entity.UserStatus;
import com.ekino.team2.punctuality.model.RangeStatus;
import com.ekino.team2.punctuality.repository.AttendanceRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class DailyReport {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StatusService statusService;

    @Autowired
    private MyMailSender mymailSender;

    private static Logger logger = LoggerFactory.getLogger(DailyReport.class);

    public void fingerPrintDaily(LocalDateTime localDateTime) {
        LocalDate localDate = localDateTime.toLocalDate();
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String fileName = dtf.format(localDate);
            fileName = "DailySendmailReport-"+fileName.replace("-", "");
            if (localDateTime.getHour() <= 12)
                fileName += "-M";
            else
                fileName += "-A";

            List<Attendance> attendances = attendanceRepository.findAttendanceByRecordTimeBetweenOrderByDeviceUserId(
                    localDate.atTime(05, 59), localDate.atTime(23, 59));
            if (!attendances.isEmpty()) {
                List<UserStatus> userStatusList = statusService.createStatusWithOneDate(attendances);
                userStatusList.sort(Comparator.comparing(UserStatus::getUserName));
                writeDataDaily(userStatusList, fileName);
                mymailSender.sendEmailWithAttachment(fileName);
            }
        }
    }

    public void writeDataDaily(List<UserStatus> lstUserStatus, String fileName) {

        try (FileInputStream file = new FileInputStream(new File("templateDaily.xlsx"));
             Workbook workbook = new XSSFWorkbook(file)
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = 1;
            int no = 1;
            for (UserStatus userStatus : lstUserStatus) {
                Row row = sheet.createRow(rowNumber++);
                row.createCell(0).setCellValue(no);
                row.createCell(1).setCellValue(userStatus.getUserCode());
                row.createCell(2).setCellValue(userStatus.getUserName());
                row.createCell(3).setCellValue(userStatus.getRecordDate().toString());
                row.createCell(4).setCellValue(userStatus.getWorkingDay());
                row.createCell(5).setCellValue(userStatus.getStatus());
                row.createCell(6).setCellValue(userStatus.getDescription());
                no++;
            }

            String fileLocation = "excelMailDaily/" + fileName + ".xlsx";
            File fileO = new File(fileLocation);
            FileOutputStream outputStream = new FileOutputStream(fileO);
            workbook.write(outputStream);

            outputStream.close();

            logger.info("Create file excel daily success :{}", fileName);
        } catch (IOException e) {
            logger.error("Create file excel daily :{} fail :", fileName, e);
        }
    }

    public ByteArrayInputStream exportDailyExcel(List<UserStatus> lstUserStatus, String fileName) {

        try (FileInputStream file = new FileInputStream(new File("templateDaily.xlsx"));
             Workbook workbook = new XSSFWorkbook(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream();
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = 1;
            int no = 1;
            for (UserStatus userStatus : lstUserStatus) {
                Row row = sheet.createRow(rowNumber++);
                row.createCell(0).setCellValue(no);
                row.createCell(1).setCellValue(userStatus.getUserCode());
                row.createCell(2).setCellValue(userStatus.getUserName());
                row.createCell(3).setCellValue(userStatus.getRecordDate().toString());
                row.createCell(4).setCellValue(userStatus.getWorkingDay());
                row.createCell(5).setCellValue(userStatus.getStatus());
                row.createCell(6).setCellValue(userStatus.getDescription());
                no++;
            }


            String fileLocation = "excelDaily/" + fileName + ".xlsx";
            File fileO = new File(fileLocation);
            FileOutputStream outputStream = new FileOutputStream(fileO);
            workbook.write(out);
            outputStream.close();

            logger.info("Create file excel daily success :{}", fileName);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            logger.error("Create file excel daily :{} fail :", fileName, e);
        }
        return null;
    }

    public ByteArrayInputStream exportRangeExcel(List<RangeStatus> rangeStatusList, String fileName) {

        try (FileInputStream file = new FileInputStream(new File("templateRange.xlsx"));
             Workbook workbook = new XSSFWorkbook(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream();
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = 1;
            int no = 1;
            for (RangeStatus rangeStatus : rangeStatusList) {
                Row row = sheet.createRow(rowNumber++);
                row.createCell(0).setCellValue(no);
                row.createCell(1).setCellValue(rangeStatus.getUserCode());
                row.createCell(2).setCellValue(rangeStatus.getUserName());
                row.createCell(3).setCellValue(rangeStatus.getWorkingDay());
                row.createCell(4).setCellValue(rangeStatus.getOffDay());
                row.createCell(5).setCellValue(rangeStatus.getLateSession());
                row.createCell(6).setCellValue(rangeStatus.getAbsentSession());
                no++;
            }


            String fileLocation = "excelRange/" + fileName + ".xlsx";
            File fileO = new File(fileLocation);
            FileOutputStream outputStream = new FileOutputStream(fileO);
            workbook.write(out);
            outputStream.close();

            logger.info("Create file excel daily success :{}", fileName);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            logger.error("Create file excel daily :{} fail :", fileName, e);
        }
        return null;
    }

    public ByteArrayInputStream exportEmployee(List<Employee> employeeList, String fileName) {

        try (FileInputStream file = new FileInputStream(new File("templateEmployee.xlsx"));
             Workbook workbook = new XSSFWorkbook(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream();
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = 1;
            int no = 1;
            for (Employee e : employeeList) {
                User user = e.getUser();
                Row row = sheet.createRow(rowNumber++);
                row.createCell(0).setCellValue(no);
                row.createCell(1).setCellValue(user.getDeviceUserId());
                row.createCell(2).setCellValue(user.getName());
                row.createCell(3).setCellValue(e.isActive() ? "true" : "false");
                no++;
            }

            String fileLocation = "excelEmployee/" + fileName + ".xlsx";
            File fileO = new File(fileLocation);
            FileOutputStream outputStream = new FileOutputStream(fileO);
            workbook.write(out);
            outputStream.close();

            logger.info("Create file excel daily success :{}", fileName);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            logger.error("Create file excel daily :{} fail :", fileName, e);
        }
        return null;
    }
}
