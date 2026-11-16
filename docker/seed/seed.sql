-- =====================================================================
-- SOTS seed: users + 4 knowledge domains, each with a test and 30
-- simulated student results, so "real" knowledge domains (KST / IITA)
-- and OWL files can be generated from the UI.
--
-- Safe to run more than once: users and domains that already exist
-- (matched by username / domain name) are skipped.
--
-- All passwords: sifra
-- Professor that owns all domains: user2
-- Students that solved the tests: student01 .. student30
-- =====================================================================

SET client_encoding = 'UTF8';
BEGIN;

-- ---------------------------------------------------------------------
-- Users (user1..user12 from skripta.txt + student01..student30)
-- ---------------------------------------------------------------------
INSERT INTO app_user (email, username, role, password)
SELECT v.email, v.username, v.role, '$2a$10$LtdPqNhC69dcJZVu60VKfeXvoPceh3blRhJdzi15LRPzkFJCaLt9W'
FROM (VALUES
    ('user1@example.com',  'user1',  'STUDENT'),
    ('user2@example.com',  'user2',  'PROFESSOR'),
    ('user3@example.com',  'user3',  'STUDENT'),
    ('user4@example.com',  'user4',  'PROFESSOR'),
    ('user5@example.com',  'user5',  'STUDENT'),
    ('user6@example.com',  'user6',  'PROFESSOR'),
    ('user7@example.com',  'user7',  'STUDENT'),
    ('user8@example.com',  'user8',  'PROFESSOR'),
    ('user9@example.com',  'user9',  'STUDENT'),
    ('user10@example.com', 'user10', 'PROFESSOR'),
    ('user11@example.com', 'user11', 'STUDENT'),
    ('user12@example.com', 'user12', 'PROFESSOR')
) AS v(email, username, role)
WHERE NOT EXISTS (SELECT 1 FROM app_user u WHERE u.username = v.username);

INSERT INTO app_user (email, username, role, password)
SELECT format('student%s@example.com', lpad(i::text, 2, '0')),
       format('student%s', lpad(i::text, 2, '0')),
       'STUDENT',
       '$2a$10$LtdPqNhC69dcJZVu60VKfeXvoPceh3blRhJdzi15LRPzkFJCaLt9W'
FROM generate_series(1, 30) AS i
WHERE NOT EXISTS (SELECT 1 FROM app_user u WHERE u.username = format('student%s', lpad(i::text, 2, '0')));

-- ---------------------------------------------------------------------
-- Helper: creates one domain (nodes, links, test, questions, answers)
-- and simulates every studentNN solving the test.
--
-- p_links       - prerequisite links the PROFESSOR drew (stored in DB)
-- p_true_links  - the "real" prerequisite structure used to simulate
--                 students; it differs slightly from p_links so the
--                 KST-generated real domain shows something interesting
-- p_questions   - one row per node: [question, correct, wrong, wrong, wrong]
--
-- Simulation: each student gets an ability in [0.35, 1.0]; each concept is
-- "known" with that probability, then the known set is closed under
-- p_true_links (you can't know a concept without its prerequisites).
-- Answers get 5% careless errors and 10% lucky guesses.
-- ---------------------------------------------------------------------
CREATE FUNCTION pg_temp.seed_domain(
    p_name         text,
    p_description  text,
    p_created      date,
    p_test_title   text,
    p_professor    text,
    p_labels       text[],
    p_links        int[],
    p_true_links   int[],
    p_questions    text[],
    p_frontend_ids text[] DEFAULT NULL
) RETURNS void LANGUAGE plpgsql AS $fn$
DECLARE
    v_n       int := array_length(p_labels, 1);
    v_prof    bigint;
    v_kd      bigint;
    v_test    bigint;
    v_id      bigint;
    v_res     bigint;
    v_ans     bigint;
    v_student bigint;
    v_nodes   bigint[] := '{}';
    v_qs      bigint[] := '{}';
    v_pair    int[];
    v_m       boolean[];
    v_ability float8;
    v_ok      int;
    v_correct boolean;
BEGIN
    IF EXISTS (SELECT 1 FROM knowledge_domain WHERE name = p_name AND is_real = false) THEN
        RAISE NOTICE 'Domain "%" already exists - skipped', p_name;
        RETURN;
    END IF;

    SELECT id INTO STRICT v_prof FROM app_user WHERE username = p_professor;

    INSERT INTO knowledge_domain (created_at, description, name, professor_id, is_real)
    VALUES (p_created, p_description, p_name, v_prof, false)
    RETURNING id INTO v_kd;

    FOR i IN 1..v_n LOOP
        INSERT INTO node (frontend_id, label, knowledge_domain_id)
        VALUES (COALESCE(p_frontend_ids[i], gen_random_uuid()::text), p_labels[i], v_kd)
        RETURNING id INTO v_id;
        v_nodes := v_nodes || v_id;
    END LOOP;

    FOREACH v_pair SLICE 1 IN ARRAY p_links LOOP
        INSERT INTO links_between_nodes (label, knowledge_domain_id, source_node_id, target_node_id)
        VALUES ('/', v_kd, v_nodes[v_pair[1]], v_nodes[v_pair[2]]);
    END LOOP;

    INSERT INTO test (title, professor_id, knowledge_domain_id)
    VALUES (p_test_title, v_prof, v_kd)
    RETURNING id INTO v_test;

    FOR i IN 1..v_n LOOP
        INSERT INTO question (question_text, test_id, node_id)
        VALUES (p_questions[i][1], v_test, v_nodes[i])
        RETURNING id INTO v_id;
        v_qs := v_qs || v_id;

        INSERT INTO answer (question_id, answer_text, is_correct) VALUES
            (v_id, p_questions[i][2], true),
            (v_id, p_questions[i][3], false),
            (v_id, p_questions[i][4], false),
            (v_id, p_questions[i][5], false);
    END LOOP;

    -- Simulated students. Answers are inserted in question order, which is
    -- what KnowledgeDomainService.generateMatrix relies on.
    FOR v_student IN SELECT id FROM app_user WHERE username LIKE 'student%' ORDER BY username LOOP
        v_ability := 0.35 + random() * 0.65;
        v_m := array_fill(false, ARRAY[v_n]);
        FOR i IN 1..v_n LOOP
            v_m[i] := random() < v_ability;
        END LOOP;
        FOR k IN 1..v_n LOOP
            FOREACH v_pair SLICE 1 IN ARRAY p_true_links LOOP
                IF NOT v_m[v_pair[1]] THEN
                    v_m[v_pair[2]] := false;
                END IF;
            END LOOP;
        END LOOP;

        INSERT INTO result (student_id, test_id, score, date_and_time)
        VALUES (v_student, v_test, 0, p_created + interval '7 days' + random() * interval '5 days')
        RETURNING id INTO v_res;

        v_ok := 0;
        FOR i IN 1..v_n LOOP
            v_correct := CASE WHEN v_m[i] THEN random() >= 0.05 ELSE random() < 0.10 END;
            IF v_correct THEN
                SELECT id INTO v_ans FROM answer WHERE question_id = v_qs[i] AND is_correct;
                v_ok := v_ok + 1;
            ELSE
                SELECT id INTO v_ans FROM answer
                WHERE question_id = v_qs[i] AND NOT is_correct
                ORDER BY random() LIMIT 1;
            END IF;
            INSERT INTO answered_question (result_id, question_id, answer_id)
            VALUES (v_res, v_qs[i], v_ans);
        END LOOP;

        UPDATE result SET score = round(100.0 * v_ok / v_n, 1) WHERE id = v_res;
    END LOOP;

    RAISE NOTICE 'Domain "%" created (id=%, test id=%)', p_name, v_kd, v_test;
END;
$fn$;

SELECT setseed(0.42);  -- reproducible simulated answers

-- ---------------------------------------------------------------------
-- 1) Osnovi programiranja  (original domain from skripta.txt)
-- ---------------------------------------------------------------------
SELECT pg_temp.seed_domain(
    'Osnovi programiranja',
    'Test iz osnova programiranja',
    DATE '2024-12-06',
    'Osnove programiranja',
    'user2',
    ARRAY['Promjenjive i tipovi podataka', 'Operatori i izrazi', 'Uslovne strukture', 'Petlje', 'Funkcije',
          'Nizovi', 'Rad sa fajlovima', 'Osnovne strukture podataka', 'Rekurzija', 'Algoritmi i sortiranje'],
    ARRAY[[1,2],[2,3],[3,4],[5,9],[4,6],[6,8],[8,10],[7,6],[7,4]],
    ARRAY[[1,2],[2,3],[3,4],[4,6],[6,8],[8,10],[4,5],[5,9],[1,7]],
    ARRAY[
        ['Šta su promenljive u programiranju?', 'Promenljive su skladišni prostori za podatke.', 'Promenljive su funkcije u programu.', 'Promenljive su alati za sortiranje.', 'Promenljive su petlje.'],
        ['Šta je operator u programiranju?', 'Operator je simbol za operaciju nad podacima.', 'Operator je varijabla.', 'Operator je vrsta petlje.', 'Operator je struktura podataka.'],
        ['Kako funkcionišu uslovne strukture?', 'Uslovne strukture omogućavaju donošenje odluka.', 'Uslovne strukture iteriraju kroz nizove.', 'Uslovne strukture služe za sortiranje.', 'Uslovne strukture skladište podatke.'],
        ['Kako se koristi for petlja?', 'For petlja se koristi za iteraciju.', 'For petlja se koristi za sortiranje.', 'For petlja definiše funkcije.', 'For petlja je vrsta uslovne strukture.'],
        ['Šta su funkcije u programiranju?', 'Funkcije grupišu kod za ponovno korišćenje.', 'Funkcije služe za skladištenje podataka.', 'Funkcije su petlje.', 'Funkcije su nizovi.'],
        ['Kako radimo sa nizovima?', 'Nizovi skladište kolekcije podataka.', 'Nizovi izvršavaju uslove.', 'Nizovi sortiraju podatke.', 'Nizovi definišu petlje.'],
        ['Kako čitati podatke iz fajla?', 'Čitanje fajlova zahteva otvaranje fajla.', 'Čitanje fajlova radi sa petljama.', 'Čitanje fajlova zahteva sortiranje.', 'Čitanje fajlova definiše promenljive.'],
        ['Šta su osnovne strukture podataka?', 'Stog i red su osnovne strukture podataka.', 'Strukture podataka su alati za sortiranje.', 'Strukture podataka su funkcije.', 'Strukture podataka su petlje.'],
        ['Kako funkcioniše rekurzija?', 'Rekurzija je funkcija koja poziva samu sebe.', 'Rekurzija je petlja.', 'Rekurzija je algoritam za sortiranje.', 'Rekurzija je struktura podataka.'],
        ['Koji algoritmi se koriste za sortiranje?', 'Algoritmi za sortiranje uređuju podatke.', 'Algoritmi sortiranja definišu petlje.', 'Algoritmi sortiranja su strukture podataka.', 'Algoritmi sortiranja su varijable.']
    ],
    ARRAY['7230e047-1e08-438d-b24b-1b5eeb5b5c97', '80d1898b-8c17-48d9-8df2-b24f1ced7f2b', 'f689e9c1-7366-4fb0-bd50-98100b6ef394',
          '9cf3203b-c181-45ce-be84-c78478dcd387', 'f3440cbc-e96f-4ae2-a1fc-17891a53a73e', '48eb335b-de1b-467e-8b7b-a3b327b1b260',
          '13b8d09b-2390-4486-9396-fb65720e1bb2', '990092f6-8627-4d7f-be92-871d4576ce86', 'dd8cf318-83e6-4268-875c-7f2604ef36f2',
          '85aca6fb-ff3e-491a-896f-0bf7bae1128f']
);

-- ---------------------------------------------------------------------
-- 2) Baze podataka
-- ---------------------------------------------------------------------
SELECT pg_temp.seed_domain(
    'Baze podataka',
    'Test iz osnova relacionih baza podataka',
    DATE '2025-03-10',
    'Baze podataka - kolokvijum',
    'user2',
    ARRAY['Relacioni model', 'ER modelovanje', 'Osnovni SQL upiti', 'Normalizacija',
          'Spajanje tabela (JOIN)', 'Agregacija i grupisanje', 'Indeksi i optimizacija upita', 'Transakcije i ACID'],
    ARRAY[[1,2],[1,3],[2,4],[3,5],[3,6],[5,7],[3,8]],
    ARRAY[[1,2],[1,3],[2,4],[3,5],[5,6],[5,7],[4,8],[3,8]],
    ARRAY[
        ['Šta je relacija u relacionom modelu?', 'Tabela sa redovima (torkama) i kolonama (atributima).', 'Veza između dva servera.', 'Vrsta indeksa.', 'Jedna kolona u tabeli.'],
        ['Šta predstavlja entitet u ER modelu?', 'Objekat iz realnog sveta o kome čuvamo podatke.', 'SQL upit.', 'Primarni ključ.', 'Vrsta transakcije.'],
        ['Koja SQL naredba se koristi za čitanje podataka?', 'SELECT', 'INSERT', 'UPDATE', 'DROP'],
        ['Šta je cilj normalizacije?', 'Smanjenje redundanse i anomalija ažuriranja.', 'Ubrzanje mreže.', 'Šifrovanje podataka.', 'Brisanje duplikata iz upita.'],
        ['Šta radi INNER JOIN?', 'Vraća redove koji imaju par u obe tabele.', 'Briše tabelu.', 'Vraća sve redove leve tabele.', 'Spaja dve baze podataka.'],
        ['Čemu služi GROUP BY?', 'Grupisanju redova radi primene agregatnih funkcija.', 'Sortiranju kolona.', 'Kreiranju indeksa.', 'Brisanju grupa korisnika.'],
        ['Zašto se kreiraju indeksi?', 'Da bi se ubrzala pretraga podataka.', 'Da bi se smanjio broj tabela.', 'Da bi se zaključala tabela.', 'Da bi se obrisali duplikati.'],
        ['Šta znači slovo A u ACID?', 'Atomičnost (Atomicity).', 'Asinhronost.', 'Agregacija.', 'Autorizacija.']
    ]
);

-- ---------------------------------------------------------------------
-- 3) Objektno orijentisano programiranje
-- ---------------------------------------------------------------------
SELECT pg_temp.seed_domain(
    'Objektno orijentisano programiranje',
    'Test iz osnova objektno orijentisanog programiranja',
    DATE '2025-04-14',
    'OOP - kolokvijum',
    'user2',
    ARRAY['Klase i objekti', 'Enkapsulacija', 'Konstruktori', 'Nasleđivanje',
          'Polimorfizam', 'Apstraktne klase i interfejsi', 'Izuzeci', 'Projektni obrasci'],
    ARRAY[[1,2],[1,3],[1,4],[4,5],[5,6],[6,8],[3,7]],
    ARRAY[[1,2],[1,3],[2,4],[4,5],[4,6],[5,8],[6,8],[1,7]],
    ARRAY[
        ['Šta je objekat?', 'Instanca klase.', 'Vrsta petlje.', 'Promenljiva tipa int.', 'Biblioteka funkcija.'],
        ['Šta je enkapsulacija?', 'Skrivanje unutrašnjeg stanja objekta iza javnih metoda.', 'Nasleđivanje više klasa.', 'Pretvaranje tipova.', 'Brisanje objekta iz memorije.'],
        ['Kada se poziva konstruktor?', 'Prilikom kreiranja objekta.', 'Prilikom brisanja objekta.', 'Pri svakom pozivu metode.', 'Samo pri pokretanju programa.'],
        ['Šta omogućava nasleđivanje?', 'Da klasa preuzme atribute i metode druge klase.', 'Da se objekat kopira u fajl.', 'Da se metoda izvrši paralelno.', 'Da se klasa obriše.'],
        ['Šta je polimorfizam?', 'Mogućnost da isti poziv metode ima različito ponašanje u podklasama.', 'Više konstruktora bez parametara.', 'Čuvanje objekata u nizu.', 'Zabrana nasleđivanja.'],
        ['Po čemu se interfejs razlikuje od klase?', 'Definiše ugovor (metode) bez implementacije stanja.', 'Ne može imati metode.', 'Uvek ima konstruktor.', 'Ne može se implementirati.'],
        ['Čemu služi try/catch blok?', 'Obradi izuzetaka (grešaka) tokom izvršavanja.', 'Definisanju petlji.', 'Kreiranju objekata.', 'Nasleđivanju klasa.'],
        ['Koji je primer kreacionog projektnog obrasca?', 'Fabrika (Factory).', 'Dekorator (Decorator).', 'Posmatrač (Observer).', 'Adapter.']
    ]
);

-- ---------------------------------------------------------------------
-- 4) Web programiranje
-- ---------------------------------------------------------------------
SELECT pg_temp.seed_domain(
    'Web programiranje',
    'Test iz osnova web programiranja',
    DATE '2025-05-19',
    'Web programiranje - kolokvijum',
    'user2',
    ARRAY['HTML osnove', 'CSS i stilizovanje', 'JavaScript osnove', 'DOM manipulacija',
          'HTTP protokol', 'Asinhrono programiranje (fetch, Promise)', 'REST API', 'React komponente'],
    ARRAY[[1,2],[1,3],[3,4],[3,6],[5,7],[6,7],[4,8]],
    ARRAY[[1,2],[1,3],[3,4],[3,6],[5,6],[6,7],[4,8],[6,8]],
    ARRAY[
        ['Čemu služi HTML?', 'Opisivanju strukture web stranice.', 'Stilizovanju stranice.', 'Radu sa bazom podataka.', 'Kompajliranju JavaScript-a.'],
        ['Kako se u CSS-u bira element po klasi?', '.naziv-klase', '#naziv-klase', '@naziv-klase', '$naziv-klase'],
        ['Koja ključna reč deklariše promenljivu kojoj se vrednost ne može ponovo dodeliti?', 'const', 'var', 'let', 'static'],
        ['Šta vraća document.getElementById?', 'DOM element sa datim id-jem.', 'Niz svih elemenata.', 'CSS pravilo.', 'HTTP odgovor.'],
        ['Koji HTTP status znači da resurs nije pronađen?', '404', '200', '301', '500'],
        ['Šta vraća fetch()?', 'Promise koji se razrešava HTTP odgovorom.', 'Direktno JSON objekat.', 'HTML element.', 'Broj bajtova.'],
        ['Koja HTTP metoda se u REST-u obično koristi za kreiranje resursa?', 'POST', 'GET', 'DELETE', 'HEAD'],
        ['Šta je props u React-u?', 'Podaci koje roditeljska komponenta prosleđuje detetu.', 'Globalni CSS stilovi.', 'Lokalna baza podataka.', 'Vrsta HTTP zahteva.']
    ]
);

COMMIT;
