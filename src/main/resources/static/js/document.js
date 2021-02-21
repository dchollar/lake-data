$(document).ready(function () {

    let timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
    let siteId;
    let site;

    // when the site changes go and get the document details for that site
    // based on the document details build the page based on the path of the document.

    $('#sitesChoice').on('change', function () {
        $("#message").text("").hide();
        siteId = $(this).val();
        getSite();
        getDocuments();
    });

    function getSite() {
        $.ajax(
            {
                type: "GET",
                url: "public/api/sites/" + siteId,
                dataType: "json",
                success: function (aSite) {
                    site = aSite;
                },
                error: function (response) {
                    console.log(response);
                }
            });
    }

    function getDocuments() {
        $.ajax(
            {
                type: "GET",
                url: "public/api/sites/" + siteId + "/documents?timezone=" + timezone,
                dataType: "json",
                success: function (documents) {
                    buildResultDiv(documents);
                },
                error: function (response) {
                    console.log(response);
                }
            });
    }

    function buildResultDiv(documents) {

        let html = '';
        if (documents === undefined || documents.length === 0) {
            html += '<h4>No Documents Found</h4>';
        } else {
            html += '<h2>' + site.description + ' Record Archive</h2>';
            html += '<ul>';

            let previousHeaders;
            let openDepth = 0;

            for (let document of documents) {

                let headerCount = 0;
                let headers = document.path.split('/')

                for (let header of headers) {
                    if (!previousHeaders || previousHeaders.length <= headerCount || previousHeaders[headerCount] !== header) {
                        let testCount = headerCount + 1;
                        if (previousHeaders && openDepth >= testCount) {
                            while (openDepth >= testCount) {
                                html += '</ul>';
                                openDepth--;
                            }
                        }
                        let headerStyle = 4 + headerCount;
                        html += '<li><h' + headerStyle + '>' + header + '</h' + headerStyle + '></li>';
                        html += '<ul>';
                        openDepth++;
                    }
                    headerCount++;
                }

                //------------------------------------

                html += '<li> <a href="documents/' + document.id + '/document" target="_blank">' + document.title + '</a></li>';
                previousHeaders = headers;
            }

            html += '</ul>';
        }

        $('#documentDiv').html(html);
    }

})
;