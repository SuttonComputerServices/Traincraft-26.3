#!/usr/bin/env bash
# usage: verify_assets.sh <source-root> <port-root>
# Unchanged assets must exist in the port at their lowercase path, byte-identical.
# Converted paths (Forge blockstates/models/lang -> 26.3 formats) are checked by AssetGameTest instead.
set -u
src="$1/src/main/resources/assets/traincraft"
dst="$2/fabric/src/main/resources/assets/traincraft"
bad=0
while IFS= read -r -d '' f; do
  rel="${f#$src/}"
  case "$rel" in
    lang_old/*|lang/*|blockstates/*|models/block/*|models/item/*|items/*|recipes/*) continue ;;
    blockstates/fluids.json) continue ;;
  esac
  drel="$(printf '%s' "$rel" | tr 'A-Z ' 'a-z_')"
  if [ ! -f "$dst/$drel" ]; then echo "MISSING $rel"; bad=1
  elif [ "$(sha256sum <"$f")" != "$(sha256sum <"$dst/$drel")" ]; then echo "DIFFERS $rel"; bad=1; fi
done < <(find "$src" -type f -print0)
# Every asset path must be a valid Identifier path: [a-z0-9/._-] only.
while IFS= read -r -d '' f; do
  rel="${f#$dst/}"
  if printf '%s' "$rel" | grep -q '[^a-z0-9/._-]'; then echo "BAD PATH $rel"; bad=1; fi
done < <(find "$dst" -type f -print0)
exit $bad
