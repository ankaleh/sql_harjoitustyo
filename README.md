# SQL-harjoitustyön raportti

## Selostus toteutetuista toiminnallisuuksista

Harjoitustyössä toteutettiin Java-kielellä komentoriviohjelma, jonka avulla voi mahdollisimman sujuvasti käyttää pakettien seurantaan suunniteltua tietokantaa. 

Heti käynnistymisen jälkeen ohjelma luettelee toiminnot, joita sillä voidaan suorittaa, ja toimintoja vastaavat numeronäppäimet. 

Luokassa main otetaan yhteys tietokantaan. Tämän jälkeen ohjelma siirtyy toistolauseeseen, jossa käyttäjää pyydetään valitsemaan toiminto. 

Toistolauseesta suoritus ohjataan Javan switch-lauseen avulla eri metodeihin, joista jokaisen vastuulla on yksi ohjelman tarjoama toiminto. Metodit on nimetty niiden suorittamien toimintojen mukaan (esim. luoTietokanta, lisaaPaikka ja lisaaAsiakas). Jokainen metodi käsittelee itse mahdolliset SQL-poikkeuksensa. 

Kaikki ne ohjelman tekemät tietokantakyselyt ja -komennot, joissa tarvitaan käyttäjän syöttämää tietoa, ovat parametrisoituja. 

Ohjelmassa on seuraavat toiminnot:

	0) Numeronäppäimen 0 painaminen lopettaa sovelluksen toiminnan. 

	1) Sovellus luo taulut tyhjään tietokantaan, kun käyttäjä painaa numeronäppäintä 1. Jos tietokanta on jo luotu (eli näppäintä 1 on painettu jo aiemmin), tapahtuu SQL-poikkeus, joka käsitellään siten, että ohjelma tulostaa: "Tietokannan luominen epäonnistui, tai tietokanta on jo luotu". Virheilmoituksen jälkeen suoritus palaa mainin toistolauseeseen. 

	2) Sovellus lisää uuden paikan tietokantaan, kun käyttäjä painaa numeronäppäintä 2.  Ohjelma kysyy käyttäjältä paikan nimen. Poikkeuksen tapahtuessa sovellus antaa virheilmoituksen: "VIRHE: Paikan lisääminen epäonnistui, tai paikka on jo lisätty tietokantaan. " 

	3) Sovellus lisää uuden asiakkaan tietokantaan, kun käyttäjä painaa numeronäppäintä 3. Ohjelma kysyy käyttäjältä asiakkaan nimen. Poikkeuksen tapahtuessa sovellus antaa virheilmoituksen: "VIRHE: Asiakkaan lisääminen epäonnistui, tai asiakas on jo lisätty tietokantaan."

	4) Sovellus lisää uuden paketin tietokantaan, kun käyttäjä painaa numeronäppäintä 4. Ohjelma kysyy käyttäjältä paketin seurantakoodin ja asiakkaan nimen. 

Koska Paketit-taulussa paketti liitetään asiakkaaseen Asiakkaat.id:n avulla, sovellukseen toteutettiin haeId-metodi, jolle annetaan parametreina asiakkaan nimi ja SQL-komento, jolla haeId-metodi toteuttaa parametrisoidun tietokantakyselyn ja palauttaa nimeen liittyvän Asiakkaat.id:n. 

On mahdollista, että asiakasta ei ole lisätty tietokantaan, minkä takia sovelluksesta tehtiin käyttäjäystävällisempi siten, että käyttäjälle ilmoitetaan, mikäli asiakasta ei ole tietokannassa. Jos asiakasta ei ole lisätty tietokantaan, haeId-metodin tekemä parametrisoitu kysely johtaa poikkeukseen, joka käsitellään siten, että metodi palauttaa arvon -1, minkä jälkeen kutsuva metodi tulostaa ilmoituksen: "Asiakasta ei ole lisätty tietokantaan. Lisää asiakas tietokantaan painamalla näppäintä 3." Tämän jälkeen ohjelma palaa main-metodin toistolauseeseen. Lisäksi sovellus varautuu kutsuvassa metodissa tapahtuvaan SQL-poikkeukseen antamalla sellaisen sattuessa virheilmoituksen: "VIRHE: Paketin lisääminen epäonnistui, tai paketti on jo lisätty tietokantaan. " 

Yksi vaihtoehto olisi ollut käyttää viiteavainta, jolloin Paketit-tauluun olisi mahdotonta lisätä Asiakkaat.id:tä, jota ei ole olemassa, mutta kun Asiakkaat.id:tä tarvitaan tässä kohdassa myös paketin lisäämiseen, päädyttiin käyttämään edellä esiteltyä haeId-metodia.

	5) Sovellus lisää uuden tapahtuman tietokantaan, kun käyttäjä painaa numeronäppäintä 5. Ohjelma kysyy käyttäjältä paketin seurantakoodin, tapahtuman paikan ja kuvauksen. Tapahtuman päivämäärän ja ajan sovellus hakee automaattisesti Javan LocalDate- ja LocalTime- 
luokkien avulla. 

Koska Tapahtumat-taulussa tapahtuma liitetään oikeaan paikkaan ja pakettiin näiden id:iden perusteella, metodissa hyödynnetään jälleen kohdassa 4 esiteltyä haeId-metodia. Jos paikkaa tai pakettia ei ole lisätty tietokantaan, kutsuva metodi tulostaa (esimerkiksi paikan puuttuessa) ilmoituksen: "Paikkaa ei ole lisätty tietokantaan. Lisää paikka tietokantaan painamalla näppäintä 2." Tämän jälkeen ohjelma palaa main-metodin toistolauseeseen. Kutsuvassa metodissa tapahtuvaan SQL-poikkeukseen sovellus varautuu antamalla sellaisen sattuessa virheilmoituksen: "VIRHE: Tapahtuman lisääminen epäonnistui."

	6) Toiminto käynnistyy, kun käyttäjä painaa numeronäppäintä 6. Sovellus hakee paketin kaikki tapahtumat käyttäjän syöttämän seurantakoodin avulla. Sovellus tulostaa tapahtumien päivämäärän, ajan, paikan ja tapahtuman kuvauksen. Tulostuksen luonteen takia sovellukseen lisättiin toiminnallisuus, joka tulostaa ilmoituksen "Ei tapahtumia", jos pakettia ei ole vielä yhdistetty mihinkään tapahtumaan. Tämä toiminnallisuus toteutettiin luokan ResultSet metodin wasNull-avulla. 

SQL-poikkeuksen sattuessa ohjelma tulostaa: "VIRHE: Tietojen hakeminen epäonnistui. Tarkista paketin seurantakoodi." Tarkistuskehotus lisättiin sen takia, että  SQL-poikkeus tapahtuu ainakin silloin, jos seurantakoodia eli pakettia ei ole lisätty tietokantaan. Vielä käyttäjäystävällisemmän sovelluksesta saisi, jos paketin olemassaolo tarkistettaisiin ja paketin puuttumisesta ilmoitettaisiin heti käyttäjän syötettyä sen seurantakoodin. Toiminnallisuuden voisi toteuttaa haeId-metodin avulla. Toiminnallisuutta ei toteutettu sen takia, että käyttäjältä kysytään tässä vain yhtä asiaa, eli vaikka toiminnallisuus lisättäisiin, se ei vähentäisi käyttäjän työtä.

	7) Toiminto käynnistyy, kun käyttäjä painaa numeronäppäintä 7. Sovellus hakee asiakkaan paketit ja niihin liittyvien tapahtumien määrän. Ohjelma kysyy käyttäjältä asiakkaan nimen ja tulostaa jokaisen asiakkaan paketin seurantakoodin ja pakettiin liittyvien tapahtumien määrän. Jälleen hyödynnettiin ResultSet-luokan metodia wasNull niitä tapauksia silmällä pitäen, joissa asiakas on tietokannassa, mutta hänelle ei ole lisätty vielä yhtään pakettia. Tällaisessa tapauksessa ohjelma tulostaa: "Asiakkaalle ei ole lisätty paketteja." SQL-poikkeuksen sattuessa ohjelma sen sijaan tulostaa: "VIRHE: Tietojen hakeminen epäonnistui. Tarkista asiakkaan nimi." 

	8) Toiminto käynnistyy, kun käyttäjä painaa numeronäppäintä 8. Sovellus hakee paikan tapahtumat. Paikan nimi kysytään käyttäjältä. Tähän metodiin toteutettiin paikan olemassaolon tarkastus haeId-metodin avulla, koska käyttäjältä pyydetään paikan lisäksi myös päivämäärää. Jos paikkaa ei ole tietokannassa, ohjelma kertoo siitä. Jos paikka on tietokannassa, sovellus tulostaa: "Tapahtumia paikassa X vvvv-kk-pp: Y."   X on käyttäjältä saatu paikan nimi, ja Y on SQL-kyselyn tuottama tulos (voi olla myös 0). 

	9)  Toiminto käynnistyy, kun käyttäjä painaa numeronäppäintä 9. Sovellus suorittaa tietokannan tehokkuustestin. Tietokantaan lisätään tuhat paikkaa, tuhat asiakasta, tuhat pakettia ja miljoona tapahtumaa. Sitten sovellus suorittaa tuhat kyselyä eri asiakkaiden pakettien määrästä ja tuhat kyselyä eri pakettien tapahtumien määrästä. Testiin haluttiin satunnaisuutta, minkä takia tapahtumien lisäämisessä ja kyselyissä käytettiin apuna Javan Random-luokkaa.

## SQL-skeema

CREATE TABLE Asiakkaat (id INTEGER PRIMARY KEY, nimi
TEXT UNIQUE);
CREATE TABLE Paikat (id INTEGER PRIMARY KEY, nimi
TEXT UNIQUE);
CREATE TABLE Paketit (id INTEGER PRIMARY KEY, koodi
TEXT UNIQUE, asiakas_id INTEGER);
CREATE TABLE Tapahtumat (paketti_id INTEGER, paikka_id INTEGER, kuvaus TEXT, paiva TEXT, aika TEXT);

## Tehokkuustestin tulokset

Testi 1)  Tulokset ilman lisättyjä indeksejä:

Tehokkuustestin vaihe 1 (paikkojen lisääminen): aikaa kului 0.0114927 sekuntia.
Tehokkuustestin vaihe 2 (asiakkaiden lisääminen): aikaa kului 0.0075621 sekuntia.
Tehokkuustestin vaihe 3 (pakettien lisääminen): aikaa kului 0.0092136 sekuntia.
Tehokkuustestin vaihe 4 (tapahtumien lisääminen): aikaa kului 5.681861399 sekuntia.
Tehokkuustestin vaihe 5 (kyselyt asiakkaan pakettien määrästä): aikaa kului 0.180854001 sekuntia.
Tehokkuustestin vaihe 6 (kyselyt paketin tapahtumien määrästä): aikaa kului 111.728418599 sekuntia.

	Testi 2)  Tulokset, kun luotiin indeksit sarakkeille Asiakkaat.nimi, Paketit.koodi ja Tapahtumat.paketti_id:

Tehokkuustestin vaihe 1 (paikkojen lisääminen): aikaa kului 0.013865401 sekuntia.
Tehokkuustestin vaihe 2 (asiakkaiden lisääminen): aikaa kului 0.010921101 sekuntia.
Tehokkuustestin vaihe 3 (pakettien lisääminen): aikaa kului 0.007903401 sekuntia.
Tehokkuustestin vaihe 4 (tapahtumien lisääminen): aikaa kului 17.559164 sekuntia.
Tehokkuustestin vaihe 5 (kyselyt asiakkaan pakettien määrästä): aikaa kului 0.1363558 sekuntia.
Tehokkuustestin vaihe 6 (kyselyt paketin tapahtumien määrästä): aikaa kului 7.809617201 sekuntia.

	Testi 3)  Tulokset, kun luotiin indeksi sarakkeelle Tapahtumat.paketti_id:

Tehokkuustestin vaihe 1 (paikkojen lisääminen): aikaa kului 0.009122 sekuntia.
Tehokkuustestin vaihe 2 (asiakkaiden lisääminen): aikaa kului 0.005124201 sekuntia.
Tehokkuustestin vaihe 3 (pakettien lisääminen): aikaa kului 0.007915199 sekuntia.
Tehokkuustestin vaihe 4 (tapahtumien lisääminen): aikaa kului 17.507136199 sekuntia.
Tehokkuustestin vaihe 5 (kyselyt asiakkaan pakettien määrästä): aikaa kului 0.129052501 sekuntia.
Tehokkuustestin vaihe 6 (kyselyt paketin tapahtumien määrästä): aikaa kului 7.959492101 sekuntia.

	Testi 4)  Tulokset, kun luotiin indeksi sarakkeelle Paketit.koodi:

Tehokkuustestin vaihe 1 (paikkojen lisääminen): aikaa kului 0.0112377 sekuntia.
Tehokkuustestin vaihe 2 (asiakkaiden lisääminen): aikaa kului 0.0065185 sekuntia.
Tehokkuustestin vaihe 3 (pakettien lisääminen): aikaa kului 0.009644 sekuntia.
Tehokkuustestin vaihe 4 (tapahtumien lisääminen): aikaa kului 5.9801219 sekuntia.
Tehokkuustestin vaihe 5 (kyselyt asiakkaan pakettien määrästä): aikaa kului 0.147209 sekuntia.
Tehokkuustestin vaihe 6 (kyselyt paketin tapahtumien määrästä): aikaa kului 111.2129174 sekuntia.

	4) Vertailua ja päätelmiä:

Merkittävästi indeksien lisääminen vaikutti vain tehokkuustestin vaiheen 6 suoritusaikaan. Vaiheessa tehtiin kyselyt tuhannen paketin tapahtumien määrästä. Kyselyt tehtiin parametrisoidulla SQL-komennolla 
	"SELECT COUNT(Tapahtumat.kuvaus) AS maara 
	FROM Paketit LEFT JOIN Tapahtumat 
	ON Paketit.id = Tapahtumat.paketti_id WHERE Paketit.koodi = ?". 
Kyselyjen kesto oli suurimmillaan n. 111.73 sekuntia. Kun testissä 3 luotiin indeksi sarakkeelle Tapahtumat.paketti_id, kyselyjen kesto oli vain noin 7.96  sekuntia. Kun testissä 2 indeksoitiin Tapahtumat.paketti_id:n lisäksi myös Paketit.koodi, vaiheeseen 6 kului aikaa n.  7.81 sekuntia. 

Tapahtumat.paketti_id:n indeksoiminen lisäsi kuitenkin luonnollisesti vaiheen 4 kestoa (vaiheessa tietokantaan lisättiin tuhat tapahtumaa). Ilman indeksointia vaiheen 4 kesto oli  pienimmillään n. 5.7 sekuntia ja indeksoinnin jälkeen suurimmillaan n. 17.56 sekuntia. 

Vaiheessa 5 suoritettiin tuhat kyselyä eri asiakkaiden pakettien määristä parametrisoidulla SQL-komennolla 
	"SELECT COUNT(Paketit.koodi) AS maara 
	FROM Asiakkaat LEFT JOIN Paketit 
	ON Asiakkaat.id = Paketit.asiakas_id WHERE Asiakkaat.nimi = ?". 
Indeksoinnin lisääminen Asiakkaat.nimi-sarakkeelle ei vaikuttanut merkittävästi kyselyjen kestoon: ilman indeksointia kesto oli enimmillään n. 0.18 sekuntia ja indeksoinnin jälkeen pienimmillään n. 0.14 sekuntia. 

Tehokkuustestin tulosten perusteella sovelluksessa kannattaa indeksoida siis ainakin Tapahtumat.paketti_id. Sen indeksointi pidentää toki tapahtumien lisäämiseen kuluvaa aikaa, mutta tällaisessa käytössä tapahtumia lisätään yleensä yksitellen. Sen sijaan yksittäiseen pakettiin voi liittyä useita tapahtumia, minkä takia hakutoiminnallisuuden on hyvä olla nopea. 

Koska joissakin sovelluksen kohdissa tarkistetaan, löytyykö asiakas, paikka tai paketti tietokannasta hakemalla paikkojen, asiakkaiden ja pakettien id:itä näiden nimien tai koodin perusteella, olisi todennäköisesti järkevää indeksoida myös Asiakkaat.nimi, Paketit.koodi ja Paikat.nimi. 
