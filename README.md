# lake-data
web application for maintaining lake water quality data on pipe lake.

# TODO
 - create a database backup process on ubuntu machine
    - cron process that will dump the db to the project and then commit and push it to git
 - allow power users a read only view of the maintenance pages
    - low priority. not sure of the need yet.
  - Add a privacy statement page linked from the footer
  - Add a version that is displayed in the page footer
  - display error messages in jsGrid
  - Create instructions or help documentation for data entry page and all maintenance pages. have this info appears in an overlay.
  - Enable a date picker in the data maintenance page - maybe
  - Add a PDF archive to the system. https://optidat.com/pl/pipe-lakes-district-records-archive/
  - set up https://superset.apache.org/
  - Create a mapping between characteristic and location and event. Want to enforce for data entry the valid locations or events for a characteristic. maybe?

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

# PDF Design Thoughts
https://nullbeans.com/spring-boot-and-lucene-configuration-example/
https://wkrzywiec.medium.com/full-text-search-with-hibernate-search-lucene-part-1-e245b889aa8e
https://dzone.com/articles/jpa-searching-using-lucene


https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox
https://pdfbox.apache.org/
https://tika.apache.org/
https://lucene.apache.org/solr/guide/6_6/uploading-data-with-solr-cell-using-apache-tika.html
https://lucene.apache.org/index.html
https://lucene.apache.org/solr/

https://mvnrepository.com/artifact/com.itextpdf/itextpdf
https://itextpdf.com/en Licensing issues with iText now.

Larry's Example: https://optidat.com/pl/pipe-lakes-district-records-archive/

document title
path - for example: 'Minutes/Board Meeting Minutes' strip any leading slash
uploaded date
associated site
creation date

blob
clob