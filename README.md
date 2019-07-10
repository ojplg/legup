# LegUp

LegUp is a web application for building legislative scorecards. LegUp
is pretty Illinois specific.

LegUp tries to do the following:
* Load information about legislators/bills/votes into its database by scraping the legislature's web page.
* Allow the user to create report cards, which include a collection of bills and whether or not the bill
  should be supported or opposed.
* Score the legislators against the report cards by checking their voting records.

LegUp exposes its functionality via a simple web application.

# Technical

* The build is done using maven just
   ````
   mvn compile
   mvn test
   ````
* Assuming mvn builds things, then you can do:
   ````
   ./dev.sh
   ````
   And the application will start running on port 8000.
* The dev.sh script above will launch against an in-memory H2 database, and data
  will not be persisted on restart.
  To use the application with permanent storage, you must install Postgres.
  Under the db directory are some scripts. Run the shell script
    ````
    cd db/
    ./db_setup.sh
    ````
    to get that working. It should ask for a password several times. (Of course,
    if you think running random shell scripts that ask for your password several times
    is nuts, just do what it says in the script and check the contents of the SQL files
    referenced to make sure nothing crazy is happening.)
    Once this is complete, the run.sh script should work, and data will persist across
    application restarts.
* Some dependencies, aside from postgres, are
  * Lombok (for good or for ill)
  * Jetty
  * Velocity
  * PDFBox
  * JSoup
  * H2 (for tests)
  * [hrorm!](http://hrorm.org)
* Other
  * LegUp uses [liquibase](http://www.liquibase.org/) for managing
  migrations. There is mark-up in the SQL structures creation script
  that indicates what has been applied. There is a shell script for
  running liquibase under the scripts directory.
  * The AWS instance I run has [postgres backups](https://www.postgresql.org/docs/10/static/backup.html)
  via the postgres pg_dump tool running under cron.

# TODO list

Do not forget to grep the code for TODO and FIXME. There are always things there.
s
* Persist all legislative maneuvers
  * Need to track vote type: cannot just be enumerated
    * Committee versus chamber votes
    * Name of committee
    * Type of vote (third reading, etc)
  * Some bill actions do not have a legislator associated with them
  * Should always be additive: do not delete old data
  * Persist both action date and load date
  * There is more than one chief sponsor
  * Sponsors can be removed, upgraded, and downgraded

* User Fixes
  * Allow users to invite others to their organizations
  * Allow organization names to be changed
  * Make sure submission of names handle spaces correctly
  * Error messages on user/organization creation
* Super User
  * Make an attribute on the USERS table
* Write more help screens
* Legislator Persistence
  * Perhaps check for and perform updates when changes to legislator data are found
  * The legislator model includes a session id, but many legislators serve in multiple sessions.
    A way to link or mark people as the same would be helpful.
* Bill Persistence
  * Perhaps four (or 5) upload chunks, separating sponsors from bill itself
* Scoring/Grading
  * Allow configuration for score options, either in config files or report cards: something
  other than 1 point for voting, 2 for sponsoring, and 3 for chief sponsors
  * Allow configuartion of grading options: absolute grades versus grading on a curve
* Add legislator tracking for veto session, etc (Louisa must define)
* Load things other than "Third Reading" Vote PDFs: Committee votes, roll call votes
* Actually make a real DB connection pool and automated reclamation
* Silent logging for unit tests (maybe)

# Another Option

See: [https://openstates.org/](https://openstates.org/)

Code: [https://github.com/openstates/](https://github.com/openstates/)
