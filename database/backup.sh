#!/usr/bin/env sh

projectfolder=/
# Backup storage directory
backupfolder=$projectfolder/database

# MySQL user
user=lake_data_app

# MySQL password
password=Pa$$word8

sqlfile=$backupfolder/dump.sql
zipfile=$backupfolder/dump.zip

cd $projectfolder
rm $sqlfile
rm $zipfile

sudo mysqldump pipe_lake -u $user -p$password --lock-tables --add-drop-database > $sqlfile

# Compress backup
zip $zipfile $sqlfile
if [ $? == 0 ]; then
  echo 'The backup was successfully compressed'
else
  echo 'Error compressing backup' | mailx -s 'Backup was not created!' $recipient_email
  exit
fi
rm $sqlfile

git commit -m "database backup"
git push

