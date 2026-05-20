package com.airline.service;

import com.airline.model.Reservation;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfGeneratorService {

    // Style Constants
    private static final BaseColor COLOR_NAVY = new BaseColor(10, 37, 64);      // #0a2540
    private static final BaseColor COLOR_CYAN = new BaseColor(0, 168, 204);     // #00a8cc
    private static final BaseColor COLOR_LIGHT_GRAY = new BaseColor(245, 247, 250); // #f5f7fa
    private static final BaseColor COLOR_BORDER_GRAY = new BaseColor(220, 224, 230); // #dce0e6
    private static final BaseColor COLOR_TEXT_DARK = new BaseColor(44, 62, 80);    // #2c3e50
    private static final BaseColor COLOR_MUTED = new BaseColor(127, 140, 141);    // #7f8c8d
    private static final BaseColor COLOR_SUCCESS_BG = new BaseColor(232, 245, 233); // #e8f5e9
    private static final BaseColor COLOR_SUCCESS_TXT = new BaseColor(46, 125, 50);  // #2e7d32

    private final Font fontTitleWhite = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.WHITE);
    private final Font fontPassBlue = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, COLOR_CYAN);
    
    private final Font fontLabel = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, COLOR_MUTED);
    private final Font fontValueBold = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, COLOR_TEXT_DARK);
    private final Font fontPnrBold = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, COLOR_CYAN);
    
    private final Font fontRouteBig = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, COLOR_NAVY);
    private final Font fontStatus = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, COLOR_SUCCESS_TXT);
    private final Font fontFooter = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, COLOR_MUTED);

    public byte[] generateBoardingPassPdf(Reservation res) {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Root wrapper card table
            PdfPTable rootTable = new PdfPTable(1);
            rootTable.setWidthPercentage(100);
            rootTable.setSpacingAfter(0);

            // 1. BRANDING HEADER
            PdfPCell headerCell = createHeaderCell();
            rootTable.addCell(headerCell);

            // 2. PASSENGER & REFERENCE SECTION
            PdfPCell passengerCell = createPassengerSection(res);
            rootTable.addCell(passengerCell);

            // 3. FLIGHT ROUTE TIMETABLE
            PdfPCell routeCell = createRouteSection(res);
            rootTable.addCell(routeCell);

            // 4. VECTOR QR CODE CARD
            PdfPCell qrCell = createQrSection(res);
            rootTable.addCell(qrCell);

            // 5. REGULATION TICKET FOOTER
            PdfPCell footerCell = createFooterSection();
            rootTable.addCell(footerCell);

            document.add(rootTable);
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    private PdfPCell createHeaderCell() {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        try {
            headerTable.setWidths(new float[]{60, 40});
        } catch (DocumentException ignored) {}

        PdfPCell leftCell = new PdfPCell(new Phrase("✈  FLYHIGH AIRLINES", fontTitleWhite));
        leftCell.setBackgroundColor(COLOR_NAVY);
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setPadding(18);
        leftCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(leftCell);

        PdfPCell rightCell = new PdfPCell(new Phrase("BOARDING PASS", fontPassBlue));
        rightCell.setBackgroundColor(COLOR_NAVY);
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        rightCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        rightCell.setPaddingRight(18);
        headerTable.addCell(rightCell);

        PdfPCell cell = new PdfPCell(headerTable);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(COLOR_NAVY);
        cell.setBorderWidth(1.5f);
        cell.setPadding(0);
        return cell;
    }

    private PdfPCell createPassengerSection(Reservation res) {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{40, 30, 30});
        } catch (DocumentException ignored) {}

        // Passenger Name
        PdfPCell nameCell = new PdfPCell();
        nameCell.setBorder(Rectangle.NO_BORDER);
        nameCell.setPadding(12);
        nameCell.addElement(new Paragraph("PASSENGER NAME", fontLabel));
        nameCell.addElement(new Paragraph(res.getPassengerName().toUpperCase(), fontValueBold));
        table.addCell(nameCell);

        // Status Badge styled cell
        PdfPCell statusCell = new PdfPCell();
        statusCell.setBorder(Rectangle.NO_BORDER);
        statusCell.setPadding(12);
        statusCell.addElement(new Paragraph("STATUS", fontLabel));
        
        PdfPTable badge = new PdfPTable(1);
        badge.setWidthPercentage(80);
        badge.setHorizontalAlignment(Element.ALIGN_LEFT);
        PdfPCell badgeCell = new PdfPCell(new Paragraph("CONFIRMED", fontStatus));
        badgeCell.setBackgroundColor(COLOR_SUCCESS_BG);
        badgeCell.setBorderColor(COLOR_SUCCESS_TXT);
        badgeCell.setBorderWidth(0.5f);
        badgeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        badgeCell.setPadding(3);
        badge.addCell(badgeCell);
        
        statusCell.addElement(badge);
        table.addCell(statusCell);

        // PNR Code
        PdfPCell pnrCell = new PdfPCell();
        pnrCell.setBorder(Rectangle.NO_BORDER);
        pnrCell.setPadding(12);
        pnrCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        pnrCell.addElement(new Paragraph("BOOKING REF / PNR", fontLabel));
        Paragraph pnrP = new Paragraph(res.getPnr().toUpperCase(), fontPnrBold);
        pnrP.setAlignment(Element.ALIGN_LEFT);
        pnrCell.addElement(pnrP);
        table.addCell(pnrCell);

        PdfPCell cell = new PdfPCell(table);
        cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        cell.setBorderColor(COLOR_NAVY);
        cell.setBorderWidth(1.5f);
        cell.setPadding(8);
        return cell;
    }

    private PdfPCell createRouteSection(Reservation res) {
        PdfPTable outerTable = new PdfPTable(1);
        outerTable.setWidthPercentage(100);

        PdfPCell cardCell = new PdfPCell();
        cardCell.setBackgroundColor(COLOR_LIGHT_GRAY);
        cardCell.setBorder(Rectangle.BOX);
        cardCell.setBorderColor(COLOR_BORDER_GRAY);
        cardCell.setBorderWidth(1f);
        cardCell.setPadding(15);

        // Flight coordinates header
        Paragraph routePara = new Paragraph(res.getSource().toUpperCase() + "  ➔  " + res.getDestination().toUpperCase(), fontRouteBig);
        routePara.setSpacingAfter(10);
        cardCell.addElement(routePara);

        // Route details grid
        PdfPTable grid = new PdfPTable(4);
        grid.setWidthPercentage(100);
        try {
            grid.setWidths(new float[]{20, 40, 20, 20});
        } catch (DocumentException ignored) {}

        // Flight No
        PdfPCell fCell = new PdfPCell();
        fCell.setBorder(Rectangle.NO_BORDER);
        fCell.setBackgroundColor(COLOR_LIGHT_GRAY);
        fCell.addElement(new Paragraph("FLIGHT", fontLabel));
        fCell.addElement(new Paragraph(res.getFlightNumber(), fontValueBold));
        grid.addCell(fCell);

        // Departure Time formatting
        String depTimeStr = "";
        if (res.getDepartureTime() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            depTimeStr = sdf.format(res.getDepartureTime());
        }
        
        PdfPCell dCell = new PdfPCell();
        dCell.setBorder(Rectangle.NO_BORDER);
        dCell.setBackgroundColor(COLOR_LIGHT_GRAY);
        dCell.addElement(new Paragraph("DEPARTURE TIME", fontLabel));
        dCell.addElement(new Paragraph(depTimeStr, fontValueBold));
        grid.addCell(dCell);

        // Seat
        PdfPCell sCell = new PdfPCell();
        sCell.setBorder(Rectangle.NO_BORDER);
        sCell.setBackgroundColor(COLOR_LIGHT_GRAY);
        sCell.addElement(new Paragraph("SEATS", fontLabel));
        String seatsVal = (res.getSeats() != null && !res.getSeats().isEmpty()) ? res.getSeats() : (res.getSeatsBooked() + " Allocated");
        sCell.addElement(new Paragraph(seatsVal, fontValueBold));
        grid.addCell(sCell);

        // Gate placeholder
        PdfPCell gCell = new PdfPCell();
        gCell.setBorder(Rectangle.NO_BORDER);
        gCell.setBackgroundColor(COLOR_LIGHT_GRAY);
        gCell.addElement(new Paragraph("GATE", fontLabel));
        gCell.addElement(new Paragraph("GATE 12A", fontValueBold));
        grid.addCell(gCell);

        cardCell.addElement(grid);
        outerTable.addCell(cardCell);

        PdfPCell cell = new PdfPCell(outerTable);
        cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        cell.setBorderColor(COLOR_NAVY);
        cell.setBorderWidth(1.5f);
        cell.setPadding(15);
        return cell;
    }

    private PdfPCell createQrSection(Reservation res) {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        // Centered QR Code box
        PdfPCell qrContainer = new PdfPCell();
        qrContainer.setBorder(Rectangle.NO_BORDER);
        qrContainer.setHorizontalAlignment(Element.ALIGN_CENTER);

        try {
            // High-fidelity local vector QR generator
            String seatLabel = (res.getSeats() != null && !res.getSeats().isEmpty()) ? res.getSeats() : String.valueOf(res.getSeatsBooked());
            String qrData = String.format("PNR:%s\nPassenger:%s\nFlight:%s\nSeats:%s", 
                    res.getPnr(), res.getPassengerName(), res.getFlightNumber(), seatLabel);
            BarcodeQRCode qrCode = new BarcodeQRCode(qrData, 120, 120, null);
            Image qrImage = qrCode.getImage();
            qrImage.setAlignment(Element.ALIGN_CENTER);
            qrContainer.addElement(qrImage);
        } catch (Exception e) {
            qrContainer.addElement(new Paragraph("[QR Barcode Generation Error]"));
        }

        Paragraph warnPara = new Paragraph("GATE AGENT SCAN ONLY — PRESENT DIGITAL OR PRINTED PASS", fontLabel);
        warnPara.setAlignment(Element.ALIGN_CENTER);
        warnPara.setSpacingBefore(5);
        qrContainer.addElement(warnPara);

        table.addCell(qrContainer);

        PdfPCell cell = new PdfPCell(table);
        cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        cell.setBorderColor(COLOR_NAVY);
        cell.setBorderWidth(1.5f);
        cell.setPadding(12);
        return cell;
    }

    private PdfPCell createFooterSection() {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        PdfPCell textCell = new PdfPCell();
        textCell.setBackgroundColor(COLOR_LIGHT_GRAY);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setPadding(10);
        textCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph footerPara = new Paragraph("Thank you for choosing FlyHigh Airlines • Have a pleasant journey!\n© 2026 FlyHigh Airlines | Developed by Vamsi Ukkusuri", fontFooter);
        footerPara.setAlignment(Element.ALIGN_CENTER);
        textCell.addElement(footerPara);
        table.addCell(textCell);

        PdfPCell cell = new PdfPCell(table);
        cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
        cell.setBorderColor(COLOR_NAVY);
        cell.setBorderWidth(1.5f);
        cell.setPadding(0);
        return cell;
    }
}
