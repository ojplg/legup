# legup

Experimental project to read PDFs and extract votes from the Illinois legislature.

Does not do much of anything yet.

This is undergoing introductory development right now. It requires a Postgres DB.
It requires Lombok. The Maven stuff is very much in flux. The run script might
not work. Etc.

# TODO list

Tech debt

* Put in logging
* Remove duplicate domain objects
* Decide on how to implement joins in DAO framework
* Inject connection logic/access to DAOs to web tier
* Structure for web tier: some kind of simple handler interfacess

Features to work on

* Match different names between PDFs and HTML
* Save results from parsing vote results


See: [https://openstates.org/](https://openstates.org/)
Code: [https://github.com/openstates/](https://github.com/openstates/)
