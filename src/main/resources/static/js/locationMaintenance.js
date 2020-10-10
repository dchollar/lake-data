$(function () {

    let unitTypes = [
        {name: "", id: "0"},
        {name: "WATER", id: "WATER"},
        {name: "EVENT", id: "EVENT"}
    ];

    $("#jsGrid").jsGrid({

        height: "auto",
        width: "100%",

        filtering: false,
        editing: true,
        inserting: false, //TODO be able to insert new ones
        sorting: true,
        paging: false,
        autoload: true,

        controller: {
            loadData: function (filter) {
                // getSites()
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
            {title: "Site Id", name: "siteId", type: "number", visible: false},
            {title: "Site", name: "siteDescription", type: "text", width: 200, editing: false},
            {type: "control"}
        ]
    });

})
;
