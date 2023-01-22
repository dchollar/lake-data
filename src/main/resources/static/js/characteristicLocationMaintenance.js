$(function () {
    $("#message").text("").hide();

    let characteristics = JSON.parse($('#characteristicOptions').val());
    let locations = JSON.parse($('#locationOptions').val());
    let sites = JSON.parse($('#siteOptions').val());

    $("#jsGrid").jsGrid({

        height: "auto",
        width: "100%",

        filtering: false,
        editing: false,
        inserting: true,
        sorting: true,
        paging: false,
        autoload: true,

        controller: {
            loadData: function (filter) {
                $("#message").text("").hide();
                return $.ajax({
                    async: false,
                    type: "GET",
                    url: "api/characteristicLocations",
                    data: filter,
                    error: function (xhr) {
                        $("#message").text("There was an issue with your request. " + xhr.responseJSON.errorMessage).show();
                    }
                });
            },

            insertItem: function (item) {
                $("#message").text("").hide();
                return $.ajax({
                    async: false,
                    type: "POST",
                    url: "api/characteristicLocations",
                    contentType: 'application/json; charset=UTF-8',
                    data: JSON.stringify(item),
                    error: function (xhr) {
                        $("#message").text("There was an issue with your request. " + xhr.responseJSON.errorMessage).show();
                    }
                });
            },

            updateItem: $.noop,

            deleteItem: function (item) {
                $("#message").text("").hide();
                return $.ajax({
                    async: false,
                    type: "DELETE",
                    url: "api/characteristicLocations/" + item.id,
                    error: function (xhr) {
                        $("#message").text("There was an issue with your request. " + xhr.responseJSON.errorMessage).show();
                    }
                });
            },
        },

        fields: [
            {title: "Id", name: "id", type: "number", visible: false},
            {title: "Site", name: "siteId", editing: false, width: 150, type: "select", items: sites, valueField: "id", textField: "name", valueType: "number"},
            {title: "Location", name: "locationId", editing: false, width: 150, type: "select", items: locations, valueField: "id", textField: "name", valueType: "number"},
            {
                title: "Characteristic",
                name: "characteristicId",
                editing: false,
                width: 150,
                type: "select",
                items: characteristics,
                valueField: "id",
                textField: "name",
                valueType: "number"
            },

            {type: "control"}
        ]
    });

})
;
