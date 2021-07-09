# lake-data
web application for maintaining lake water quality data on pipe lake

Characteristic Types:
  EVENT: something that happens once a year for a specific characteristic.
  WATER: Something that happens once a day for a specific characteristic.

# TODO
 - create a database backup process on ubuntu machine
    - cron process that will dump the db to the project and then commit and push it to git
 - allow power users a read only view of the maintenance pages
    - low priority. not sure of the need yet.
  - Add a privacy statement page linked from the footer
  - Get someone to add the other documents to the system
  - Link documents to pipe lake web site
  - Change style sheet to make site look more like the pipe lake site.
  - Create instructions or help documentation for data entry page and all maintenance pages. have this info appears in an overlay.
  - Enable a date picker in the data maintenance page - NO they have all looked bad
  - set up https://superset.apache.org/
  - Enable https on the server - NO not going to do it. requires domain registration  https://www.digitalocean.com/community/tutorials/how-to-secure-apache-with-let-s-encrypt-on-ubuntu-20-04
  - Create a mapping between event type characteristics and sites. This would create a characteristic_site table.
    This is analogous to the characteristic_location table. Not sure I want to do this. This would limit the
    events to a specific site for data entry.
  - Bulk upload and download of documents. Not sure I want to do this. Will do as an on-demand request.

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
## Lucine
Lucine is an open source indexing and search library for java
- https://nullbeans.com/spring-boot-and-lucene-configuration-example/
- https://wkrzywiec.medium.com/full-text-search-with-hibernate-search-lucene-part-1-e245b889aa8e
- https://dzone.com/articles/jpa-searching-using-lucene
- https://lucene.apache.org/index.html
## PDF
Libraries for dealing with pdf or other types of documents. iText used to be the leading 
library for this but is no longer free.
- https://pdfbox.apache.org/
- https://tika.apache.org/ All document types. Not limited to PDF.
- https://colwil.com/how-to-extract-text-from-a-scanned-pdf-using-ocr-in-java/
## Solr
Is a complete open source system that uses lucine. Not needed currently
- https://solr.apache.org/

Larry's Example: https://optidat.com/pl/pipe-lakes-district-records-archive/
