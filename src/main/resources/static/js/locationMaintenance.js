$(function () {

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
                return $.ajax({
                    type: "GET",
                    url: "api/locations",
                    data: filter
                });
            },

            insertItem: function (item) {
                return $.ajax({
                    type: "POST",
                    url: "api/locations",
                    contentType: 'application/json',
                    data: JSON.stringify(item)
                });
            },

            updateItem: function (item) {
                return $.ajax({
                    type: "PUT",
                    url: "api/locations/" + item.id,
                    contentType: 'application/json',
                    data: JSON.stringify(item)
                });
            },

            deleteItem: function (item) {
                return $.ajax({
                    type: "DELETE",
                    url: "api/locations/" + item.id
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
