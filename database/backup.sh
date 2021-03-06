#!/usr/bin/env sh

projectfolder=/home/dan/IdeaProjects/db-backup/lake-data
# Backup storage directory
backupfolder=$projectfolder/database

# MySQL user
user=backup

sqlfile=$backupfolder/large_dump.sql
zipfile=$backupfolder/large_dump.zip
#this file holds the password and is not checked in
defaultfile=$backupfolder/defaults.cnf

cd $projectfolder
mysqldump --defaults-file=$defaultfile --databases pipe_lake --user=$user --lock-tables --add-drop-database --result-file=$sqlfile

# Compress backup
rm $zipfile
zip -9 -q $zipfile $sqlfile
rm $sqlfile

git add database/large_dump.zip
git commit -m "database backup"
git push

