# Script for taking a back-up of the legup database
# Use psql to restore

DATE=`date +%Y%m%d`
FILEPATH=/var/legup/db_dumps/$DATE.sql

echo "Making legup backup for $DATE to $FILEPATH"

pg_dump legup > $FILEPATH
