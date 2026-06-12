#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
SOURCE_FILE="$PROJECT_ROOT/local.properties.example"
DEST_FILE="$PROJECT_ROOT/local.properties"

USAGE="Usage: scripts/setup-local.sh [-f]

Copies local.properties.example to local.properties in the project root.
Use -f to overwrite an existing local.properties."

FORCE=0
while getopts ":f" opt; do
  case ${opt} in
    f ) FORCE=1 ;;
    * ) echo "$USAGE"; exit 2 ;;
  esac
done

if [ -f "$DEST_FILE" ] && [ "$FORCE" -ne 1 ]; then
  echo "local.properties already exists. Use -f to overwrite." >&2
  exit 1
fi

cp -f "$SOURCE_FILE" "$DEST_FILE"

SDK_PATH=""
for candidate in \
  "${ANDROID_SDK_ROOT:-}" \
  "${ANDROID_HOME:-}" \
  "$HOME/Library/Android/sdk" \
  "$HOME/Android/Sdk"; do
  if [ -n "$candidate" ] && [ -d "$candidate" ]; then
    SDK_PATH="$candidate"
    break
  fi
done

if [ -n "$SDK_PATH" ]; then
  printf '\nsdk.dir=%s\n' "$SDK_PATH" >> "$DEST_FILE"
  echo "Android SDK found: $SDK_PATH"
else
  echo "Android SDK was not found automatically." >&2
  echo "Open Android Studio > Settings/Preferences > Android SDK, then add sdk.dir to local.properties." >&2
fi

echo "Created local.properties. Add your API configuration before building."
