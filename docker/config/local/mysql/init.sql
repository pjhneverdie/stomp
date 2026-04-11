CREATE USER IF NOT EXISTS 'localuser'@'%' IDENTIFIED BY 'localpass';
GRANT ALL PRIVILEGES ON localdb.* TO 'localuser'@'%';
FLUSH PRIVILEGES;