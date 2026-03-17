#!/bin/bash
# Wrapper script to execute init_db.sql with correct working directory
cd /app/sql
mariadb -u root -p"$MARIADB_ROOT_PASSWORD" "$MARIADB_DATABASE" < init_db.sql
