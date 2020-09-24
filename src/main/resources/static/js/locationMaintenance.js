$(document).ready(function () {

    let siteId;
    let locations;

    $('#sitesChoice').on('change', function () {
        siteId = $(this).val();
        getLocations();
    });

    $('#submitButton').on('click', function (event) {
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
                    error: function (response) {
                        console.log(response);
                    }
                });
        }
    });

    function getLocations() {
        if (selectedUnit && selectedUnit.type !== 'EVENT') {
            $.ajax(
                {
                    type: "GET",
                    url: "public/api/sites/" + siteId + "/locations",
                    dataType: "json",
                    success: function (locationsForSite) {
                        locations = locationsForSite;
                        // TODO build the ResultsDiv
                    },
                    error: function (response) {
                        console.log(response);
                    }
                });
        } else {
            $('#locationSelectionDiv').html('');
        }
    }



})
;
