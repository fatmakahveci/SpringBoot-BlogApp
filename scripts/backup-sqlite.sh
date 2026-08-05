#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 2 ]]; then
  echo "Usage: $0 SOURCE_DATABASE BACKUP_FILE" >&2
  exit 64
fi

source_database=$1
backup_file=$2

if [[ ! -f "$source_database" ]]; then
  echo "Source database does not exist: $source_database" >&2
  exit 66
fi
if [[ "$source_database" == *"'"* || "$backup_file" == *"'"* ]]; then
  echo "Database paths must not contain single quotes." >&2
  exit 65
fi

backup_directory=$(dirname "$backup_file")
mkdir -p "$backup_directory"
temporary_backup=$(mktemp "$backup_directory/.blog-backup.XXXXXX")
trap 'rm -f "$temporary_backup"' EXIT

sqlite3 "$source_database" ".timeout 5000" ".backup '$temporary_backup'"
integrity=$(sqlite3 "$temporary_backup" "PRAGMA integrity_check;")
if [[ "$integrity" != "ok" ]]; then
  echo "Backup integrity check failed: $integrity" >&2
  exit 1
fi

chmod 600 "$temporary_backup"
mv -f "$temporary_backup" "$backup_file"
trap - EXIT
echo "Verified SQLite backup written to $backup_file"
