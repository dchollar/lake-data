$(document).ready(function () {

    let siteId;
    let unitId;
    let selectedUnit

    $('#sitesChoice').on('change', function () {
        siteId = $(this).val();
        if (unitId) {
            getLocations();
        } else {
            $('#locationSelectionDiv').html('');
        }
    });

    $('#unitsChoice').on('change', function () {
        unitId = $(this).val();
        $.ajax(
            {
                type: "GET",
                url: "public/api/units/" + unitId,
                dataType: "json",
                success: function (unit) {
                    selectedUnit = unit;
                    if (siteId) {
                        getLocations();
                    } else {
                        $('#locationSelectionDiv').html('');
                    }
                },
                error: function (response) {
                    console.log(response);
                }
            });
    });

    $('#submitButton').on('click', function () {
        if (siteId && unitId) {
            let url = buildSubmitUrl();
            $.ajax(
                {
                    type: "GET",
                    url: url,
                    dataType: "json",
                    success: function (measurements) {
                        buildResultDiv(measurements);
                    },
                    error: function (xhr, resp, text) {
                        console.log(xhr, resp, text);
                        $("#message").text("There was an issue saving your data. Check your submission. " + xhr.responseText).show();
                    }
                });
        }
    });

    function buildSubmitUrl() {
        let url = "public/api/measurements?siteId=" + siteId + "&unitId=" + unitId;
        if (selectedUnit && selectedUnit.type !== 'EVENT') {
            let locationId = $('#locationsChoice').val();
            url += '&locationId=' + locationId;
        }
        let fromDate = $('#fromDateChoice').val();
        if (fromDate) {
            url += '&fromDate=' + fromDate;
        }
        let toDate = $('#toDateChoice').val();
        if (toDate) {
            url += '&toDate=' + toDate;
        }
        return url
    }

    function getLocations() {
        if (selectedUnit && selectedUnit.type !== 'EVENT') {
            $.ajax(
                {
                    type: "GET",
                    url: "public/api/sites/" + siteId + "/locations",
                    dataType: "json",
                    success: function (locations) {
                        buildLocationsChoice(locations);
                    },
                    error: function (response) {
                        console.log(response);
                    }
                });
        } else {
            $('#locationSelectionDiv').html('');
        }
    }

    function buildLocationsChoice(locations) {
        let locationOptions = '<label for="locationsChoice"><h5>Location:</h5></label>';
        locationOptions += '<select name="locationsChoice" id="locationsChoice" class="form-control">';
        locationOptions += '<option value=""></option>';
        for (let location of locations) {
            for (let unit of location.units) {
                if (selectedUnit.id === unit.id) {
                    locationOptions += '<option value="' + location.id + '">' + location.description + '</option>';
                }
            }
        }
        locationOptions += '</select>';
        $('#locationSelectionDiv').html(locationOptions);
    }

    function buildResultDiv(measurements) {
        let allResultData = '';
        if (measurements === undefined || measurements.length === 0) {
            allResultData += '<hr><h4>No Data Found</h4>';
        } else {
            allResultData += buildResultSection(measurements);
        }

        $('#resultDiv').html(allResultData);
    }

    function buildResultSection(measurements) {
        let resultHtml = '';
        resultHtml += '<h3>Results for ' + selectedUnit.shortDescription + ': ' + selectedUnit.longDescription + '</h3>';
        resultHtml += '<table id="resultTable" class="table table-responsive table-bordered table-striped">';
        resultHtml += buildTableHeader();
        resultHtml += buildTableBody(measurements);
        resultHtml += '</table>';
        return resultHtml
    }

    function buildTableHeader() {
        let tableHeaderHtml = '<thead class="thead-dark"><tr>';
        tableHeaderHtml += '<th>Id</th>';
        tableHeaderHtml += '<th>Collection Date</th>';
        if (selectedUnit.type !== 'EVENT') {
            tableHeaderHtml += '<th>' + selectedUnit.unitDescription + '</th>';
        }
        if (selectedUnit.enableDepth) {
            tableHeaderHtml += '<th>Depth</th>';
        }
        tableHeaderHtml += '<th>Comment</th>';
        tableHeaderHtml += '<th>Reporter</th>';
        tableHeaderHtml += '</tr></thead>';
        return tableHeaderHtml;
    }

    function buildTableBody(measurements) {
        let tableBodyHtml = '<tbody>';
        for (let measurement of measurements) {
            tableBodyHtml += '<tr>';
            tableBodyHtml += '<td>' + measurement.id + '</td>';
            tableBodyHtml += '<td>' + measurement.collectionDate + '</td>';
            if (selectedUnit.type !== 'EVENT') {
                tableBodyHtml += '<td>' + measurement.value + '</td>';
            }
            if (selectedUnit.enableDepth) {
                tableBodyHtml += '<td>' + measurement.depth + '</td>';
            }
            tableBodyHtml += '<td>' + measurement.comment + '</td>';
            tableBodyHtml += '<td>' + measurement.reporter.firstName + ' ' + measurement.reporter.lastName + '</td>';
            tableBodyHtml += '</tr>';
        }
        tableBodyHtml += '</tbody>';
        return tableBodyHtml;
    }

})
;
