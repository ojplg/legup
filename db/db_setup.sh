sudo -u postgres psql -f create_dev_db.sql
sudo -u postgres psql -f create_test_db.sql
sudo -u postgres psql -f create_db.sql
psql -f create_structures.sql legup_test
psql -f create_structures.sql legup_dev
psql -f create_structures.sql legup

