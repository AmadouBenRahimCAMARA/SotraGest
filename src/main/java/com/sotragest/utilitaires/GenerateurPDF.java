package com.sotragest.utilitaires;



import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.colors.ColorConstants;

import com.sotragest.modele.Ticket;
import com.sotragest.modele.Utilisateur;

import java.io.File;
import java.time.format.DateTimeFormatter;

public class GenerateurPDF {

    public static void genererTicketPDF(Ticket ticket, Utilisateur utilisateur) throws Exception {
        // Créer le répertoire tickets s'il n'existe pas
        File repertoireTickets = new File("tickets");
        if (!repertoireTickets.exists()) {
            repertoireTickets.mkdirs();
        }

        String nomFichier = "tickets/ticket_" + ticket.getNumeroTicket() + ".pdf";

        PdfWriter writer = new PdfWriter(nomFichier);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            // Police
            PdfFont font = PdfFontFactory.createFont();
            String compagnie = utilisateur.getNomCompagnie();
            // En-tête
            Paragraph titre = new Paragraph(compagnie)
                .setFont(font)
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.BLUE);
            document.add(titre);

            Paragraph sousTitre = new Paragraph("Ticket de Transport")
                .setFont(font)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
            document.add(sousTitre);

            // Informations du ticket
            Table table = new Table(2);
            table.setWidth(400);

            table.addCell("Numéro de ticket:");
            table.addCell(ticket.getNumeroTicket() != null ? ticket.getNumeroTicket() : "Inconnu");

            table.addCell("Voyageur:");
            table.addCell(ticket.getVoyageur() != null ? ticket.getVoyageur().getNomCompletVoyageur() : "Inconnu");

            table.addCell("Téléphone:");
            table.addCell(ticket.getVoyageur() != null ? ticket.getVoyageur().getTelephoneVoyageur() : "Inconnu");

            table.addCell("Trajet:");
            table.addCell(ticket.getTrajet() != null ? ticket.getTrajet().getItineraire() : "Inconnu");

            table.addCell("Date de départ:");
            table.addCell(ticket.getTrajet() != null && ticket.getTrajet().getDateHeureDepart() != null
                ? ticket.getTrajet().getDateHeureDepart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))
                : "Inconnue");

            table.addCell("Bus:");
            table.addCell(ticket.getTrajet() != null && ticket.getTrajet().getBus() != null
                ? ticket.getTrajet().getBus().getDescriptionComplete()
                : "Inconnu");

            table.addCell("Chauffeur:");
            table.addCell(ticket.getTrajet() != null && ticket.getTrajet().getChauffeur() != null
                ? ticket.getTrajet().getChauffeur().getNomCompletChauffeur()
                : "Inconnu");

            table.addCell("Siège:");
            table.addCell(String.valueOf(ticket.getNumeroSiege()));

            table.addCell("Prix:");
            table.addCell(String.format("%.0f FCFA", ticket.getPrix()));

            table.addCell("Date de vente:");
            table.addCell(ticket.getDateVente() != null
                ? ticket.getDateVente().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))
                : "Inconnue");

            table.addCell("Vendeur:");
            table.addCell(ticket.getVendeur() != null ? ticket.getVendeur().getNomCompletUtilisateur() : "Inconnu");

            document.add(table);

            // Code QR (simulé)
            Paragraph codeQR = new Paragraph("\nCode QR: " + 
                (ticket.getCodeQR() != null ? ticket.getCodeQR() : "Aucun"))
                .setFont(font)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20);
            document.add(codeQR);

            // Observations
            if (ticket.getObservationsTicket() != null && !ticket.getObservationsTicket().trim().isEmpty()) {
                Paragraph observations = new Paragraph("\nObservations: " + ticket.getObservationsTicket())
                    .setFont(font)
                    .setFontSize(10)
                    .setMarginTop(10);
                document.add(observations);
            }

            // Pied de page
            Paragraph piedPage = new Paragraph("\nMerci de voyager avec notre compagnie!\nConservez ce ticket pendant tout le voyage.")
                .setFont(font)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30)
                .setFontColor(ColorConstants.GRAY);
            document.add(piedPage);

        } finally {
            document.close();
        }

        System.out.println("Ticket PDF généré: " + nomFichier);

        // Ouvrir le PDF automatiquement (optionnel)
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "\"\"", nomFichier});
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", nomFichier});
            } else if (os.contains("nux") || os.contains("nix")) {
                Runtime.getRuntime().exec(new String[]{"xdg-open", nomFichier});
            }
        } catch (Exception e) {
            System.out.println("Impossible d'ouvrir automatiquement le PDF: " + e.getMessage());
        }
    }
}
