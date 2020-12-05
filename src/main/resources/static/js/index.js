$(document).ready(function () {

    let siteId;
    let characteristicId;
    let selectedCharacteristic
    let chart

    $('#sitesChoice').on('change', function () {
        $("#message").text("").hide();
        siteId = $(this).val();
        characteristicId = undefined;
        getCharacteristics();
        $('#locationSelectionDiv').html('');
    });

    $('#characteristicsChoice').on('change', function () {
        $("#message").text("").hide();
        characteristicId = $(this).val();
        $.ajax(
            {
                type: "GET",
                url: "public/api/characteristics/" + characteristicId,
                dataType: "json",
                success: function (characteristic) {
                    selectedCharacteristic = characteristic;
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
        if (siteId && characteristicId) {
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
        let url = "public/api/measurements?siteId=" + siteId + "&characteristicId=" + characteristicId;
        if (selectedCharacteristic && selectedCharacteristic.type !== 'EVENT') {
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

    function getCharacteristics() {
        $.ajax(
            {
                type: "GET",
                url: "public/api/sites/" + siteId + "/characteristics",
                dataType: "json",
                success: function (characteristics) {
                    buildCharacteristicsChoice(characteristics);
                },
                error: function (response) {
                    console.log(response);
                }
            });
    }

    function buildCharacteristicsChoice(characteristics) {
        let options = '<option value=""></option>';
        for (let characteristic of characteristics) {
            options += '<option value="' + characteristic.id + '">' + characteristic.description + '</option>';
        }
        $('#characteristicsChoice').html(options);
    }

    function getLocations() {
        if (selectedCharacteristic && selectedCharacteristic.type !== 'EVENT') {
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
            for (let characteristic of location.characteristics) {
                if (selectedCharacteristic.id === characteristic.id) {
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
        resultHtml += '<h3>Results for ' + selectedCharacteristic.shortDescription + ': ' + selectedCharacteristic.description + '</h3>';
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
                if (selectedCharacteristic.enableDepth) {
                    labels.push(measurement.collectionDate + ' | ' + measurement.depth);
                } else {
                    labels.push(measurement.collectionDate);
                }
                if (selectedCharacteristic.type === 'EVENT') {
                    data.push(measurement.dayOfYear);
                } else {
                    data.push(measurement.value);
                }
            }
            buildChartSection(data, labels);
        }
    }

    function buildChartSection(data, labels) {
        let label = selectedCharacteristic.shortDescription + ' ' + selectedCharacteristic.description + ' ' + selectedCharacteristic.unitDescription;
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
        tableHeaderHtml += '<th>Collection Date</th>';
        if (selectedCharacteristic.enableDepth) {
            tableHeaderHtml += '<th>Depth</th>';
        }
        if (selectedCharacteristic.type !== 'EVENT') {
            tableHeaderHtml += '<th>' + selectedCharacteristic.unitDescription + '</th>';
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
            tableBodyHtml += '<td>' + measurement.collectionDate + '</td>';
            if (selectedCharacteristic.enableDepth) {
                tableBodyHtml += '<td>' + measurement.depth + '</td>';
            }
            if (selectedCharacteristic.type !== 'EVENT') {
                tableBodyHtml += '<td>' + measurement.value + '</td>';
            }
            tableBodyHtml += '<td>' + measurement.comment + '</td>';
            tableBodyHtml += '</tr>';
        }
        tableBodyHtml += '</tbody>';

        return tableBodyHtml;
    }

})
;
