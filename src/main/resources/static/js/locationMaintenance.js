$(function () {
    $("#message").text("").hide();

    let sites = JSON.parse($('#siteOptions').val());

    $("#jsGrid").jsGrid({

        height: "auto",
        width: "100%",

        filtering: false,
        editing: true,
        inserting: true,
        sorting: true,
        paging: false,
        autoload: true,

        controller: {
            loadData: function (filter) {
                $("#message").text("").hide();
                return $.ajax({
                    type: "GET",
                    url: "api/locations",
                    data: filter,
                    error: function (xhr, resp, text) {
                        $("#message").text("There was an issue with your request. " + xhr.responseText).show();
                    }
                });
            },

            insertItem: function (item) {
                $("#message").text("").hide();
                return $.ajax({
                    type: "POST",
                    url: "api/locations",
                    contentType: 'application/json',
                    data: JSON.stringify(item),
                    error: function (xhr, resp, text) {
                        $("#message").text("There was an issue with your request. " + xhr.responseText).show();
                    }
                });
            },

            updateItem: function (item) {
                $("#message").text("").hide();
                return $.ajax({
                    type: "PUT",
                    url: "api/locations/" + item.id,
                    contentType: 'application/json',
                    data: JSON.stringify(item),
                    error: function (xhr, resp, text) {
                        $("#message").text("There was an issue with your request. " + xhr.responseText).show();
                    }
                });
            },

            deleteItem: function (item) {
                $("#message").text("").hide();
                return $.ajax({
                    type: "DELETE",
                    url: "api/locations/" + item.id,
                    error: function (xhr, resp, text) {
                        $("#message").text("There was an issue with your request. " + xhr.responseText).show();
                    }
                });
            },
        },

        fields: [
            {title: "Id", name: "id", type: "number", visible: false},
            {title: "Description", name: "description", type: "text", width: 200, validate: "required"},
            {title: "Comment", name: "comment", type: "text", width: 200},
            {title: "Site", name: "siteId", editing: false, width: 150, type: "select", items: sites, valueField: "id", textField: "name", valueType: "number"},
            {type: "control"}
        ]
    });

})
;
