package com.vitec.translation;

/**
 * Apuluokka käännöspromptien hallintaan.
 */
public class TranslationPrompt {
    private String prompt;
    private String domain;
    private String style;
    
    /**
     * Luo uuden käännöspromptin annetuilla parametreilla.
     * 
     * @param domain Toimiala (esim. "laki", "lääketiede", "tekniikka")
     * @param style Tyyli (esim. "muodollinen", "epämuodollinen", "tekninen")
     */
    public TranslationPrompt(String domain, String style) {
        this.domain = domain;
        this.style = style;
        buildPrompt();
    }
    
    /**
     * Rakentaa käännöspromptin annettujen parametrien perusteella.
     */
    private void buildPrompt() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("Käännä seuraava teksti huomioiden nämä ohjeet: ");
        
        if (domain != null && !domain.isEmpty()) {
            builder.append("Teksti liittyy toimialaan: ").append(domain).append(". ");
        }
        
        if (style != null && !style.isEmpty()) {
            builder.append("Käytä käännöksessä tyyliä: ").append(style).append(". ");
        }
        
        this.prompt = builder.toString();
    }
    
    /**
     * Palauttaa rakennetun promptin.
     */
    public String getPrompt() {
        return prompt;
    }
    
    /**
     * Lisää mukautetun ohjeistuksen promptiin.
     * 
     * @param instruction Lisättävä ohjeistus
     */
    public void addCustomInstruction(String instruction) {
        if (instruction != null && !instruction.isEmpty()) {
            this.prompt += instruction + " ";
        }
    }
    
    /**
     * Lisää erikoistermit promptiin.
     * 
     * @param terms Avain-arvo -parit termeistä (lähde -> käännös)
     */
    public void addSpecialTerms(java.util.Map<String, String> terms) {
        if (terms != null && !terms.isEmpty()) {
            StringBuilder termBuilder = new StringBuilder();
            termBuilder.append("Käytä näitä erikoistermejä: ");
            
            for (java.util.Map.Entry<String, String> entry : terms.entrySet()) {
                termBuilder.append(entry.getKey())
                          .append(" → ")
                          .append(entry.getValue())
                          .append(", ");
            }
            
            // Poistetaan viimeinen pilkku ja välilyönti
            String termString = termBuilder.substring(0, termBuilder.length() - 2);
            this.prompt += termString + ". ";
        }
    }
}