package com.dery.lecatro.util;

import java.awt.Color;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

@Component
public class PdfGenerator {

    // cores do sistema 
    private static final Color COLOR_DARK    = new Color(26, 35, 50);   // #1a2332
    private static final Color COLOR_ACCENT  = new Color(232, 160, 32); // #e8a020
    private static final Color COLOR_MUTED   = new Color(108, 117, 125);
    private static final Color COLOR_WHITE   = Color.WHITE;
    private static final Color COLOR_LIGHT   = new Color(244, 246, 249);

    // fontes
    private static final Font FONT_TITLE     = new Font(Font.HELVETICA, 18, Font.BOLD,   COLOR_DARK);
    private static final Font FONT_SUBTITLE  = new Font(Font.HELVETICA, 10, Font.NORMAL, COLOR_MUTED);
    private static final Font FONT_HEADER    = new Font(Font.HELVETICA, 9,  Font.BOLD,   COLOR_WHITE);
    private static final Font FONT_CELL      = new Font(Font.HELVETICA, 9,  Font.NORMAL, COLOR_DARK);
    private static final Font FONT_SECTION   = new Font(Font.HELVETICA, 10, Font.BOLD,   COLOR_DARK);
    private static final Font FONT_VALUE     = new Font(Font.HELVETICA, 10, Font.NORMAL, COLOR_DARK);
    private static final Font FONT_PLATE     = new Font(Font.HELVETICA, 28, Font.BOLD,   COLOR_DARK);
    private static final Font FONT_FOOTER    = new Font(Font.HELVETICA, 8,  Font.NORMAL, COLOR_MUTED);

    private static final DateTimeFormatter DATE_FORMAT     = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // =====================================================================
    // CABEÇALHO COMUM A TODOS OS DOCUMENTOS
    // =====================================================================

    private void addHeader(Document document, String title, String subtitle) throws DocumentException {
        // marca LECATRO
        Paragraph brand = new Paragraph("LECATRO", FONT_TITLE);
        brand.setAlignment(Element.ALIGN_LEFT);
        document.add(brand);

        // subtítulo do relatório
        Paragraph sub = new Paragraph(subtitle, FONT_SUBTITLE);
        sub.setAlignment(Element.ALIGN_LEFT);
        sub.setSpacingAfter(4);
        document.add(sub);

        // linha separadora em accent
		LineSeparator line = new LineSeparator(2, 100, COLOR_ACCENT, Element.ALIGN_LEFT, -2);
		document.add(new Chunk(line));

        // título do relatório
        Paragraph titleParagraph = new Paragraph(title, FONT_SECTION);
        titleParagraph.setSpacingBefore(12);
        titleParagraph.setSpacingAfter(4);
        document.add(titleParagraph);

        // data de geração
        Paragraph generated = new Paragraph(
            "Gerado em: " + LocalDateTime.now().format(DATETIME_FORMAT), FONT_FOOTER
        );
        generated.setSpacingAfter(16);
        document.add(generated);
    }

    // =====================================================================
    // RODAPÉ COMUM
    // =====================================================================

    private void addFooter(Document document, int total) throws DocumentException {
        Paragraph footer = new Paragraph(
            "Total de registos: " + total, FONT_FOOTER
        );
        footer.setSpacingBefore(12);
        footer.setAlignment(Element.ALIGN_RIGHT);
        document.add(footer);
    }

    // =====================================================================
    // CABEÇALHO DE TABELA
    // =====================================================================

    private PdfPCell headerCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_HEADER));
        cell.setBackgroundColor(COLOR_DARK);
        cell.setPadding(8);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    // =====================================================================
    // CÉLULA DE DADOS
    // =====================================================================

    private PdfPCell dataCell(String text, boolean shaded) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "—", FONT_CELL));
        cell.setBackgroundColor(shaded ? COLOR_LIGHT : COLOR_WHITE);
        cell.setPadding(7);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    // =====================================================================
    // PDF — PROPRIETÁRIOS
    // =====================================================================

    public void generateOwners(
        List<String[]> rows, // cada linha: [nome, nuit, email, dataNasc]
        OutputStream out
    ) throws Exception {
        Document document = new Document(PageSize.A4.rotate()); // paisagem
        PdfWriter.getInstance(document, out);
        document.open();

        addHeader(document, "Lista de Proprietários",
            "Sistema de Gestão e Emissão de Matrículas");

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 2f, 3f, 2f});

        // cabeçalho
        table.addCell(headerCell("Nome Completo"));
        table.addCell(headerCell("NUIT"));
        table.addCell(headerCell("Email"));
        table.addCell(headerCell("Data de Nascimento"));

        // dados
        boolean shaded = false;
        for (String[] row : rows) {
            table.addCell(dataCell(row[0], shaded));
            table.addCell(dataCell(row[1], shaded));
            table.addCell(dataCell(row[2], shaded));
            table.addCell(dataCell(row[3], shaded));
            shaded = !shaded;
        }

        document.add(table);
        addFooter(document, rows.size());
        document.close();
    }

    // =====================================================================
    // PDF — VEÍCULOS
    // =====================================================================

    public void generateVehicles(
        List<String[]> rows, // [marca, modelo, cor, chassis, ano]
        OutputStream out
    ) throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, out);
        document.open();

        addHeader(document, "Lista de Veículos",
            "Sistema de Gestão e Emissão de Matrículas");

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 2f, 1.5f, 3f, 1.5f});

        table.addCell(headerCell("Marca"));
        table.addCell(headerCell("Modelo"));
        table.addCell(headerCell("Cor"));
        table.addCell(headerCell("Nº Chassis"));
        table.addCell(headerCell("Ano"));

        boolean shaded = false;
        for (String[] row : rows) {
            table.addCell(dataCell(row[0], shaded));
            table.addCell(dataCell(row[1], shaded));
            table.addCell(dataCell(row[2], shaded));
            table.addCell(dataCell(row[3], shaded));
            table.addCell(dataCell(row[4], shaded));
            shaded = !shaded;
        }

        document.add(table);
        addFooter(document, rows.size());
        document.close();
    }

    // =====================================================================
    // PDF — PEDIDOS
    // =====================================================================

    public void generateRequests(
        List<String[]> rows, // [proprietário, veículo, operador, estado, data]
        String filterInfo,   // descrição dos filtros activos
        OutputStream out
    ) throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, out);
        document.open();

        addHeader(document, "Lista de Pedidos",
            "Sistema de Gestão e Emissão de Matrículas");

        // filtros activos
        if (filterInfo != null && !filterInfo.isBlank()) {
            Paragraph filters = new Paragraph("Filtros: " + filterInfo, FONT_FOOTER);
            filters.setSpacingAfter(8);
            document.add(filters);
        }

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.5f, 2.5f, 2.5f, 1.5f, 1.5f});

        table.addCell(headerCell("Proprietário"));
        table.addCell(headerCell("Veículo"));
        table.addCell(headerCell("Operador"));
        table.addCell(headerCell("Estado"));
        table.addCell(headerCell("Data"));

        boolean shaded = false;
        for (String[] row : rows) {
            table.addCell(dataCell(row[0], shaded));
            table.addCell(dataCell(row[1], shaded));
            table.addCell(dataCell(row[2], shaded));
            table.addCell(dataCell(row[3], shaded));
            table.addCell(dataCell(row[4], shaded));
            shaded = !shaded;
        }

        document.add(table);
        addFooter(document, rows.size());
        document.close();
    }

    // =====================================================================
    // PDF — MATRÍCULAS
    // =====================================================================

    public void generateLicensePlates(
        List<String[]> rows, // [número, proprietário, veículo, data emissão, estado]
        OutputStream out
    ) throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, out);
        document.open();

        addHeader(document, "Lista de Matrículas",
            "Sistema de Gestão e Emissão de Matrículas");

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 2.5f, 2.5f, 2f, 1.5f});

        table.addCell(headerCell("Número"));
        table.addCell(headerCell("Proprietário"));
        table.addCell(headerCell("Veículo"));
        table.addCell(headerCell("Data de Emissão"));
        table.addCell(headerCell("Estado"));

        boolean shaded = false;
        for (String[] row : rows) {
            table.addCell(dataCell(row[0], shaded));
            table.addCell(dataCell(row[1], shaded));
            table.addCell(dataCell(row[2], shaded));
            table.addCell(dataCell(row[3], shaded));
            table.addCell(dataCell(row[4], shaded));
            shaded = !shaded;
        }

        document.add(table);
        addFooter(document, rows.size());
        document.close();
    }

    // =====================================================================
    // PDF — HISTÓRICO DE UM PEDIDO
    // =====================================================================

    public void generateHistory(
        String requestInfo,  
        List<String[]> rows, 
        OutputStream out
    ) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        addHeader(document, "Histórico do Pedido",
            "Sistema de Gestão e Emissão de Matrículas");

        // informação do pedido
        Paragraph info = new Paragraph(requestInfo, FONT_VALUE);
        info.setSpacingAfter(12);
        document.add(info);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 4f, 2f});

        table.addCell(headerCell("Evento"));
        table.addCell(headerCell("Descrição"));
        table.addCell(headerCell("Data"));

        boolean shaded = false;
        for (String[] row : rows) {
            table.addCell(dataCell(row[0], shaded));
            table.addCell(dataCell(row[1], shaded));
            table.addCell(dataCell(row[2], shaded));
            shaded = !shaded;
        }

        document.add(table);
        addFooter(document, rows.size());
        document.close();
    }

    // =====================================================================
    // PDF — CERTIFICADO DE MATRÍCULA
    // =====================================================================

    public void generateCertificate(
        String plateNumber,   
        String issueDate,     
        String ownerName,    
        String ownerNuit,    
        String vehicleBrand, 
        String vehicleModel,  
        String vehicleChassis,
        String vehicleYear,  
        OutputStream out
    ) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();

        // ---- cabeçalho do certificado ----
        Paragraph brand = new Paragraph("LECATRO", FONT_TITLE);
        brand.setAlignment(Element.ALIGN_CENTER);
        document.add(brand);

        Paragraph systemName = new Paragraph(
            "Sistema de Gestão e Emissão de Matrículas", FONT_SUBTITLE
        );
        systemName.setAlignment(Element.ALIGN_CENTER);
        systemName.setSpacingAfter(8);
        document.add(systemName);

        LineSeparator line = new LineSeparator(2, 100, COLOR_ACCENT, Element.ALIGN_CENTER, -2);
        document.add(new Chunk(line));

        Paragraph certTitle = new Paragraph("CERTIFICADO DE MATRÍCULA", FONT_SECTION);
        certTitle.setAlignment(Element.ALIGN_CENTER);
        certTitle.setSpacingBefore(20);
        certTitle.setSpacingAfter(20);
        document.add(certTitle);

        // ---- número da matrícula em destaque ----
        PdfPTable plateBox = new PdfPTable(1);
        plateBox.setWidthPercentage(60);
        plateBox.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell plateCell = new PdfPCell();
        plateCell.setBackgroundColor(COLOR_DARK);
        plateCell.setPadding(20);
        plateCell.setBorder(Rectangle.NO_BORDER);
        plateCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph plateText = new Paragraph(plateNumber, FONT_PLATE);
        plateText.setAlignment(Element.ALIGN_CENTER);
        // cor do número em accent
        FONT_PLATE.setColor(COLOR_ACCENT);
        plateCell.addElement(plateText);
        plateBox.addCell(plateCell);

        document.add(plateBox);

        Paragraph issueDateParagraph = new Paragraph(
            "Data de Emissão: " + issueDate, FONT_SUBTITLE
        );
        issueDateParagraph.setAlignment(Element.ALIGN_CENTER);
        issueDateParagraph.setSpacingBefore(8);
        issueDateParagraph.setSpacingAfter(24);
        document.add(issueDateParagraph);

        // ---- dados do proprietário ----
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1f, 2f});
        infoTable.setSpacingBefore(8);

        // secção proprietário
        PdfPCell ownerHeader = new PdfPCell(new Phrase("PROPRIETÁRIO", FONT_HEADER));
        ownerHeader.setBackgroundColor(COLOR_DARK);
        ownerHeader.setPadding(8);
        ownerHeader.setBorder(Rectangle.NO_BORDER);
        ownerHeader.setColspan(2);
        infoTable.addCell(ownerHeader);

        infoTable.addCell(dataCell("Nome", true));
        infoTable.addCell(dataCell(ownerName, true));
        infoTable.addCell(dataCell("NUIT", false));
        infoTable.addCell(dataCell(ownerNuit, false));

        // espaço entre secções
        PdfPCell spacer = new PdfPCell(new Phrase(" "));
        spacer.setBorder(Rectangle.NO_BORDER);
        spacer.setColspan(2);
        spacer.setPadding(4);
        infoTable.addCell(spacer);

        // secção veículo
        PdfPCell vehicleHeader = new PdfPCell(new Phrase("VEÍCULO", FONT_HEADER));
        vehicleHeader.setBackgroundColor(COLOR_DARK);
        vehicleHeader.setPadding(8);
        vehicleHeader.setBorder(Rectangle.NO_BORDER);
        vehicleHeader.setColspan(2);
        infoTable.addCell(vehicleHeader);

        infoTable.addCell(dataCell("Marca / Modelo", true));
        infoTable.addCell(dataCell(vehicleBrand + " " + vehicleModel, true));
        infoTable.addCell(dataCell("Nº Chassis", false));
        infoTable.addCell(dataCell(vehicleChassis, false));
        infoTable.addCell(dataCell("Ano de Fabrico", true));
        infoTable.addCell(dataCell(vehicleYear, true));

        document.add(infoTable);

        // ---- rodapé do certificado ----
        Paragraph footer = new Paragraph(
            "Documento gerado electronicamente pelo sistema LECATRO em "
            + LocalDateTime.now().format(DATETIME_FORMAT),
            FONT_FOOTER
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(32);
        document.add(footer);

        document.close();
    }
}