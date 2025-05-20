# Tekoälypohjainen kielenkääntäjä Javalla

Tämä projekti on Java-pohjainen komentorivisovellus, joka hyödyntää DeepL API:a käännösten tekemiseen. Sovellus tukee sekä perus käännöksiä että edistyneempiä käännösprompteja, joilla voi tarkentaa käännöksen toimialaa, tyyliä ja erikoistermistöä.

## Ominaisuudet

- Tekstin kääntäminen kielestä toiseen
- Mukautetut käännöspromptit toimialakontekstin, tyylin ja sävyn määrittelyyn
- Toimialakohtaisen terminologian määrittely käännösprosessiin
- Erikoistermien hallinta kieliparien välillä
- Tuettujen kielten listaus

## Vaatimukset

- Java 17 tai uudempi
- Maven
- DeepL API -avain
- IDE (suositus: VS Code Java-laajennuksilla)

## DeepL API-avaimen hankkiminen

Sovellus vaatii toimiakseen DeepL API-avaimen. Seuraa näitä ohjeita avaimen hankkimiseksi:

1. **Rekisteröityminen DeepL API -palveluun**:
   - Mene osoitteeseen https://www.deepl.com/pro-api
   - Valitse "Sign up for free"
   - Täytä rekisteröitymistiedot ja vahvista sähköposti

2. **Tilin luominen**:
   - Valitse "API Free" -suunnitelma, joka tarjoaa 500 000 merkkiä kuukaudessa ilmaiseksi
   - Vahvista rekisteröityminen vastaamalla sähköpostiisi tulleeseen viestiin

3. **API-avaimen noutaminen**:
   - Kirjaudu sisään DeepL-tiliisi
   - Siirry "Account" > "Authentication Key for DeepL API" -osioon
   - Kopioi API-avain (muodossa: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx:fx)

4. **Huomioitavaa**:
   - API-avain on henkilökohtainen - älä jaa sitä julkisesti
   - Free-tason käyttöraja on 500 000 merkkiä/kk
   - Voit tarkistaa käyttörajoituksesi DeepL-tilin hallintapaneelista

## Projektin asennus VS Codessa

### 1. Tarvittavien laajennusten asennus

1. Avaa VS Code
2. Asenna seuraavat laajennukset (Extensions-näkymästä):
   - Extension Pack for Java
   - Maven for Java
   - Project Manager for Java

### 2. Uuden Maven-projektin luominen

1. Avaa Command Palette (Ctrl+Shift+P)
2. Kirjoita: `Maven: Create Maven Project`
3. Valitse `maven-archetype-quickstart`
4. Määritä:
   - Group Id: `com.vitec`
   - Artifact Id: `translation-service`
   - Package: `com.vitec.translation`

### 3. POM-tiedoston päivittäminen

Korvaa `pom.xml`-tiedoston sisältö seuraavalla:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.vitec</groupId>
    <artifactId>translation-service</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- Jackson for JSON processing -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.15.2</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.6.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.vitec.translation.TranslationApp</mainClass>
                        </manifest>
                    </archive>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### 4. Java-tiedostojen luominen

Luo seuraavat tiedostot kansioon `src/main/java/com/vitec/translation/`:

1. **TranslationService.java** - DeepL API -integraatio
2. **TranslationPrompt.java** - Käännöspromptien hallinta
3. **TranslationApp.java** - Komentorivisovellus

## Projektin rakenne

```
translation-service/
│
├── pom.xml                      # Maven konfiguraatiotiedosto
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/            
│   │   │       └── vitec/      
│   │   │           └── translation/ 
│   │   │               ├── TranslationService.java
│   │   │               ├── TranslationPrompt.java
│   │   │               └── TranslationApp.java
│   │   │
│   │   └── resources/
│   │
│   └── test/
│
└── target/
    └── translation-service-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Sovelluksen ajaminen VS Codessa

1. **Suoraan IDE:stä**:
   - Avaa `TranslationApp.java`
   - Paina "Run" painiketta tiedoston yläreunassa tai paina F5

2. **Komentoriviltä**:
   - Avaa terminaali VS Codessa (Ctrl+`)
   - Suorita:
   ```bash
   mvn clean package
   java -jar target/translation-service-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

3. **Maven-laajennuksella**:
   - Avaa Maven-näkymä VS Codessa
   - Laajenna projekti → Plugins → exec
   - Kaksoisnapsauta "exec:java"

## Sovelluksen käyttö

1. Käynnistäessä sovellus pyytää DeepL API-avaimen
2. Päävalikosta voit valita:
   - **1. Käännä teksti** - Peruskäännös kielestä toiseen
   - **2. Käännä teksti mukautetulla promptilla** - Edistyneempi käännös toimialan ja tyylin määrittelyillä
   - **3. Näytä tuetut kielet** - Listaa DeepL:n tukemat kielet
   - **0. Lopeta** - Sulje sovellus

### Peruskäännöksen käyttö

1. Valitse valikosta kohta 1
2. Syötä lähdekieli (esim. FI, EN, SV)
3. Syötä kohdekieli (esim. FI, EN, SV)
4. Kirjoita käännettävä teksti
5. Paina Enter tyhjällä rivillä päättääksesi tekstin syötön

### Mukautetun käännöksen käyttö

1. Valitse valikosta kohta 2
2. Syötä lähdekieli ja kohdekieli
3. Anna toimiala (esim. laki, lääketiede, teknologia)
4. Määritä tyyli (esim. muodollinen, epämuodollinen, tekninen)
5. Voit lisätä erikoistermejä muodossa `lähde:käännös`
6. Voit lisätä mukautettuja ohjeita käännösprosessiin
7. Kirjoita käännettävä teksti

## Vianetsintä

1. **API-avain virheet**:
   - Tarkista että avain on oikein syötetty
   - Varmista että tilisi on aktivoitu
   - Tarkista käyttörajasi

2. **Java-versiovirheet**:
   - Varmista Java 17+ käyttö: `java -version`

3. **Maven-virheet**:
   - Päivitä Maven-riippuvuudet: `mvn clean install -U`

4. **VS Code -ongelmat**:
   - Päivitä Java-projektit: Command Palette → "Java: Refresh Projects"

---

*Tämä projekti on luotu osana Vitecin "Tekoälypohjainen luonnollisen kielen kääntäjä Javalla" -työpajaa.*