$(function () {
    $("#message").text("").hide();

    $.get("api/characteristicTypes").done(function (characteristicTypes) {

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
                        async: false,
                        type: "GET",
                        url: "api/characteristics",
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
                        url: "api/characteristics",
                        contentType: 'application/json; charset=UTF-8',
                        data: JSON.stringify(item),
                        error: function (xhr) {
                            $("#message").text("There was an issue with your request. " + xhr.responseJSON.errorMessage).show();
                        }
                    });
                },

                updateItem: function (item) {
                    $("#message").text("").hide();
                    return $.ajax({
                        async: false,
                        type: "PUT",
                        url: "api/characteristics/" + item.id,
                        contentType: 'application/json; charset=UTF-8',
                        data: JSON.stringify(item),
                        error: function (xhr) {
                            $("#message").text("There was an issue with your request. " + xhr.responseJSON.errorMessage).show();
                        }
                    });
                },

                deleteItem: function (item) {
                    $("#message").text("").hide();
                    return $.ajax({
                        async: false,
                        type: "DELETE",
                        url: "api/characteristics/" + item.id,
                        error: function (xhr) {
                            $("#message").text("There was an issue with your request. " + xhr.responseJSON.errorMessage).show();
                        }
                    });
                },
            },

            fields: [
                {title: "Id", name: "id", type: "number", width: 75, visible: false},
                {title: "Units", name: "unitDescription", type: "text", width: 75, validate: "required"},
                {title: "Description", name: "description", type: "text", width: 300, validate: "required"},
                {title: "Symbol", name: "shortDescription", type: "text", width: 75, validate: "required"},
                {title: "Depth Profile", name: "enableDepth", type: "checkbox", width: 75},
                {title: "Type", name: "type", validate: "required", type: "select", items: characteristicTypes, valueField: "id", textField: "name", valueType: "string"},
                {type: "control"}
            ]
        });
    });
});
