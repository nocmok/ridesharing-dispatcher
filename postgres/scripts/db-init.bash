export PGPASSWORD=orp
psql -h localhost -p 5432 -U orp -d orp -f "$(dirname $0)/db-init.sql" -w