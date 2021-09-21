#!/usr/bin/env sh

projectFolder=/home/dan/IdeaProjects/lake-data
# Backup storage directory
backupFolder=$projectFolder/database

# MySQL user
user=backup

sqlFile=$backupFolder/large_dump.sql
zipFile=$backupFolder/large_dump.zip
#this file holds the password and is not checked in
defaultFile=$backupFolder/defaults.cnf

cd $projectFolder
mysqldump --defaults-file=$defaultFile --databases pipe_lake --user=$user --lock-tables --add-drop-database --result-file=$sqlFile

# Compress backup
rm $zipFile
zip -9 -q $zipFile $sqlFile
rm $sqlFile

git add database/large_dump.zip
git commit -m "database backup"
git push

