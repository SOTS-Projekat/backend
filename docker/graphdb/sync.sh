#!/bin/sh
# Keeps GraphDB in sync with ./kst/exports:
#  - creates the "sots" repository on first start
#  - loads every *.owl file into its own named graph  http://example.org/eks/graph/<file name>
#  - re-loads a file whenever it changes (e.g. after a professor regenerates a real domain)

GRAPHDB="${GRAPHDB_URL:-http://graphdb:7200}"
REPO="${GRAPHDB_REPO:-sots}"
DIR="${OWL_DIR:-/owl}"
STATE=/tmp/owl-sync-state

echo "graphdb-sync: waiting for GraphDB at $GRAPHDB ..."
until curl -sf "$GRAPHDB/rest/repositories" >/dev/null; do sleep 3; done

if ! curl -sf "$GRAPHDB/rest/repositories/$REPO" >/dev/null; then
  echo "graphdb-sync: creating repository '$REPO'"
  curl -sf -X POST "$GRAPHDB/rest/repositories" -F "config=@/graphdb/repo-config.ttl" || {
    echo "graphdb-sync: could not create repository" >&2; exit 1; }
  # Enable label/IRI autocomplete so domains and competencies can be found by name in the UI
  curl -s -X POST -H "X-GraphDB-Repository: $REPO" "$GRAPHDB/rest/autocomplete/enabled?enabled=true" >/dev/null
fi

touch "$STATE"
echo "graphdb-sync: watching $DIR"
while true; do
  for f in "$DIR"/*.owl; do
    [ -e "$f" ] || continue
    name=$(basename "$f")
    sum=$(md5sum "$f" | cut -d' ' -f1)
    grep -q "^$name $sum\$" "$STATE" && continue

    graph="http://example.org/eks/graph/$name"
    enc=$(printf '%s' "$graph" | sed 's#:#%3A#g; s#/#%2F#g')
    code=$(curl -s -o /dev/null -w '%{http_code}' -X PUT -H 'Content-Type: application/rdf+xml' \
      --data-binary "@$f" "$GRAPHDB/repositories/$REPO/rdf-graphs/service?graph=$enc")
    if [ "$code" = "204" ] || [ "$code" = "200" ]; then
      grep -v "^$name " "$STATE" > "$STATE.tmp"; echo "$name $sum" >> "$STATE.tmp"; mv "$STATE.tmp" "$STATE"
      echo "graphdb-sync: loaded $name -> <$graph>"
    else
      echo "graphdb-sync: FAILED to load $name (HTTP $code)" >&2
    fi
  done
  sleep 5
done
