package sqlharjoitustyö;

import java.time.*;
import java.sql.Connection;
import java.sql.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.Scanner;


public class SQLHarjoitustyö {

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:tietokanta.db");
            Statement stmnt = connection.createStatement();

            Scanner lukija = new Scanner(System.in);
            System.out.println("Alla on lueteltu ohjelman toiminnot ja toimintoja vastaavat numeronäppäimet. Valitse toiminto painamalla sitä vastaavaa numeronäppäintä.");
            System.out.println("1: Luo tietokannan.");
            System.out.println("2: Lisää paikan.");
            System.out.println("3: Lisää asiakkaan.");
            System.out.println("4: Lisää paketin.");
            System.out.println("5: Lisää tapahtuman. Tapahtuman ajankohdaksi tulee automaattisesti tapahtuman lisäämishetki.");
            System.out.println("6: Hakee paketin tapahtumat paketin seurantakoodin avulla.");
            System.out.println("7: Hakee asiakkaan paketit ja paketteihin liittyvien tapahtumien määrän asiakkaan nimen avulla.");
            System.out.println("8: Hakee paikan tapahtumien määrän tiettynä päivänä paikan nimen avulla.");
            System.out.println("9: Suorittaa tietokannan tehokkuustestin.");
            Boolean jatkuuko = true;
            while (jatkuuko) {
                System.out.print("Valitse toiminto (1 - 9): ");

                int nappain = Integer.valueOf(lukija.nextLine());

                switch (nappain) {
                    case 0:
                        jatkuuko = false;
                        break;
                    case 1:
                        luoTietokanta(connection, stmnt);
                        break;
                    case 2:
                        lisaaPaikka(connection, lukija);
                        break;

                    case 3:
                        lisaaAsiakas(connection, lukija);
                        break;
                    case 4:
                        lisaaPaketti(connection, lukija);
                        break;
                    case 5:
                        lisaaTapahtuma(connection, lukija);
                        break;
                    case 6:
                        haePaketinTapahtumat(connection, lukija);
                        break;
                    case 7:
                        haeAsiakkaanPaketitJaNiihinLiittyvatTapahtumat(connection, lukija);
                        break;
                    case 8:
                        haePaikanTapahtumat(connection, lukija);
                        break;
                    case 9:
                        suoritaTehokkuustesti(connection, stmnt, lukija);
                        break;

                    default:
                        System.out.println("Virheellinen näppäinvalinta. Yritä uudestaan.");

                }

            }
        } catch (SQLException e) {
            System.out.println("VIRHE: Yhteyden muodostaminen tietokantaan epäonnistui.");
        }

    }

    private static void luoTietokanta(Connection connection, Statement stmnt) {
        try {
            connection.setAutoCommit(false);
            stmnt.execute("CREATE TABLE Asiakkaat (id INTEGER PRIMARY KEY, nimi TEXT UNIQUE)");
            stmnt.execute("CREATE TABLE Paikat (id INTEGER PRIMARY KEY, nimi TEXT UNIQUE)");
            stmnt.execute("CREATE TABLE Paketit (id INTEGER PRIMARY KEY, koodi TEXT UNIQUE, asiakas_id INTEGER)");
            stmnt.execute("CREATE TABLE Tapahtumat (paketti_id INTEGER, paikka_id INTEGER, kuvaus TEXT, paiva TEXT, aika TEXT)");
            //luodaan sopivat indeksit tehokkuustestiä varten:
            stmnt.execute("CREATE INDEX idx_nimi ON Asiakkaat (nimi)");
            stmnt.execute("CREATE INDEX idx_koodi ON Paketit (koodi)");
            stmnt.execute("CREATE INDEX idx_pNimi ON Paikat (nimi)");
            stmnt.execute("CREATE INDEX idx_tapahtumat_paketti_id ON Tapahtumat (paketti_id)");
            connection.commit();
            connection.setAutoCommit(true);
            System.out.println("Tietokanta luotu.");
        } catch (SQLException e) {
            System.out.println("Tietokannan luominen epäonnistui, tai tietokanta on jo luotu.");
        }
    }

    private static void lisaaPaikka(Connection connection, Scanner lukija) {
        try {
            System.out.print("Anna paikan nimi: ");
            PreparedStatement prepared = connection.prepareStatement("INSERT INTO Paikat (nimi) VALUES (?)");
            String nimi = lukija.nextLine();
            prepared.setString(1, nimi);
            prepared.executeUpdate(); //tässä tarkistaa, onko koodi UNIQUE --> jos ei ole tulee Exception
            System.out.println("Paikka lisätty.");
        } catch (SQLException e) {
            System.out.println("VIRHE: Paikan lisääminen epäonnistui, tai paikka on jo lisätty tietokantaan.");
        }
    }

    private static void lisaaAsiakas(Connection connection, Scanner lukija) {
        try {
            System.out.print("Anna asiakkaan nimi: ");
            PreparedStatement prepared = connection.prepareStatement("INSERT INTO Asiakkaat (nimi) VALUES (?)");
            String nimi = lukija.nextLine();
            prepared.setString(1, nimi);
            prepared.executeUpdate(); 
            System.out.println("Asiakas lisätty.");
        } catch (SQLException e) {
            System.out.println("VIRHE: Asiakkaan lisääminen epäonnistui, tai asiakas on jo lisätty tietokantaan.");
        }
    }

    private static void lisaaPaketti(Connection connection, Scanner lukija) {
        try {
            System.out.print("Anna paketin seurantakoodi: ");
            String koodi = lukija.nextLine();
            System.out.print("Anna asiakkaan nimi: ");
            String nimi = lukija.nextLine();
            PreparedStatement prepared = connection.prepareStatement("INSERT INTO Paketit (koodi, asiakas_id) VALUES (?, ?)");
            String SQLKomentoHaeId = "SELECT id FROM Asiakkaat WHERE nimi = ?";
            int asiakasId = haeId(nimi, connection, SQLKomentoHaeId);
            if (asiakasId < 0) {
                System.out.println("Asiakasta ei ole lisätty tietokantaan. Lisää asiakas tietokantaan painamalla näppäintä 3.");
                return;
            }
            prepared.setString(1, koodi);
            prepared.setInt(2, asiakasId);
            prepared.executeUpdate();
            System.out.println("Paketti lisätty.");

        } catch (SQLException e) {
            System.out.println("VIRHE: Paketin lisääminen epäonnistui, tai paketti on jo lisätty tietokantaan.");
        }
    }

    private static void lisaaTapahtuma(Connection connection, Scanner lukija) {
        try {
            System.out.print("Anna paketin seurantakoodi: ");
            String koodi = lukija.nextLine();
            String SQLKomentoHaeId = "SELECT id FROM Paketit WHERE koodi = ?";
            int pakettiId = haeId(koodi, connection, SQLKomentoHaeId);
            if (pakettiId < 0) {
                System.out.println("Pakettia ei ole lisätty tietokantaan. Lisää paketti tietokantaan painamalla näppäintä 4.");
                return;
            }

            System.out.print("Anna tapahtuman paikka: ");
            String paikka = lukija.nextLine();
            SQLKomentoHaeId = "SELECT id FROM Paikat WHERE nimi = ?";
            int paikkaId = haeId(paikka, connection, SQLKomentoHaeId);
            if (paikkaId < 1) {
                System.out.println("Paikkaa ei ole lisätty tietokantaan. Lisää paikka tietokantaan painamalla näppäintä 2.");
                return;
            }

            System.out.print("Anna tapahtuman kuvaus: ");
            String kuvaus = lukija.nextLine();

            LocalDate localDate = LocalDate.now(); //2020-02-08
            String paiva = localDate.toString();

            LocalTime localTime = LocalTime.now(); //18:41:43.632
            String aika = localTime.toString();

            PreparedStatement prepared = connection.prepareStatement("INSERT INTO Tapahtumat (paketti_id, paikka_id, kuvaus, paiva, aika) VALUES (?, ?, ?, ?, ?)");

            prepared.setInt(1, pakettiId);
            prepared.setInt(2, paikkaId);
            prepared.setString(3, kuvaus);
            prepared.setString(4, paiva);
            prepared.setString(5, aika);

            prepared.executeUpdate();
            System.out.println("Tapahtuma lisätty.");

        } catch (SQLException e) {
            System.out.println("VIRHE: Tapahtuman lisääminen epäonnistui.");
        }
    }

    private static void haePaketinTapahtumat(Connection connection, Scanner lukija) { 
        try {
            System.out.print("Anna paketin seurantakoodi: ");
            String koodi = lukija.nextLine();
            //tähän voisi lisätä paketin olemassaolon tarkistuksen; antaisi jo tässä virheilmoituksen, jos pakettia ei ole lisätty
            PreparedStatement prepared = connection.prepareStatement("SELECT Tapahtumat.paiva, Tapahtumat.aika, Paikat.nimi, Tapahtumat.kuvaus FROM Paketit LEFT JOIN Tapahtumat ON Paketit.id = Tapahtumat.paketti_id LEFT JOIN Paikat ON Paikat.id = Tapahtumat.paikka_id WHERE Paketit.koodi = ?");
            prepared.setString(1, koodi);
            ResultSet result = prepared.executeQuery();
            //NULLia, jos paketille ei ole lisätty tapahtumia -->:
            result.getString("kuvaus");
            if (result.wasNull()) {
                System.out.println("Ei tapahtumia.");
            } else {
                while (result.next()) {
                    System.out.println(result.getString("paiva") + " " + result.getString("aika") + ", " + result.getString("nimi") + ", " + result.getString("kuvaus"));
                }
            }

        } catch (SQLException e) {
            System.out.println("VIRHE: Tietojen hakeminen epäonnistui. Tarkista paketin seurantakoodi.");
        }
    }

    private static void haeAsiakkaanPaketitJaNiihinLiittyvatTapahtumat(Connection connection, Scanner lukija) { 
        try {
            System.out.print("Anna asiakkaan nimi: ");
            String nimi = lukija.nextLine();
            //tähän voisi lisätä asiakkaan olemassaolon tarkistuksen; antaisi virheilmoituksen, jos asiakasta ei ole lisätty
            PreparedStatement prepared = connection.prepareStatement("SELECT Paketit.koodi, COUNT(Tapahtumat.kuvaus) AS maara FROM  Asiakkaat LEFT JOIN Paketit ON Asiakkaat.id = Paketit.asiakas_id LEFT JOIN Tapahtumat ON Paketit.id = Tapahtumat.paketti_id WHERE Asiakkaat.nimi = ? GROUP BY Paketit.koodi");
            prepared.setString(1, nimi);
            ResultSet result = prepared.executeQuery();
            result.getString("koodi");
            if (result.wasNull()) { //Jos ei tätä, "koodista" NULL
                System.out.println("Asiakkaalle ei ole lisätty paketteja.");
            } else {
                while (result.next()) {
                    System.out.println(result.getString("koodi") + ": " + result.getInt("maara") + " tapahtumaa");
                }
            }

        } catch (SQLException e) {
            System.out.println("VIRHE: Tietojen hakeminen epäonnistui. Tarkista asiakkaan nimi.");
        }
    }

    private static void haePaikanTapahtumat(Connection connection, Scanner lukija) {
        try {
            System.out.print("Anna paikan nimi: ");
            String nimi = lukija.nextLine();

            String SQLKomentoHaeId = "SELECT id FROM Paikat WHERE nimi = ?";
            int paikkaId = haeId(nimi, connection, SQLKomentoHaeId);
            if (paikkaId < 1) {
                System.out.println("Paikkaa ei ole tietokannassa.");
                return;
            }

            System.out.print("Anna päivämäärä muodossa vvvv-kk-pp: ");
            String paiva = lukija.nextLine();

            PreparedStatement prepared = connection.prepareStatement("SELECT COUNT(Tapahtumat.kuvaus) AS maara FROM Paikat LEFT JOIN Tapahtumat ON Tapahtumat.paikka_id = Paikat.id WHERE Paikat.nimi = ? AND Tapahtumat.paiva = ?");
            prepared.setString(1, nimi);
            prepared.setString(2, paiva);
            ResultSet result = prepared.executeQuery();

            while (result.next()) {
                System.out.println("Tapahtumia paikassa " + nimi + " " + paiva + ": " + result.getInt("maara"));
            }

        } catch (SQLException e) {
            System.out.println("VIRHE: Tietojen hakeminen epäonnistui.");
        }

    }

    private static int haeId(String syote, Connection connection, String SQLKomento) {
        try {
            PreparedStatement prepared = connection.prepareStatement(SQLKomento);
            prepared.setString(1, syote);

            ResultSet result = prepared.executeQuery();

            return result.getInt("id");

        } catch (SQLException e) {
            return -1;
        }
    }

    private static void suoritaTehokkuustesti(Connection connection, Statement stmnt, Scanner lukija) {
        Random arpoja = new Random();
        try {

            luoTietokanta(connection, stmnt);

            connection.setAutoCommit(false);

            long aika1 = System.nanoTime();
            for (int i = 1; i < 1001; i++) { // lisätään tuhat paikkaa
                String paikka = "P" + Integer.toString(i);

                stmnt.executeUpdate("INSERT INTO Paikat (nimi) VALUES ('" + paikka + "')");
            }
            long aika2 = System.nanoTime();
            System.out.println("Tehokkuustestin vaihe 1 (paikkojen lisääminen): aikaa kului " + (aika2 - aika1) / 1e9 + " sekuntia.");

            long aika3 = System.nanoTime();
            for (int i = 1; i < 1001; i++) { // lisätään tuhat asiakasta

                String asiakas = "A" + Integer.toString(i);

                stmnt.executeUpdate("INSERT INTO Asiakkaat (nimi) VALUES ('" + asiakas + "')");
            }
            long aika4 = System.nanoTime();
            System.out.println("Tehokkuustestin vaihe 2 (asiakkaiden lisääminen): aikaa kului " + (aika4 - aika3) / 1e9 + " sekuntia.");

            long aika5 = System.nanoTime();
            for (int i = 1; i < 1001; i++) { // lisätään tuhat pakettia yhden transaktion sisällä
               
                String koodi = "K" + Integer.toString(i);

                stmnt.executeUpdate("INSERT INTO Paketit (koodi, asiakas_id) VALUES ('" + koodi + "', " + i + ")");

            }
            long aika6 = System.nanoTime();
            System.out.println("Tehokkuustestin vaihe 3 (pakettien lisääminen): aikaa kului " + (aika6 - aika5) / 1e9 + " sekuntia.");

            long aika7 = System.nanoTime();
            for (int i = 1; i < 1000001; i++) { //lisätään miljoona tapahtumaa

                int pakettiId = arpoja.nextInt(1000) + 1; //1-1000
                int paikkaId = arpoja.nextInt(1000) + 1; //1-1000
                LocalDate localDate = LocalDate.now(); //2020-02-08
                String paiva = localDate.toString();

                LocalTime localTime = LocalTime.now(); //18:41:43.632
                String aika = localTime.toString();
                stmnt.executeUpdate("INSERT INTO Tapahtumat (paketti_id, paikka_id, kuvaus, paiva, aika) VALUES (" + pakettiId + ", " + paikkaId + ", 'lahetetty', '" + paiva + "' ,'" + aika + "')");
            }
            long aika8 = System.nanoTime();
            System.out.println("Tehokkuustestin vaihe 4 (tapahtumien lisääminen): aikaa kului " + (aika8 - aika7) / 1e9 + " sekuntia.");

            connection.commit();

            connection.setAutoCommit(true);

            long aika9 = System.nanoTime();
            for (int i = 1; i < 1001; i++) { // tuhat kyselyä, joista jokaisessa haetaan jonkun asiakkaan pakettien määrä

                int nro = arpoja.nextInt(1000) + 1; //1-1000
                String asiakkaanNimi = "A" + Integer.toString(nro);

                PreparedStatement prepared = connection.prepareStatement("SELECT COUNT(Paketit.koodi) AS maara FROM Asiakkaat LEFT JOIN Paketit ON Asiakkaat.id = Paketit.asiakas_id WHERE Asiakkaat.nimi = ?");
                prepared.setString(1, asiakkaanNimi);
                ResultSet result = prepared.executeQuery();

            }
            long aika10 = System.nanoTime();
            System.out.println("Tehokkuustestin vaihe 5 (kyselyt asiakkaan pakettien määrästä): aikaa kului " + (aika10 - aika9) / 1e9 + " sekuntia.");

            long aika11 = System.nanoTime();
            for (int i = 1; i < 1001; i++) { // tuhat kyselyä, joista jokaisessa haetaan jonkin paketin tapahtumien määrä

                int nro = arpoja.nextInt(1000) + 1; //1-1000
                String paketinKoodi = "K" + Integer.toString(nro);

                PreparedStatement prepared = connection.prepareStatement("SELECT COUNT(Tapahtumat.kuvaus) AS maara FROM  Paketit LEFT JOIN Tapahtumat ON Paketit.id = Tapahtumat.paketti_id WHERE Paketit.koodi = ?");
                prepared.setString(1, paketinKoodi);
                ResultSet result = prepared.executeQuery();
            }
            long aika12 = System.nanoTime();
            System.out.println("Tehokkuustestin vaihe 6 (kyselyt paketin tapahtumien määrästä): aikaa kului " + (aika12 - aika11) / 1e9 + " sekuntia.");

        } catch (SQLException ex) {
            System.out.println("Testin suorittaminen epäonnistui.");
        }

    }
}
