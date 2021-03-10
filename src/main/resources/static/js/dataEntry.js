$(document).ready(function () {

    let siteId;
    let characteristicId;
    let selectedCharacteristic

    $('#sitesChoice').on('change', function () {
        $("#message").text("").hide();
        siteId = $(this).val();
        if (characteristicId) {
            getLocations();
        } else {
            $('#locationSelectionDiv').html('');
        }
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
        $("#message").text("").hide();
        if (siteId && characteristicId) {
            let data = buildSubmitData();
            $.ajax(
                {
                    type: "POST",
                    url: "api/measurements",
                    contentType: 'application/json',
                    data: JSON.stringify(data),
                    success: function () {
                        alert("Your data has been saved.");
                        // $('#collectionDateField').value = '';
                        // $('#locationsChoice').value = '';
                        // $('#depthField').value = '';
                        // $('#commentField').value = '';
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
        if (selectedCharacteristic.type !== 'EVENT') {
            value = $('#valueField').val();
            locationId = $('#locationsChoice').val();
        }
        let depth;
        if (selectedCharacteristic.enableDepth) {
            depth = $('#depthField').val();
        }
        return {
            "collectionDate": $('#collectionDateField').val(),
            "value": value,
            "depth": depth,
            "comment": $('#commentField').val(),
            "locationId": locationId,
            "siteId": siteId,
            "characteristicId": characteristicId
        };
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
        let locationOptions = '<label for="locationsChoice"><h5>Location:</h5></label>';
        locationOptions += '<select name="locationsChoice" id="locationsChoice" class="form-control">';
        locationOptions += '<option value=""></option>';
        for (let location of locations) {
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
        locationOptions += '</select>';
        $('#locationSelectionDiv').html(locationOptions);
    }

    function buildValueDiv() {
        let divHtml = '';
        if (selectedCharacteristic.type !== 'EVENT') {
            divHtml = '<div class="col-sm-12">'
            divHtml += '<label for="valueField"><b>Actual Measurement Value for ';
            divHtml += selectedCharacteristic.shortDescription;
            divHtml += ' in ' + selectedCharacteristic.unitDescription + ':';
            divHtml += '</b></label>';
            divHtml += '<input id="valueField" type="number" required class="form-control">';
            divHtml += '</div>';
        }
        $('#valueDiv').html(divHtml);
    }

    function buildDepthDiv() {
        let divHtml = '';
        if (selectedCharacteristic.enableDepth) {
            divHtml = '<div class="col-sm-12">'
            divHtml += '<label for="depthField"><b>Depth of Measurement in feet:</b></label>';
            divHtml += '<input id="depthField" type="number" required  class="form-control">';
            divHtml += '</div>';
        }
        $('#depthDiv').html(divHtml);
    }

})
;
