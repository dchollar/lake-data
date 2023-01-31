const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
const baseUri = location.href.substring(0,location.href.indexOf('public/'));
let siteId;
let site;

$(document).ready(function () {
    // when the site changes go and get the document details for that site
    // based on the document details build the page based on the path of the document.

    $('#sitesChoice').on('change', function () {
        $("#message").text("").hide();
        siteId = $(this).val();
        findDocuments();
    });

    $('#submitButton').on('click', function () {
        $("#message").text("").hide();
        findDocuments();

    });
})
;

function findDocuments() {
    if (siteId) {
        let searchWord = $('#searchPhrase').val();
        let category = $('#categoryPhrase').val();
        let url = baseUri + "public/api/sites/" + siteId + "/documents?timezone=" + timezone;
        if (searchWord) {
            url += "&searchWord=" + searchWord;
        }
        if (category) {
            url += "&category=" + category;
        }
        getSite();
        getDocuments(url);
    } else {
        $('#documentDiv').html('');
    }
}

function getSite() {
    $.ajax({
        async: false,
        type: "GET",
        url: baseUri + "public/api/sites/" + siteId,
        dataType: "json",
        success: function (aSite) {
            site = aSite;
        },
        error: function (xhr) {
            $("#message").text("There was an issue with your request. " + xhr.responseJSON.errorMessage).show();
        }
    });
}

function getDocuments(url) {
    $.ajax({
        async: false,
        type: "GET",
        url: url,
        dataType: "json",
        success: function (documents) {
            buildResultDiv(documents);
        },
        error: function (xhr) {
            $("#message").text("There was an issue with your request. " + xhr.responseJSON.errorMessage).show();
        }
    });
}

function buildResultDiv(documents) {

    let html = '';
    if (documents === undefined || documents.length === 0) {
        html += '<h4>No Documents Found</h4>';
    } else {
        html += '<h2>' + site.description + ' Record Archive</h2>';
        html += '<h6><i> Number of Documents retrieved: ';
        html += documents.length;
        html += '</i></h6>'
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
                    let divName = 'a_' + header.replace(/\s+/g, '') + counter + '_div';
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

            html += '<li class="list-group-item list-group-item-primary">';
            html += '<a href="';
            html += baseUri;
            html += 'public/api/documents/';
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
