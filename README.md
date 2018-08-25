# legup

Experimental project to read PDFs and extract votes from the Illinois legislature.

Does not do much of anything yet.

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

* Improve injection db connection logic/access to web tier, do not use raw Connection object
* Actually make a real connection pool and automated reclamation
* Maybe throw out the whole of the DB access layer and replace with Hibernate/javax.persistence
* Need a way to chain handlers, perhaps changing the handler interface to something more useful
* Need a way to handle things that need to be in session and then cleared up

Features to work on

* Work on Bill Votes PDF that use "A" code
* Automatically go from bill search to vote load
* Figure out how to determine introducers
* Allow configuration for score options
* Calculate legislator grades from scores
* Allow configuartion of grading options (grade on a curve?)
* Determine differences between vote types (Third Reading, committee, etc)
* Add a way to do name overrides in configuration or maybe DB


# Better Option

See: [https://openstates.org/](https://openstates.org/)

Code: [https://github.com/openstates/](https://github.com/openstates/)
