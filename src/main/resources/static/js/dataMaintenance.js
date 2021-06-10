$(function () {
    $("#message").text("").hide();

    let characteristics = JSON.parse($('#characteristicOptions').val());
    let locations = JSON.parse($('#locationOptions').val());
    let sites = JSON.parse($('#siteOptions').val());
    let fundingSources = JSON.parse($('#fundingSourceOptions').val());

    $("#jsGrid").jsGrid({

        height: "auto",
        width: "100%",

        filtering: true,
        editing: true,
        inserting: false,
        sorting: true,
        paging: true,
        autoload: true,

        pageSize: 15,
        pageButtonCount: 5,

        controller: {
            loadData: function (filter) {
                $("#message").text("").hide();
                return $.ajax({
                    type: "GET",
                    url: "api/measurements",
                    data: filter,
                    error: function (xhr) {
                        $("#message").text("There was an issue with your request. " + xhr.responseText).show();
                    }
                });
            },

            insertItem: $.noop, // this is handled by a different page

            updateItem: function (item) {
                $("#message").text("").hide();
                return $.ajax({
                    type: "PUT",
                    url: "api/measurements/" + item.id,
                    contentType: 'application/json',
                    data: JSON.stringify(item),
                    error: function (xhr) {
                        $("#message").text("There was an issue with your request. " + xhr.responseText).show();
                    }
                });
            },

            deleteItem: function (item) {
                $("#message").text("").hide();
                return $.ajax({
                    type: "DELETE",
                    url: "api/measurements/" + item.id + "?characteristicType=" + item.characteristicType,
                    error: function (xhr) {
                        $("#message").text("There was an issue with your request. " + xhr.responseText).show();
                    }
                });
            },
        },

        fields: [
            {title: "Id", name: "id", type: "number", visible: false},
            {title: "Type", name: "characteristicType", type: "text", visible: false},
            {title: "Site", name: "siteId", editing: false, width: 150, type: "select", items: sites, valueField: "id", textField: "name", valueType: "number"},
            {title: "Location", name: "locationId", editing: false, width: 150, type: "select", items: locations, valueField: "id", textField: "name", valueType: "number"},
            {
                title: "Characteristic",
                name: "characteristicId",
                editing: false,
                width: 100,
                type: "select",
                items: characteristics,
                valueField: "id",
                textField: "name",
                valueType: "number"
            },
            {title: "Funding", name: "fundingSourceId", editing: true, width: 110, type: "select", items: fundingSources, valueField: "id", textField: "name", valueType: "number"},
            {title: "Collection Date", name: "collectionDate", validate: "required", type: "text", width: 75},
            {title: "Depth", name: "depth", filtering: false, type: "number", width: 50},
            {title: "Value", name: "value", filtering: false, type: "number", width: 50},
            {title: "Comment", name: "comment", filtering: false, type: "textarea", width: 150},
            {title: "Reporter", name: "reporterName", editing: false, filtering: true, type: "textarea", width: 100},
            {type: "control"}
        ]
    });
});
