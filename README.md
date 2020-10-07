# lake-data
web application for maintaining lake water quality data on pipe lake.

# TODO
 - create a database backup process on ubuntu machine
    - cron process that will dump the db to the project and then commit and push it to git
 - create maintenance page for unit location combination
    - low priority. not sure of the need yet.
 - create maintenance page for measurements and events
    - Will only be for updating or deleting. Not inserting.
    - low priority. not sure of the need yet.
 - change locations maintenance page to allow for creating new ones
    - low priority. not sure of the need yet.
 - allow power users a read only view of the maintenance pages
    - low priority. not sure of the need yet.
 - add more data???
    - stream flow data
    - DNR SWIMS data

# Technologies Used:
- Groovy
- Spring Boot
- Spring JPA
- Spring Security
- MySQL
- ThymeLeaf
- JQuery
- Bootstrap
- js-grid for maintenance pages: http://js-grid.com/

Wisconsin DNR data site is https://dnrx.wisconsin.gov/swims/login.jsp
