# DEL838_prog2hazi

## Főbb funkciók

* **Szerepköralapú hozzáférés (`User` -> `Reader` / `Manager`):**
  * **Reader (olvasó):** könyvek böngészése és keresése, kölcsönzés (aktív kölcsönzési limittel), visszavétel, saját aktív kölcsönzések megtekintése, valamint új olvasói fiók regisztrálása.
  * **Manager (könyvtáros/adminisztrátor):** új könyvek hozzáadása, példányszám növelése, könyvek törlése (ha nincs rajtuk aktív kölcsönzés).
* **Könyvtári elemek (`LibraryItem` -> `PrintedBook` / `EBook`):**
  * Nyomtatott könyvek (ISBN, oldalszám) és e-könyvek (formátum, fájlméret MB-ban) kezelése polimorfizmussal.
* **Kölcsönzési rendszer (`Loan`):**
  * Kölcsönzési és lejárati dátum nyilvántartása, késedelmi díj számítása visszavételkor.
* **Egyedi kivételkezelés:**
  * `LibraryException`, `UnauthorizedActionException` és `ItemNotAvailableException` a jogosulatlan műveletek és elérhetőségi hibák elkülönítésére.
* **Skálázható, streamelt adattárolás (`LibraryStorage`):**
  * Pufferelt I/O (`BufferedReader` / `BufferedWriter`, 64 KB puffer, UTF-8 kódolás) mesterséges darabszám- vagy sorlimit nélkül.
  * `library_items.csv`: könyvtári tételek mentése és betöltése.
  * `library_users.txt`: felhasználók (`MANAGER` / `READER`) mentése és betöltése.
  * `library_loans.txt`: aktív kölcsönzések mentése a kölcsönző adataival (`loanId`, `readerId`, `readerName`, `itemId`, `borrowDate`, `dueDate`) együtt, hogy a következő indításkor is megmaradjanak.
