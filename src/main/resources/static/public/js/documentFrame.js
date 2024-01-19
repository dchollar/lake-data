const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
const baseUri = location.href.substring(0, location.href.indexOf('public/'));

$(document).ready(function () {
    let documents = JSON.parse($('#documentList').val());
    buildDocumentDiv(documents);
});

function buildDocumentUrl(html, document) {
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
    return html;
}

function buildDocumentDiv(documents) {

    let html = '';
    if (documents === undefined || documents.length === 0) {
        html += '<h4>No Documents Found</h4>';
    } else {
        html += '<h6><i> Number of Documents retrieved: ';
        html += documents.length;
        html += '</i></h6>';
        html += '<div id="accordion"><div class="panel list-group"><ul>';

        let previousHeaders;
        let openDepth = 0;
        let counter = 0;

        for (let document of documents) {
            let headerCount = 0;
            let headers = document.path.split('/');

            if (document.path !== '') {
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
            }

            //------------------------------------

            html += '<li class="list-group-item list-group-item-primary">';
            html = buildDocumentUrl(html, document);

            previousHeaders = headers;
        }

        html += '</ul></div></div>';
    }

    $('#documentDiv').html(html);
}
