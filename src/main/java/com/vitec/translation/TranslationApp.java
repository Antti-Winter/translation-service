package com.vitec.translation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Komentorivisovellus DeepL-käännöspalvelun testaamiseen.
 */
public class TranslationApp {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static TranslationService translationService;
    
    public static void main(String[] args) {
        System.out.println("=== Tekoälypohjainen kääntäjä (DeepL) ===");
        
        try {
            // Pyydä API-avain käyttäjältä
            System.out.print("Anna DeepL API-avain: ");
            String apiKey = reader.readLine().trim();
            
            // Alusta käännöspalvelu
            translationService = new TranslationService(apiKey);
            System.out.println("Palvelu alustettu onnistuneesti!\n");
            
            // Näytä päävalikko
            showMainMenu();
            
        } catch (Exception e) {
            System.err.println("Virhe: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Näyttää päävalikon ja käsittelee käyttäjän valinnat.
     */
    private static void showMainMenu() throws IOException {
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\n--- Päävalikko ---");
            System.out.println("1. Käännä teksti");
            System.out.println("2. Käännä teksti mukautetulla promptilla");
            System.out.println("3. Näytä tuetut kielet");
            System.out.println("0. Lopeta");
            System.out.print("Valintasi: ");
            
            String choice = reader.readLine().trim();
            
            switch (choice) {
                case "1":
                    translateSimple();
                    break;
                case "2":
                    translateWithCustomPrompt();
                    break;
                case "3":
                    listSupportedLanguages();
                    break;
                case "0":
                    exit = true;
                    System.out.println("Kiitos käytöstä!");
                    break;
                default:
                    System.out.println("Virheellinen valinta, yritä uudelleen.");
            }
        }
    }
    
    /**
     * Suorittaa yksinkertaisen käännöksen.
     */
    private static void translateSimple() throws IOException {
        try {
            System.out.print("Lähdekieli (esim. FI, EN, SV): ");
            String sourceLanguage = reader.readLine().trim().toUpperCase();
            
            System.out.print("Kohdekieli (esim. FI, EN, SV): ");
            String targetLanguage = reader.readLine().trim().toUpperCase();
            
            System.out.println("Syötä käännettävä teksti (tyhjä rivi lopettaa):");
            StringBuilder textBuilder = new StringBuilder();
            String line;
            while (!(line = reader.readLine()).isEmpty()) {
                textBuilder.append(line).append("\n");
            }
            
            String text = textBuilder.toString().trim();
            if (text.isEmpty()) {
                System.out.println("Ei käännettävää tekstiä.");
                return;
            }
            
            System.out.println("\nKäännetään...");
            String translated = translationService.translate(text, sourceLanguage, targetLanguage);
            
            System.out.println("\n--- Käännös ---");
            System.out.println(translated);
            
        } catch (Exception e) {
            System.err.println("Käännösvirhe: " + e.getMessage());
        }
    }
    
    /**
     * Suorittaa käännöksen mukautetulla promptilla.
     */
    private static void translateWithCustomPrompt() throws IOException {
        try {
            System.out.print("Lähdekieli (esim. FI, EN, SV): ");
            String sourceLanguage = reader.readLine().trim().toUpperCase();
            
            System.out.print("Kohdekieli (esim. FI, EN, SV): ");
            String targetLanguage = reader.readLine().trim().toUpperCase();
            
            // Kerää promptin tiedot
            System.out.print("Toimiala (esim. laki, lääketiede, tekniikka): ");
            String domain = reader.readLine().trim();
            
            System.out.print("Tyyli (esim. muodollinen, epämuodollinen, tekninen): ");
            String style = reader.readLine().trim();
            
            TranslationPrompt prompt = new TranslationPrompt(domain, style);
            
            // Kysy erikoistermejä
            System.out.println("Haluatko lisätä erikoistermejä? (k/e): ");
            if (reader.readLine().trim().equalsIgnoreCase("k")) {
                Map<String, String> terms = collectSpecialTerms();
                prompt.addSpecialTerms(terms);
            }
            
            // Kysy mukautettua ohjeistusta
            System.out.println("Haluatko lisätä mukautettua ohjeistusta? (k/e): ");
            if (reader.readLine().trim().equalsIgnoreCase("k")) {
                System.out.print("Kirjoita mukautettu ohjeistus: ");
                String instruction = reader.readLine().trim();
                prompt.addCustomInstruction(instruction);
            }
            
            System.out.println("Syötä käännettävä teksti (tyhjä rivi lopettaa):");
            StringBuilder textBuilder = new StringBuilder();
            String line;
            while (!(line = reader.readLine()).isEmpty()) {
                textBuilder.append(line).append("\n");
            }
            
            String text = textBuilder.toString().trim();
            if (text.isEmpty()) {
                System.out.println("Ei käännettävää tekstiä.");
                return;
            }
            
            System.out.println("\nKäytetty prompt:");
            System.out.println(prompt.getPrompt());
            
            System.out.println("\nKäännetään...");
            String translated = translationService.translate(
                    text, sourceLanguage, targetLanguage, prompt.getPrompt());
            
            System.out.println("\n--- Käännös ---");
            System.out.println(translated);
            
        } catch (Exception e) {
            System.err.println("Käännösvirhe: " + e.getMessage());
        }
    }
    
    /**
     * Kerää erikoistermit käyttäjältä.
     */
    private static Map<String, String> collectSpecialTerms() throws IOException {
        Map<String, String> terms = new HashMap<>();
        
        System.out.println("Syötä erikoistermit muodossa 'lähde:käännös' (tyhjä rivi lopettaa):");
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            String[] parts = line.split(":");
            if (parts.length == 2) {
                terms.put(parts[0].trim(), parts[1].trim());
            } else {
                System.out.println("Virheellinen muoto, käytä muotoa 'lähde:käännös'");
            }
        }
        
        return terms;
    }
    
    /**
     * Näyttää tuetut kielet.
     */
    private static void listSupportedLanguages() {
        try {
            System.out.println("\nHaetaan tuettuja kieliä...");
            java.util.List<String> languages = translationService.getSupportedLanguages();
            
            System.out.println("\n--- Tuetut kielet ---");
            for (String language : languages) {
                System.out.println(language);
            }
            
        } catch (Exception e) {
            System.err.println("Virhe kielten haussa: " + e.getMessage());
        }
    }
}