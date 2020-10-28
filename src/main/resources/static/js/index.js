$(document).ready(function () {

    let siteId;
    let unitId;
    let selectedUnit
    let chart

    $('#sitesChoice').on('change', function () {
        $("#message").text("").hide();
        siteId = $(this).val();
        unitId = undefined;
        getUnits();
        $('#locationSelectionDiv').html('');
    });

    $('#unitsChoice').on('change', function () {
        $("#message").text("").hide();
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
        $("#message").text("").hide();
        if (siteId && unitId) {
            let url = buildSubmitUrl();
            $.ajax(
                {
                    type: "GET",
                    url: url,
                    dataType: "json",
                    success: function (measurements) {
                        buildResultDiv(measurements);
                        buildChartDiv(measurements);
                    },
                    error: function (xhr, resp, text) {
                        console.log(xhr, resp, text);
                        $("#message").text("There was an issue with your request. " + xhr.responseText).show();
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

    function getUnits() {
        $.ajax(
            {
                type: "GET",
                url: "public/api/sites/" + siteId + "/units",
                dataType: "json",
                success: function (units) {
                    buildUnitsChoice(units);
                },
                error: function (response) {
                    console.log(response);
                }
            });
    }

    function buildUnitsChoice(units) {
        let unitOptions = '<option value=""></option>';
        for (let unit of units) {
            unitOptions += '<option value="' + unit.id + '">' + unit.longDescription + '</option>';
        }
        $('#unitsChoice').html(unitOptions);
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
        let locationOptions = '<h5><label for="locationsChoice">Location:</label></h5>';
        locationOptions += '<select name="locationsChoice" id="locationsChoice" class="form-control">';
        locationOptions += '<option value=""></option>';
        for (let location of locations) {
            for (let unit of location.units) {
                if (selectedUnit.id === unit.id) {
                    locationOptions += '<option value="';
                    locationOptions += location.id;
                    if (location.comment) {
                        locationOptions += '" title="';
                        locationOptions += location.comment;
                    }
                    locationOptions += '">'
                    locationOptions += location.description;
                    locationOptions += '</option>';
                }
            }
        }
        locationOptions += '</select>';
        $('#locationSelectionDiv').html(locationOptions);
    }

    function buildResultDiv(measurements) {
        let html = '';
        if (measurements === undefined || measurements.length === 0) {
            html += '<h4>No Data Found</h4>';
        } else {
            html += buildResultSection(measurements);
        }

        $('#resultDiv').html(html);
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

    function buildChartDiv(measurements) {
        if (chart) {
            chart.destroy()
        }
        if (measurements === undefined || measurements.length === 0) {
            $('#resultDiv').html('<h4>No Data Found</h4>');
        } else {
            let data = [];
            let labels = [];
            for (let measurement of measurements) {
                if (selectedUnit.enableDepth) {
                    labels.push(measurement.collectionDate + ' | ' + measurement.depth);
                } else {
                    labels.push(measurement.collectionDate);
                }
                if (selectedUnit.type === 'EVENT') {
                    data.push(measurement.dayOfYear);
                } else {
                    data.push(measurement.value);
                }
            }
            buildChartSection(data, labels);
        }
    }

    function buildChartSection(data, labels) {
        let label = selectedUnit.shortDescription + ' ' + selectedUnit.longDescription + ' ' + selectedUnit.unitDescription;
        let ctx = document.getElementById('myChart').getContext('2d');
        chart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: label,
                    data: data
                }]
            },
            options: {}
        });
    }

    function buildTableHeader() {
        let tableHeaderHtml = '<thead class="thead-dark"><tr>';
        tableHeaderHtml += '<th>Id</th>';
        tableHeaderHtml += '<th>Collection Date</th>';
        if (selectedUnit.enableDepth) {
            tableHeaderHtml += '<th>Depth</th>';
        }
        if (selectedUnit.type !== 'EVENT') {
            tableHeaderHtml += '<th>' + selectedUnit.unitDescription + '</th>';
        }
        tableHeaderHtml += '<th>Comment</th>';
        //tableHeaderHtml += '<th>Reporter</th>';
        tableHeaderHtml += '</tr></thead>';
        return tableHeaderHtml;
    }

    function buildTableBody(measurements) {
        let tableBodyHtml = '<tbody>';
        for (let measurement of measurements) {
            tableBodyHtml += '<tr>';
            tableBodyHtml += '<td>' + measurement.id + '</td>';
            tableBodyHtml += '<td>' + measurement.collectionDate + '</td>';
            if (selectedUnit.enableDepth) {
                tableBodyHtml += '<td>' + measurement.depth + '</td>';
            }
            if (selectedUnit.type !== 'EVENT') {
                tableBodyHtml += '<td>' + measurement.value + '</td>';
            }
            tableBodyHtml += '<td>' + measurement.comment + '</td>';
            //tableBodyHtml += '<td>' + measurement.reporter.firstName + ' ' + measurement.reporter.lastName + '</td>';
            tableBodyHtml += '</tr>';
        }
        tableBodyHtml += '</tbody>';

        return tableBodyHtml;
    }

})
;
