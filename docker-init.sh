#!/bin/bash
# Wrapper script to execute init_db.sql with correct working directory
cd /app/sql
mariadb -u root -p"$MYSQL_ROOT_PASSWORD" "$MYSQL_DATABASE" < init_db.sql
