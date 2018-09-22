# LegUp

Experimental project to read PDFs and extract votes from the Illinois legislature.

LegUp tries to do the following:
* Load information about legislators/bills/votes into its database by scraping the legislature's web page.
* Allow the user to create report cards, which include a collection of bills and whether or not the bill 
  should be supported or opposed.
* Score the legislators against the report cards by checking their voting records.

LegUp exposes its functionality via a simple web application.

# Technical

* A pre-requisite to running the application (or the tests), is to install Postgres.
  Under the db directory are some scripts. Run the shell script
    ````
    cd db/
    ./db_setup.sh
    ````
    to get that working. It should ask for a password several times. (Of course, 
    if you think running random shell scripts that ask for your password several times
    is nuts, just do what it says in the script and check the contents of the SQL files
    referenced to make sure nothing crazy is happening.)
* The build is done using maven just
   ```` 
   mvn compile
   mvn test
   ````   
* Assuming the DB is in place, and mvn builds things, then you can do:
   ````
   ./run.sh
   ````
   And the application will start running on port 8000.
* Some dependencies, aside from postgres, are
  * Lombok (for good or for ill)
  * Jetty
  * Velocity
  * PDFBox
  * JSoup    

# TODO list

Do not forget to grep the code for TODO and FIXME. There are always things there.

Technical Betterments

* Actually make a real connection pool and automated reclamation
* Maybe throw out the whole of the DB access layer (hrorm) and replace with Hibernate/javax.persistence 
  or Mybatis (there's a branch for this) or JDBI (proyal's favorite) 
  or at least break hrorm to a stand-alone project
* Need a way to chain handlers, perhaps changing the handler interface to something more useful
* Framework for form error checking (though there are few forms with much interaction) and error reporting
* Add more logging, particular for bill searching
* Add a way to do name overrides in configuration or maybe DB

Features/bugs to work on

* CSV screens for cut-and-paste to spreadsheets
* Low, high, median score on report card page
* Legislator Persistence
  * Perhaps check for and perform updates when changes to legislator data are found
  * The legislator model includes a session id, but many legislators serve in multiple sessions.
    A way to link or mark people as the same would be helpful.
* Bill Persistence
  * Perhaps four (or 5) upload chunks, separating sponsors from bill itself
  * Need to use transactions for all the pieces
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
