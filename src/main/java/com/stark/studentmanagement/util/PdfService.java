package com.stark.studentmanagement.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.stark.studentmanagement.entity.Exam;
import com.stark.studentmanagement.entity.Result;
import com.stark.studentmanagement.entity.User;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PdfService {

    public void generateAdmitCard(User user, List<Exam> exams, String filePath) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // 1. Centurion University Header
        addUniversityHeader(document, "Examination Hall Ticket");

        // 2. Student Details
        addStudentMetaData(document, user);

        // 3. Exam Timetable Table
        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.DARK_GRAY);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);

        PdfPTable table = new PdfPTable(new float[]{1f, 2f, 4.5f, 2.5f, 2.5f});
        table.setWidthPercentage(100);
        table.setSpacingBefore(15f);
        table.setSpacingAfter(15f);

        addTableHeader(table, new String[]{"SL.NO", "SUB.CODE", "SUBJECT", "DATE", "TIMINGS"}, tableHeaderFont);

        if (exams != null && !exams.isEmpty()) {
            int sl = 1;
            for (Exam e : exams) {
                String subCode = "CUTM" + (1000 + (e.getSubject() != null ? e.getSubject().getId() : e.getId()));
                String subName = e.getSubject() != null ? e.getSubject().getName().toUpperCase() : e.getTitle().toUpperCase();
                String dateStr = e.getExamDate() != null ? e.getExamDate().toString() : LocalDate.now().plusDays(sl * 2).toString();
                String timeStr = e.getStartTime() != null ? e.getStartTime().toString() : "10:00 AM";

                addTableCell(table, String.valueOf(sl++), cellFont, Element.ALIGN_CENTER);
                addTableCell(table, subCode, cellFont, Element.ALIGN_CENTER);
                addTableCell(table, subName, cellFont, Element.ALIGN_LEFT);
                addTableCell(table, dateStr, cellFont, Element.ALIGN_CENTER);
                addTableCell(table, timeStr, cellFont, Element.ALIGN_CENTER);
            }
        } else {
            // Default sample exam schedule
            String[][] sampleExams = {
                {"1", "CUTM1004", "DISCRETE MATHEMATICS", "2026-10-10", "10:00 AM - 1:00 PM"},
                {"2", "CUTM1018", "DATA ANALYSIS AND VISUALISATION USING PYTHON", "2026-10-12", "10:00 AM - 1:00 PM"},
                {"3", "CUTM1010", "ENVIRONMENTAL STUDIES", "2026-10-14", "10:00 AM - 1:00 PM"},
                {"4", "CUTM1046", "ELECTRONIC DEVICES AND SYSTEMS", "2026-10-16", "10:00 AM - 1:00 PM"},
                {"5", "CUTM1007", "OPTICS AND OPTICAL FIBRES", "2026-10-18", "10:00 AM - 1:00 PM"},
                {"6", "CUTM1014", "GENDER, HUMAN RIGHTS AND ETHICS", "2026-10-20", "10:00 AM - 1:00 PM"}
            };
            for (String[] row : sampleExams) {
                addTableCell(table, row[0], cellFont, Element.ALIGN_CENTER);
                addTableCell(table, row[1], cellFont, Element.ALIGN_CENTER);
                addTableCell(table, row[2], cellFont, Element.ALIGN_LEFT);
                addTableCell(table, row[3], cellFont, Element.ALIGN_CENTER);
                addTableCell(table, row[4], cellFont, Element.ALIGN_CENTER);
            }
        }
        document.add(table);

        // 4. Examination Instructions
        Font instHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
        Font instBody = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);
        document.add(new Paragraph("INSTRUCTIONS TO CANDIDATES:", instHeader));
        document.add(new Paragraph("1. Candidate must present this official Hall Ticket and valid University ID.", instBody));
        document.add(new Paragraph("2. Report 30 minutes before commencement of the examination.", instBody));
        document.add(new Paragraph("3. Electronic gadgets and smartwatches are strictly prohibited inside the hall.", instBody));
        document.add(new Paragraph("4. Compulsory webcam and AI proctoring will be active during online exams.", instBody));

        // 5. Signature Footer
        addFooterSignature(document);
        document.close();
    }

    public void generateGradeSheet(User user, List<Result> results, String filePath) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // 1. Centurion University Header
        addUniversityHeader(document, "Semester Grade Sheet");

        // 2. Student Details
        addStudentMetaData(document, user);

        // 3. Grade Table
        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.DARK_GRAY);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);

        PdfPTable table = new PdfPTable(new float[]{1f, 2f, 5f, 2.5f, 1.5f, 1.5f});
        table.setWidthPercentage(100);
        table.setSpacingBefore(15f);
        table.setSpacingAfter(15f);

        addTableHeader(table, new String[]{"SL.NO", "SUB.CODE", "SUBJECT", "TYPE", "CREDIT", "GRADE"}, tableHeaderFont);

        if (results != null && !results.isEmpty()) {
            int sl = 1;
            for (Result r : results) {
                String subCode = "CUTM" + (1000 + (r.getExam() != null && r.getExam().getSubject() != null ? r.getExam().getSubject().getId() : sl));
                String subName = r.getExam() != null ? r.getExam().getTitle().toUpperCase() : "COURSE EXAM";
                String type = "THEORY+PROJECT";
                String credit = "3";
                String grade = r.getScore() >= 8 ? "O" : (r.getScore() >= 6 ? "A" : "B");

                addTableCell(table, String.valueOf(sl++), cellFont, Element.ALIGN_CENTER);
                addTableCell(table, subCode, cellFont, Element.ALIGN_CENTER);
                addTableCell(table, subName, cellFont, Element.ALIGN_LEFT);
                addTableCell(table, type, cellFont, Element.ALIGN_CENTER);
                addTableCell(table, credit, cellFont, Element.ALIGN_CENTER);
                addTableCell(table, grade, cellFont, Element.ALIGN_CENTER);
            }
        } else {
            // Default sample grade rows matching uploaded document
            String[][] sampleGrades = {
                {"1", "CUTM1004", "DISCRETE MATHEMATICS", "THEORY+PROJECT", "3", "A"},
                {"2", "CUTM1018", "DATA ANALYSIS AND VISUALISATION USING PYTHON", "PP", "4", "O"},
                {"3", "CUTM1010", "ENVIRONMENTAL STUDIES", "PROJECT", "2", "E"},
                {"4", "CUTM1046", "ELECTRONIC DEVICES AND SYSTEMS", "BOTH", "3", "B"},
                {"5", "CUTM1007", "OPTICS AND OPTICAL FIBRES", "BOTH", "3", "A"},
                {"6", "CUTM1014", "GENDER, HUMAN RIGHTS AND ETHICS", "THEORY+PROJECT", "3", "A"}
            };
            for (String[] row : sampleGrades) {
                addTableCell(table, row[0], cellFont, Element.ALIGN_CENTER);
                addTableCell(table, row[1], cellFont, Element.ALIGN_CENTER);
                addTableCell(table, row[2], cellFont, Element.ALIGN_LEFT);
                addTableCell(table, row[3], cellFont, Element.ALIGN_CENTER);
                addTableCell(table, row[4], cellFont, Element.ALIGN_CENTER);
                addTableCell(table, row[5], cellFont, Element.ALIGN_CENTER);
            }
        }
        document.add(table);

        // 4. Credits & SGPA / CGPA Summary Bar
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
        PdfPTable summaryTable = new PdfPTable(4);
        summaryTable.setWidthPercentage(100);
        summaryTable.addCell(createBorderlessCell("Total Credits : 18", summaryFont));
        summaryTable.addCell(createBorderlessCell("Credits Cleared: 18", summaryFont));
        summaryTable.addCell(createBorderlessCell("SGPA : 8.15", summaryFont));
        summaryTable.addCell(createBorderlessCell("CGPA : 8.15", summaryFont));
        document.add(summaryTable);

        // 5. Signature Footer
        addFooterSignature(document);
        document.close();
    }

    public void generateFeeReceipt(User user, com.stark.studentmanagement.entity.Fee fee, String filePath) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        addUniversityHeader(document, "Official Fee Payment Receipt");
        addStudentMetaData(document, user);

        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(80);
        table.setSpacingBefore(15f);
        table.setSpacingAfter(15f);

        String totalStr = (fee != null && fee.getTotalAmount() != null) ? fee.getTotalAmount().toString() : "50,000.00";
        String paidStr = (fee != null && fee.getPaidAmount() != null) ? fee.getPaidAmount().toString() : "0.00";
        String dueStr = (fee != null) ? fee.getDueAmount().toString() : "50,000.00";
        String statusStr = (fee != null && fee.getStatus() != null) ? fee.getStatus().toString() : "PENDING";

        table.addCell(createCell("Total Academic Fee:", normalFont, true));
        table.addCell(createCell("Rs. " + totalStr, normalFont, false));

        table.addCell(createCell("Paid Amount:", normalFont, true));
        table.addCell(createCell("Rs. " + paidStr, normalFont, false));

        table.addCell(createCell("Due Amount:", normalFont, true));
        table.addCell(createCell("Rs. " + dueStr, normalFont, false));

        table.addCell(createCell("Payment Status:", normalFont, true));
        table.addCell(createCell(statusStr, normalFont, true));

        document.add(table);
        addFooterSignature(document);
        document.close();
    }

    private void addUniversityHeader(Document document, String titleText) throws Exception {
        Font mainHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new BaseColor(40, 40, 40));
        Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new BaseColor(80, 80, 80));
        Font stateFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new BaseColor(40, 40, 40));
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);

        Paragraph p1 = new Paragraph("Centurion University of Technology and Management", mainHeaderFont);
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(p1);

        Paragraph p2 = new Paragraph("School of Engineering & Technology ,Vizianagaram", subHeaderFont);
        p2.setAlignment(Element.ALIGN_CENTER);
        document.add(p2);

        Paragraph p3 = new Paragraph("ANDHRAPRADESH", stateFont);
        p3.setAlignment(Element.ALIGN_CENTER);
        document.add(p3);

        document.add(new Paragraph(" "));

        Paragraph p4 = new Paragraph(titleText, titleFont);
        p4.setAlignment(Element.ALIGN_CENTER);
        document.add(p4);

        document.add(new Paragraph(" "));
    }

    private void addStudentMetaData(Document document, User user) throws Exception {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);

        PdfPTable metaTable = new PdfPTable(2);
        metaTable.setWidthPercentage(100);
        metaTable.setSpacingAfter(10f);

        String regd = user.getErpId() != null ? user.getErpId() : (user.getUsername() != null ? user.getUsername() : "241801120002");
        String name = user.getFullName() != null ? user.getFullName().toUpperCase() : "KARRI SRICHAITANYA";
        String dept = user.getDepartment() != null ? user.getDepartment() : "Computer Science and Engineering";
        String sec = user.getSection() != null ? user.getSection() : "A";

        metaTable.addCell(createMetaCell("Student Regd. No : ", regd, labelFont, valueFont));
        metaTable.addCell(createMetaCell("Course : ", "Bachelor of Technology in " + dept, labelFont, valueFont));

        metaTable.addCell(createMetaCell("Student Name : ", name, labelFont, valueFont));
        metaTable.addCell(createMetaCell("Batch : ", "2024", labelFont, valueFont));

        metaTable.addCell(createMetaCell("Branch : ", dept, labelFont, valueFont));
        metaTable.addCell(createMetaCell("Semester : ", "Sem 1 (Section " + sec + ")", labelFont, valueFont));

        document.add(metaTable);
    }

    private PdfPCell createMetaCell(String label, String value, Font labelFont, Font valueFont) {
        Phrase p = new Phrase();
        p.add(new Chunk(label, labelFont));
        p.add(new Chunk(value, valueFont));
        PdfPCell cell = new PdfPCell(p);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(3);
        return cell;
    }

    private void addTableHeader(PdfPTable table, String[] headers, Font font) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, font));
            cell.setBackgroundColor(new BaseColor(245, 247, 250));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(6);
            table.addCell(cell);
        }
    }

    private void addTableCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addFooterSignature(Document document) throws Exception {
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        PdfPTable footerTable = new PdfPTable(2);
        footerTable.setWidthPercentage(100);

        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.DARK_GRAY);
        Font sigFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new BaseColor(15, 23, 42));

        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        PdfPCell leftCell = createBorderlessCell("Date : " + currentDate, dateFont);

        PdfPCell rightCell = createBorderlessCell("Gulum..\nDean, Examinations", sigFont);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        footerTable.addCell(leftCell);
        footerTable.addCell(rightCell);

        document.add(footerTable);
    }

    private PdfPCell createCell(String text, Font font, boolean isBold) {
        PdfPCell cell = new PdfPCell(new Phrase(text, isBold ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11) : font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell createBorderlessCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4);
        return cell;
    }
}
