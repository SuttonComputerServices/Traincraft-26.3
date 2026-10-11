#!/usr/bin/env bash
# Exits 1 if any .java under common/src/main or fabric/src/main lacks Traincraft and LGPL in its first 20 lines.
cd "$(dirname "$0")/.." || exit 1
fail=0
while IFS= read -r f; do
  h=$(head -n 20 "$f")
  if ! grep -q Traincraft <<<"$h" || ! grep -q LGPL <<<"$h"; then echo "MISSING HEADER: $f"; fail=1; fi
done < <(find common/src/main fabric/src/main -name '*.java' | sort)
exit $fail
