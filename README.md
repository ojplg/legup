# legup

Experimental project to read PDFs and extract votes from the Illinois legislature.

Does not do much of anything yet.

This is undergoing introductory development right now. It requires a Postgres DB.
It requires Lombok. The Maven stuff is very much in flux. The run script might
not work. Etc.

# TODO list

Do not forget to grep the code for TODO and FIXME. There are always things there.

Technical Betterments

* Decide on how to implement joins in DAO framework
* Improve injection db connection logic/access to web tier, do not use raw Connection object
* Actually make a real connection pool and automated reclamation

Features to work on

* Match different names between PDFs and HTML (started)
 * Display all results
 * Display unmatched/uncollated votes
* Add overrides to DB and make CRUD screens


See: [https://openstates.org/](https://openstates.org/)
Code: [https://github.com/openstates/](https://github.com/openstates/)
