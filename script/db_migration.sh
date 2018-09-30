#!/bin/bash

# For doing liquibase migrations

LIQUIBASEDIR=$HOME/liquibase/
LEGUPDIR=$HOME/git/legup/
MVNDIR=$HOME/.m2/repository/

CMD="./liquibase --changeLogFile=$LEGUPDIR/db/create_structures.sql\
            --username=legupuser\
            --password=legupuserpass\
            --url=jdbc:postgresql://localhost:5432/legup\
            --driver=org.postgresql.Driver\
            --classpath=$MVNDIR/org/postgresql/postgresql/42.2.4/postgresql-42.2.4.jar\
            update"

cd $LIQUIBASEDIR
echo $CMD
$CMD
