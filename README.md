# lake-data
web application for maintaining lake water quality data on pipe lake.

# TODO
 - create a database backup process on ubuntu machine
    - cron process that will dump the db to the project and then commit and push it to git
 - allow power users a read only view of the maintenance pages
    - low priority. not sure of the need yet.
  - Add a privacy statement page linked from the footer
  - Add a version that is displayed in the page footer
  - Create instructions or help documentation for data entry page and all maintenance pages. have this info appears in an overlay.
  - Enable a date picker in the data maintenance page - NO they have all looked bad
  - set up https://superset.apache.org/
  - Enable https on the server - NO not going to do it. requires domain registration  https://www.digitalocean.com/community/tutorials/how-to-secure-apache-with-let-s-encrypt-on-ubuntu-20-04
  - Create a mapping between characteristic and location and event. Want to enforce for data entry the valid locations or events for a characteristic. maybe?
  - Change the document page to have collapsible sections. All leaf nodes should collapse.
  - load other document types besides PDFs.
  - Bulk upload and download of documents. Not sure I want to do this


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
- https://nullbeans.com/spring-boot-and-lucene-configuration-example/
- https://wkrzywiec.medium.com/full-text-search-with-hibernate-search-lucene-part-1-e245b889aa8e
- https://dzone.com/articles/jpa-searching-using-lucene


- https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox
- https://pdfbox.apache.org/
- https://tika.apache.org/
- https://lucene.apache.org/solr/guide/6_6/uploading-data-with-solr-cell-using-apache-tika.html
- https://lucene.apache.org/index.html
- https://lucene.apache.org/solr/

- https://mvnrepository.com/artifact/com.itextpdf/itextpdf
- https://itextpdf.com/en Licensing issues with iText now.

-https://colwil.com/how-to-extract-text-from-a-scanned-pdf-using-ocr-in-java/

Larry's Example: https://optidat.com/pl/pipe-lakes-district-records-archive/
