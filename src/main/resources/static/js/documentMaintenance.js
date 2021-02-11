$(function () {

    // TODO need to actually upload the file

    let timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
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
                    url: "api/documents?timezone=" + timezone,
                    data: filter
                });
            },

            insertItem: function (item) {
                return $.ajax({
                    type: "POST",
                    url: "api/documents?timezone=" + timezone,
                    contentType: 'application/json',
                    data: JSON.stringify(item)
                });
            },

            updateItem: function (item) {
                return $.ajax({
                    type: "PUT",
                    url: "api/documents/" + item.id +"?timezone=" + timezone,
                    contentType: 'application/json',
                    data: JSON.stringify(item)
                });
            },

            deleteItem: function (item) {
                return $.ajax({
                    type: "DELETE",
                    url: "api/documents/" + item.id
                });
            },
        },

        fields: [
            {title: "Id", name: "id", type: "number", visible: false},
            {title: "Site", name: "siteId", width: 100, type: "select", items: sites, valueField: "id", textField: "name", valueType: "number", validate: "required"},
            {title: "Path", name: "path", type: "text", width: 200, validate: "required"},
            {title: "Title", name: "title", type: "text", width: 100, validate: "required"},
            {title: "Created", name: "created", type: "text", filtering: false, editing: false, inserting: false, width: 100},
            {title: "Last Updated", name: "lastUpdated", type: "text", filtering: false, editing: false, inserting: false, width: 100},

            {type: "control"}
        ]
    });

})
;
