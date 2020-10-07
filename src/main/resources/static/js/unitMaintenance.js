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
        inserting: true,
        sorting: true,
        paging: false,
        autoload: true,

        controller: {
            loadData: function (filter) {
                return $.ajax({
                    type: "GET",
                    url: "/api/units",
                    data: filter
                });
            },

            insertItem: function (item) {
                return $.ajax({
                    type: "POST",
                    url: "/api/units",
                    contentType: 'application/json',
                    data: JSON.stringify(item)
                });
            },

            updateItem: function (item) {
                return $.ajax({
                    type: "PUT",
                    url: "/api/units/" + item.id,
                    contentType: 'application/json',
                    data: JSON.stringify(item)
                });
            },

            deleteItem: function (item) {
                return $.ajax({
                    type: "DELETE",
                    url: "/api/units/" + item.id
                });
            },
        },

        fields: [
            {title: "Id", name: "id", type: "number", width: 75, visible: false},
            {title: "Unit Description", name: "unitDescription", type: "text", width: 75, validate: "required"},
            {title: "Long Description", name: "longDescription", type: "text", width: 300, validate: "required"},
            {title: "Short Description", name: "shortDescription", type: "text", width: 75, validate: "required"},
            {title: "Enable Depth", name: "enableDepth", type: "checkbox", width: 75},
            {title: "Type", name: "type", validate: "required", type: "select", items: unitTypes, valueField: "id", textField: "name", valueType: "string"},
            {type: "control"}
        ]
    });

})
;
