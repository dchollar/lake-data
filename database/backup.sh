#!/usr/bin/env sh

projectFolder=/home/dan/work/lake-data
# Backup storage directory
backupFolder=$projectFolder/database

# MySQL user
user=backup

fileName=large_dump
sqlFile=$backupFolder/$fileName.sql
zipFile=$fileName.zip
#this file holds the password and is not checked in
defaultFile=$backupFolder/defaults.cnf

echo "performing backup"
cd $projectFolder || exit
mysqldump --defaults-file=$defaultFile --databases pipe_lake --user=$user --lock-tables --add-drop-database --result-file=$sqlFile

echo "compressing backup"
cd $backupFolder || exit
rm $zipFile
zip -9 -q $zipFile $fileName.sql
rm $fileName.sql
cd $projectFolder || exit

echo "pushing to git"
git add database/large_dump.zip
git commit -m "database backup"
git push
