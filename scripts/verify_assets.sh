#!/usr/bin/env bash
# usage: verify_assets.sh <source-root> <port-root>
set -u
src="$1/src/main/resources/assets/traincraft"
dst="$2/fabric/src/main/resources/assets/traincraft"
bad=0
while IFS= read -r -d '' f; do
  rel="${f#$src/}"
  # Renamed to lowercase because Identifier paths must be lowercase (see SteamSmallRenderer).
  drel="$rel"; [ "$rel" = "textures/trains/locoSmallSteamEU.png" ] && drel="textures/trains/locosmallsteameu.png"
  if [ ! -f "$dst/$drel" ]; then echo "MISSING $rel"; bad=1
  elif [ "$(sha256sum <"$f")" != "$(sha256sum <"$dst/$drel")" ]; then echo "DIFFERS $rel"; bad=1; fi
done < <(find "$src" -type f -print0)
exit $bad
