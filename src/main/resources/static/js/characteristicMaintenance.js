$(function () {

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
                    return $.ajax({
                        type: "GET",
                        url: "api/characteristics",
                        data: filter
                    });
                },

                insertItem: function (item) {
                    return $.ajax({
                        type: "POST",
                        url: "api/characteristics",
                        contentType: 'application/json',
                        data: JSON.stringify(item)
                    });
                },

                updateItem: function (item) {
                    return $.ajax({
                        type: "PUT",
                        url: "api/characteristics/" + item.id,
                        contentType: 'application/json',
                        data: JSON.stringify(item)
                    });
                },

                deleteItem: function (item) {
                    return $.ajax({
                        type: "DELETE",
                        url: "api/characteristics/" + item.id
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
