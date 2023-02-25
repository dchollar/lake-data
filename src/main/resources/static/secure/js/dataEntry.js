let siteId;
let characteristicId;
let selectedCharacteristic;
const baseUri = location.href.substring(0,location.href.indexOf('page/'));

$(document).ready(function () {

    $('#sitesChoice').on('change', function () {
        $("#message").text("").hide();
        siteId = $(this).val();
        characteristicId = undefined;
        selectedCharacteristic = undefined;
        $('#locationSelectionDiv').html('');
        getCharacteristics();
    });

    $('#characteristicsChoice').on('change', function () {
        $("#message").text("").hide();
        characteristicId = $(this).val();
        $.ajax({
            async: false,
            type: "GET",
            url: baseUri + "public/api/characteristics/" + characteristicId,
            dataType: "json",
            success: function (characteristic) {
                selectedCharacteristic = characteristic;
                if (selectedCharacteristic && siteId) {
                    getLocations();
                    buildValueDiv();
                    buildDepthDiv();
                } else {
                    $('#locationSelectionDiv').html('');
                }
            },
            error: function (response) {
                console.log(response);
                $('#locationSelectionDiv').html('');
            }
        });
    });

    $('#submitButton').on('click', function () {
        $("#message").text("").hide();
        if (siteId && characteristicId) {
            let data = buildSubmitData();
            $.ajax({
                async: false,
                type: "POST",
                url: baseUri + "api/measurements",
                contentType: 'application/json; charset=UTF-8',
                data: JSON.stringify(data),
                success: function () {
                    alert("Your data has been saved.");
                    window.location = "dataEntry";
                },
                error: function (xhr) {
                    $("#message").text("There was an issue saving your data. Check your submission. " + xhr.responseJSON.errorMessage).show();
                }
            });
        }
    });
})
;

function buildSubmitData() {
    let value;
    let locationId;
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
        "characteristicId": characteristicId,
        "fundingSourceId": $('#fundingSourceChoice').val(),
    };
}

function getCharacteristics() {
    if (siteId) {
        $.ajax({
            async: false,
            type: "GET",
            url: baseUri + "api/characteristics?siteId=" + siteId,
            dataType: "json",
            success: function (characteristics) {
                buildCharacteristicsChoice(characteristics);
            },
            error: function (response) {
                console.log(response);
            }
        });
    } else {
        document.getElementById('characteristicsChoice').innerHTML = '';
    }
}

function buildCharacteristicsChoice(characteristics) {
    let options = '<option value=""></option>';
    for (let characteristic of characteristics) {
        options += '<option value="' + characteristic.id + '">' + characteristic.description + '</option>';
    }
    document.getElementById('characteristicsChoice').innerHTML = options;
}

function getLocations() {
    if (siteId && selectedCharacteristic && selectedCharacteristic.type !== 'EVENT') {
        $.ajax({
            async: false,
            type: "GET",
            url: baseUri + "public/api/sites/" + siteId + "/locations?characteristicId=" + characteristicId,
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
        locationOptions += '">';
        locationOptions += location.description;
        locationOptions += '</option>';
    }
    locationOptions += '</select>';
    $('#locationSelectionDiv').html(locationOptions);
}

function buildValueDiv() {
    let divHtml = '';
    if (selectedCharacteristic.type !== 'EVENT') {
        divHtml = '<div class="col-sm-12">';
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
        divHtml = '<div class="col-sm-12">';
        divHtml += '<label for="depthField"><b>Depth of Measurement in feet:</b></label>';
        divHtml += '<input id="depthField" type="number" required  class="form-control">';
        divHtml += '</div>';
    }
    $('#depthDiv').html(divHtml);
}