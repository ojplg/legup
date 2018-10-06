# LegUp

Experimental project to read PDFs and extract votes from the Illinois legislature.

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

# TODO list

Do not forget to grep the code for TODO and FIXME. There are always things there.

Technical Betterments

* Frank recommends http://www.liquibase.org/ for managing migrations. This
  could become a concern, once Legup is being used and the database cannot
  simply be nuked. I have included liquibase mark-ups in the sql scripts
  and a short script for using liquibase to build the schema.
* Also, need to look into [postgres backups](https://www.postgresql.org/docs/10/static/backup.html)
  I have set up pg_dump for cron on the aws instance.
* Actually make a real connection pool and automated reclamation
* Maybe throw out the whole of the DB access layer (hrorm) and replace with 
  Hibernate/javax.persistence or Mybatis (there's a branch for this) or JDBI 
  (proyal's favorite) 
* Silent logging for unit tests (maybe)

Features/bugs to work on

* Write more help screens
* Legislator Persistence
  * Perhaps check for and perform updates when changes to legislator data are found
  * The legislator model includes a session id, but many legislators serve in multiple sessions.
    A way to link or mark people as the same would be helpful.
* Bill Persistence
  * Perhaps four (or 5) upload chunks, separating sponsors from bill itself
  * Need to use transactions for all the pieces (also turn off postgres autocommit)
  * Need to handle situation where only some parts have changed, use updates and deletes
* Scoring/Grading
  * Need different grading scales for different chambers
  * Allow configuration for score options, either in config files or report cards: something
  other than 1 point for voting, 2 for sponsoring, and 3 for chief sponsors
  * Allow configuartion of grading options: absolute grades versus grading on a curve
* Add legislator tracking for veto session, etc (Louisa must define)
* Load things other than "Third Reading" Vote PDFs: Committee votes

# Better Option

See: [https://openstates.org/](https://openstates.org/)

Code: [https://github.com/openstates/](https://github.com/openstates/)
