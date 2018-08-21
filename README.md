# legup

Experimental project to read PDFs and extract votes from the Illinois legislature.

Does not do much of anything yet.

# Technical

* A pre-requisite to running the application (or the tests), is to install Postgres.
  Under the db directory are some scripts. Run them like this
    ````
    sudo -u postgres psql -f setup.sql
    psql -f db/structures.sql legup
    ````
    to get that working.
* The build is done using maven just
   ```` 
   mvn compile
   mvn test
   ````   
* The run script included assumes my home directory right now. That needs to be fixed.
* The application will start running on port 8000.
* Some dependencies, aside from postgres are
  * Lombok (for good or for ill)
  * Jetty
  * Velocity
  * PDFBox
  * JSoup    

# TODO list

Do not forget to grep the code for TODO and FIXME. There are always things there.

Technical Betterments

* Improve injection db connection logic/access to web tier, do not use raw Connection object
* Actually make a real conunection pool and automated reclamation
* Save link to votes pdf when downloading/parsing

Features to work on

* Legislator import needs work: some names are not parsing correctly
* Add a way to do name overrides in the DB
* Name matching requires more sophistication
* Add a concept of a report card: collection of bills and desired votes
* Figure out how to parse bill sponsors and introducers
* Calculate legislator score
* Allow configuration for score options
* Calculate legislator grades from scores
* Allow configuartion of grading options (grade on a curve?)

# Better Option

See: [https://openstates.org/](https://openstates.org/)

Code: [https://github.com/openstates/](https://github.com/openstates/)
