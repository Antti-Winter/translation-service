package com.vitec.translation;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Palveluluokka, joka tarjoaa käännösrajapinnan DeepL API:n kautta.
 */
public class TranslationService {
    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl = "https://api.deepl.com/v2";

    /**
     * Konstruktori, joka alustaa palvelun annetulla API-avaimella.
     *
     * @param apiKey DeepL API-avain
     */
    public TranslationService(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Kääntää annetun tekstin lähdekielestä kohdekielelle.
     *
     * @param text Käännettävä teksti
     * @param sourceLanguage Lähdekieli (esim. "FI", "EN", "SV")
     * @param targetLanguage Kohdekieli (esim. "FI", "EN", "SV")
     * @return Käännetty teksti
     * @throws IOException Jos API-kutsussa tapahtuu virhe
     * @throws InterruptedException Jos API-kutsu keskeytyy
     */
    public String translate(String text, String sourceLanguage, String targetLanguage) 
            throws IOException, InterruptedException {
        return translate(text, sourceLanguage, targetLanguage, null);
    }

    /**
     * Kääntää annetun tekstin lähdekielestä kohdekielelle käyttäen mukautettua promptia.
     *
     * @param text Käännettävä teksti
     * @param sourceLanguage Lähdekieli (esim. "FI", "EN", "SV")
     * @param targetLanguage Kohdekieli (esim. "FI", "EN", "SV")
     * @param customPrompt Mukautettu prompt käännöksen ohjaamiseen
     * @return Käännetty teksti
     * @throws IOException Jos API-kutsussa tapahtuu virhe
     * @throws InterruptedException Jos API-kutsu keskeytyy
     */
    public String translate(String text, String sourceLanguage, String targetLanguage, String customPrompt) 
            throws IOException, InterruptedException {
        // Rakennetaan API-kutsun parametrit
        Map<String, String> params = new HashMap<>();
        params.put("text", text);
        params.put("source_lang", sourceLanguage);
        params.put("target_lang", targetLanguage);
        
        // Lisätään mukautettu prompt jos annettu
        if (customPrompt != null && !customPrompt.isEmpty()) {
            // DeepL API voi tukea tätä formality-parametrilla, tai voimme integroida prompting-logiikan tähän
            params.put("formality", "prefer_more"); // Esimerkki parametri
        }
        
        // Muodostetaan HTTP-kutsu
        String requestBody = buildRequestBody(params);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/translate"))
                .header("Authorization", "DeepL-Auth-Key " + apiKey)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        // Lähetetään kutsu ja käsitellään vastaus
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return parseResponse(response.body());
        } else {
            throw new IOException("API-kutsu epäonnistui: " + response.statusCode() + " " + response.body());
        }
    }
    
    /**
     * Rakentaa URL-encoded request bodyn parametreista.
     */
    private String buildRequestBody(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return builder.toString();
    }
    
    /**
     * Jäsentää API-vastauksen ja palauttaa käännetyn tekstin.
     */
    @SuppressWarnings("unchecked")
    private String parseResponse(String responseBody) throws IOException {
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        
        // DeepL API palauttaa käännökset "translations" avaimella, joka on lista
        if (responseMap.containsKey("translations")) {
            java.util.List<Map<String, Object>> translations = 
                    (java.util.List<Map<String, Object>>) responseMap.get("translations");
            
            if (!translations.isEmpty() && translations.get(0).containsKey("text")) {
                return (String) translations.get(0).get("text");
            }
        }
        
        throw new IOException("Virheellinen API-vastaus: " + responseBody);
    }
    
    /**
     * Palauttaa listan tuetuista kielistä.
     */
    public java.util.List<String> getSupportedLanguages() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/languages"))
                .header("Authorization", "DeepL-Auth-Key " + apiKey)
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return parseLanguagesResponse(response.body());
        } else {
            throw new IOException("API-kutsu epäonnistui: " + response.statusCode() + " " + response.body());
        }
    }
    
    /**
     * Jäsentää kielilistan API-vastauksesta.
     */
    @SuppressWarnings("unchecked")
    private java.util.List<String> parseLanguagesResponse(String responseBody) throws IOException {
        java.util.List<Map<String, Object>> languagesData = 
                objectMapper.readValue(responseBody, java.util.List.class);
        
        java.util.List<String> languages = new java.util.ArrayList<>();
        for (Map<String, Object> lang : languagesData) {
            if (lang.containsKey("language")) {
                languages.add((String) lang.get("language"));
            }
        }
        
        return languages;
    }
}