#!/usr/bin/env bash
set -euo pipefail

USAGE="Usage: setup-local.sh [-f]

Copies local.properties.example to local.properties in the project root.
Use -f to overwrite an existing local.properties."

FORCE=0
while getopts ":f" opt; do
  case ${opt} in
    f ) FORCE=1 ;;
    * ) echo "$USAGE"; exit 2 ;;
  esac
done

if [ -f local.properties ] && [ "$FORCE" -ne 1 ]; then
  echo "local.properties already exists. Use -f to overwrite." >&2
  exit 1
fi

cp -f local.properties.example local.properties
echo "Created local.properties from local.properties.example. Edit it and add your keys."