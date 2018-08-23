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
    to get that working. It should ask for a password several times.
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

Features to work on

* Figure out how to parse bill sponsors and introducers
* Allow configuration for score options
* Calculate legislator grades from scores
* Allow configuartion of grading options (grade on a curve?)
* Figure out URLs for vote PDFs
* Determine differences between vote types (Third Reading, committee, etc)
* Add a way to do name overrides in configuration or maybe DB


# Better Option

See: [https://openstates.org/](https://openstates.org/)

Code: [https://github.com/openstates/](https://github.com/openstates/)
