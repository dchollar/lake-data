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
                        buildValueDiv();
                        buildDepthDiv();
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
            let data = buildSubmitData();
            $.ajax(
                {
                    type: "POST",
                    url: "api/measurements",
                    contentType: 'application/json',
                    data: JSON.stringify(data),
                    success: function (results) {
                        alert("Your data has been saved.");
                        window.location = "dataEntry";
                    },
                    error: function (xhr, resp, text) {
                        console.log(xhr, resp, text);
                        $("#message").text("There was an issue saving your data. Check your submission. " + xhr.responseText).show();
                    }
                });
        }
    });

    function buildSubmitData() {
        let value;
        let locationId
        if (selectedUnit.type !== 'EVENT') {
            value = $('#valueField').val();
            locationId = $('#locationsChoice').val();
        }
        let depth;
        if (selectedUnit.enableDepth) {
            depth = $('#depthField').val();
        }
        return {
            "collectionDate": $('#collectionDateField').val(),
            "value": value,
            "depth": depth,
            "comment": $('#commentField').val(),
            "locationId": locationId,
            "siteId": siteId,
            "unitId": unitId
        };
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

    function buildValueDiv() {
        let divHtml = '';
        if (selectedUnit.type !== 'EVENT') {
            divHtml = '<div class="col-sm-12">'
            divHtml += '<label for="valueField"><b>Actual Measurement Value:</b></label>';
            divHtml += '<input id="valueField" type="number" required class="form-control">';
            divHtml += '</div>';
        }
        $('#valueDiv').html(divHtml);
    }

    function buildDepthDiv() {
        let divHtml = '';
        if (selectedUnit.enableDepth) {
            divHtml = '<div class="col-sm-12">'
            divHtml += '<label for="depthField"><b>Depth of Measurement:</b></label>';
            divHtml += '<input id="depthField" type="number" required  class="form-control">';
            divHtml += '</div>';
        }
        $('#depthDiv').html(divHtml);
    }

})
;
