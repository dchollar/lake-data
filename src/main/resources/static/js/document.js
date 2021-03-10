$(document).ready(function () {

    let timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
    let siteId;
    let site;

    // when the site changes go and get the document details for that site
    // based on the document details build the page based on the path of the document.

    $('#sitesChoice').on('change', function () {
        $("#message").text("").hide();
        siteId = $(this).val();
        let url = "public/api/sites/" + siteId + "/documents?timezone=" + timezone;
        getSite();
        getDocuments(url);
    });

    $('#submitButton').on('click', function () {
        $("#message").text("").hide();
        let searchWord = $('#searchPhrase').val();
        if (siteId && searchWord) {
            let url = "public/api/documents/site/" + siteId + "/search?searchWord=" + searchWord + "&timezone=" + timezone;
            getDocuments(url);
        }
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
                error: function (xhr, resp) {
                    console.log(resp);
                    $("#message").text("There was an issue with your request. " + xhr.responseText).show();
                }
            });
    }

    function getDocuments(url) {
        $.ajax(
            {
                type: "GET",
                url: url,
                dataType: "json",
                success: function (documents) {
                    buildResultDiv(documents);
                },
                error: function (xhr, resp) {
                    console.log(resp);
                    $("#message").text("There was an issue with your request. " + xhr.responseText).show();
                }
            });
    }

    function buildResultDiv(documents) {

        let html = '';
        if (documents === undefined || documents.length === 0) {
            html += '<h4>No Documents Found</h4>';
        } else {
            html += '<h2>' + site.description + ' Record Archive</h2>';
            html += '<div id="accordion"><div class="panel list-group"><ul>';


            let previousHeaders;
            let openDepth = 0;
            let counter = 0;

            for (let document of documents) {

                let headerCount = 0;
                let headers = document.path.split('/')

                for (let header of headers) {
                    counter++;
                    // determine if a header needs to be written or closed
                    if (!previousHeaders || previousHeaders.length <= headerCount || previousHeaders[headerCount] !== header) {

                        let nextHeaderCount = headerCount + 1;
                        // close any headers
                        if (previousHeaders && openDepth >= nextHeaderCount) {
                            while (openDepth >= nextHeaderCount) {
                                html += '</ul></div></li>';
                                openDepth--;
                            }
                        }

                        // write out the header
                        let headerStyle = 4 + headerCount;
                        let divName = header.replace(/\s+/g, '') + counter + '_div';
                        html += '<li class="list-group-item">';
                        html += '<a href="#' + divName + '" data-parent="#accordion" data-toggle="collapse">';
                        html += '<h' + headerStyle + '>' + header + '</h' + headerStyle + '>';
                        html += '</a>';
                        html += '<div class="collapse list-group-flush" id="' + divName + '">';
                        html += '<ul>';

                        openDepth++;
                    }
                    headerCount++;
                }

                //------------------------------------

                html += '<li class="list-group-item list-group-item-secondary">';
                html += '<a  href="public/api/documents/';
                html += document.id;
                html += '/document" target="_blank">';
                html += document.title;
                html += ' (';
                html += document.fileSize;
                html += ' KB)';
                html += '</a>';
                html += '</li>';

                previousHeaders = headers;
            }

            html += '</ul></div></div>';
        }

        $('#documentDiv').html(html);
    }

})
;
