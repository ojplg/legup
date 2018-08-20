# legup

Experimental project to read PDFs and extract votes from the Illinois legislature.

Does not do much of anything yet.

This is undergoing introductory development right now. It requires a Postgres DB.
It requires Lombok. The Maven stuff is very much in flux. The run script might
not work. Etc.

# TODO list

Do not forget to grep the code for TODO and FIXME. There are always things there.

Technical Betterments

* Improve injection db connection logic/access to web tier, do not use raw Connection object
* Actually make a real connection pool and automated reclamation
* Name class should include a parsedFrom string
* Consistency of names between routes, handlers, templates. Should be
  * /some_nice_route
  * SomeNiceRoute.java
  * some_nice_route.vtl
  Names should always be view_xyz, save_xyz, review_xyz, etc

Features to work on

* Legislator import needs work: some names are not parsing correctly
* Add a way to do name overrides in the DB
* View all votes of a legislator


See: [https://openstates.org/](https://openstates.org/)
Code: [https://github.com/openstates/](https://github.com/openstates/)
