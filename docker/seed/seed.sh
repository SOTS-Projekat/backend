#!/bin/sh
# Waits until the backend (Hibernate ddl-auto=update) has created the tables,
# then loads seed.sql. seed.sql skips anything that already exists.
set -e

TABLES="'app_user','knowledge_domain','node','links_between_nodes','test','question','answer','result','answered_question'"

echo "seed: waiting for backend to create tables..."
i=0
until [ "$(psql -tAc "select count(*) from information_schema.tables where table_schema='public' and table_name in ($TABLES)")" = "9" ]; do
  i=$((i + 1))
  if [ "$i" -gt 120 ]; then
    echo "seed: timed out waiting for tables" >&2
    exit 1
  fi
  sleep 5
done
sleep 5  # let Hibernate finish adding constraints

psql -v ON_ERROR_STOP=1 -f /seed/seed.sql
echo "seed: done"
