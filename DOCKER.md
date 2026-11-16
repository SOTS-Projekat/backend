# Running SOTS with Docker

One command starts everything: Postgres, the Spring Boot backend, the KST (Flask/IITA) service,
the React frontend, GraphDB, and a one-time seeder that fills the database.

The compose file lives in the backend repo and builds the other two repos from sibling folders,
so clone all three next to each other:

```bash
git clone https://github.com/SOTS-Projekat/backend.git
```

```bash
git clone https://github.com/SOTS-Projekat/frontend.git
```

```bash
git clone https://github.com/SOTS-Projekat/kst.git
```

```
SOTS/
├── backend/    <- docker-compose.yml, DOCKER.md, docker/ (seed + GraphDB)
├── frontend/
└── kst/
```

Then, from the `backend` folder:

```bash
docker compose up -d --build
```

The first build takes a few minutes (Maven + npm downloads). After that:

| What | Where |
|---|---|
| Frontend | http://localhost:5173 |
| Backend API | http://localhost:8080 |
| Postgres | `localhost:5433`, db `SOTS`, user `postgres` / `password` |
| Generated OWL files | `../kst/exports/` (the kst repo) |
| GraphDB (visual graph + SPARQL) | http://localhost:7200, repository `sots` |

## Seeded data

All passwords are `sifra`.

- **`user2`** (PROFESSOR) owns 4 knowledge domains, each with one test:
  1. Osnovi programiranja (the domain from `src/main/resources/skripta.txt`)
  2. Baze podataka
  3. Objektno orijentisano programiranje
  4. Web programiranje
- **`student01` … `student30`** (STUDENT) have each solved all 4 tests.
- `user1` … `user12` from `skripta.txt` also exist (odd = student, even = professor).

The students' answers are simulated from a "true" prerequisite structure that differs a bit from
the links the professor drew, so the KST-generated real domain differs from the drawn one.
Edit `docker/seed/seed.sql` to change domains, questions or the simulation.

## Generating a real knowledge domain + OWL file

1. Log in as `user2` / `sifra`.
2. Open **Knowledge Domains** and click a domain (opens its Edit page).
3. The page calls the KST service, which runs IITA on the students' answers. It shows the real
   domain next to the professor's and writes `../kst/exports/<Domain_name>_REAL.owl`.

Open that `.owl` file in Protégé (File → Open).

Each file is self-contained: it declares the classes (`Domain`, `Competency`), object properties
(`hasCompetency`, `prerequisiteOf`) and data properties (`name`, `generatedAt`) it uses, so it opens
without `schema.owl`. To also add an `owl:imports` of the shared schema, set `OWL_SCHEMA_IMPORT`
before starting (the path must be valid for whoever opens the file):

```bash
OWL_SCHEMA_IMPORT=http://example.org/eks/schema.owl docker compose up -d
```

## Exploring the domains in GraphDB

The `graphdb-sync` service loads every `../kst/exports/*.owl` file into GraphDB (repository `sots`),
one named graph per file: `http://example.org/eks/graph/<file name>`. It checks for changes every
5 seconds, so a domain regenerated from the app shows up in GraphDB almost right away.

1. Open http://localhost:7200 and pick the **sots** repository (top right).
2. **Explore → Visual graph**: type a domain or competency name (e.g. `Baze`) and pick it.
   Click a node to expand its `hasCompetency` / `prerequisiteOf` links.
3. **SPARQL**: query across all domains, for example the direct prerequisites of every competency:

```sparql
PREFIX ex: <http://example.org/eks/schema#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

SELECT ?domain ?from ?to WHERE {
  ?d a ex:Domain ; rdfs:label ?domain ; ex:hasCompetency ?a .
  ?a ex:prerequisiteOf ?b .
  ?a rdfs:label ?from . ?b rdfs:label ?to .
  FILTER NOT EXISTS { ?b ex:prerequisiteOf ?a }               # skip equivalent pairs
  FILTER NOT EXISTS {                                         # skip indirect (transitive) links
    ?a ex:prerequisiteOf ?c . ?c ex:prerequisiteOf ?b .
    FILTER (?c != ?a && ?c != ?b)
    FILTER NOT EXISTS { ?c ex:prerequisiteOf ?a }
    FILTER NOT EXISTS { ?b ex:prerequisiteOf ?c }
  }
}
ORDER BY ?domain ?from ?to
```

GraphDB runs as the free edition (no license needed). Its data is kept in the `graphdb-data`
volume; `docker compose down -v` wipes it and everything is reloaded from `../kst/exports` on the next start.

## Useful commands

```bash
docker compose logs -f backend kst      # follow logs
docker compose logs seed                # what the seeder did
docker compose down                     # stop (data is kept)
docker compose down -v                  # stop and wipe the database (re-seeds next time)
```

## Notes

- The seeder waits for Hibernate (`ddl-auto=update`) to create the tables, then loads
  `docker/seed/seed.sql`. It runs on every `up` but skips users and domains that already exist.
- The backend reads the KST URL from `KST_URL` (property `kst.url`, default
  `http://127.0.0.1:5000`), so running everything locally without Docker still works.
- `kst/requirements.txt` is UTF-16 encoded; the KST Dockerfile converts it before `pip install`.
