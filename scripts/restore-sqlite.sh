#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 2 ]]; then
  echo "Usage: $0 BACKUP_FILE RESTORED_DATABASE" >&2
  exit 64
fi

backup_file=$1
restored_database=$2

if [[ ! -f "$backup_file" ]]; then
  echo "Backup file does not exist: $backup_file" >&2
  exit 66
fi

integrity=$(sqlite3 "$backup_file" "PRAGMA integrity_check;")
if [[ "$integrity" != "ok" ]]; then
  echo "Refusing to restore an invalid backup: $integrity" >&2
  exit 1
fi

restore_directory=$(dirname "$restored_database")
mkdir -p "$restore_directory"
temporary_restore=$(mktemp "$restore_directory/.blog-restore.XXXXXX")
trap 'rm -f "$temporary_restore"' EXIT
install -m 600 "$backup_file" "$temporary_restore"
mv -f "$temporary_restore" "$restored_database"
trap - EXIT
echo "Verified SQLite database restored to $restored_database"
