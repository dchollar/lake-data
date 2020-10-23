# lake-data
web application for maintaining lake water quality data on pipe lake.

# TODO
 - create a database backup process on ubuntu machine
    - cron process that will dump the db to the project and then commit and push it to git
 - create maintenance page for measurements and events
    - Will only be for updating or deleting. Not inserting.
 - change locations maintenance page to allow for creating new ones
    - low priority. not sure of the need yet.
 - allow power users a read only view of the maintenance pages
    - low priority. not sure of the need yet.
  - Add a privacy statement page linked from the footer
  - Add a version that is displayed in the page footer
  - Maybe remove phone number from contact. Email might be enough.
  - Create instructions or help documentation for data entry page and all maintenance pages. have this info appear in an overlay.

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
- ChartJS: https://www.chartjs.org/

Wisconsin DNR data site is https://dnrx.wisconsin.gov/swims/login.jsp