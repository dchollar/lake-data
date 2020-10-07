$(function () {

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
                    url: "/api/reporters",
                    data: filter
                });
            },

            insertItem: function (item) {
                return $.ajax({
                    type: "POST",
                    url: "/api/reporters",
                    contentType: 'application/json',
                    data: JSON.stringify(item)
                });
            },

            updateItem: function (item) {
                return $.ajax({
                    type: "PUT",
                    url: "/api/reporters/" + item.id,
                    contentType: 'application/json',
                    data: JSON.stringify(item)
                });
            },

            deleteItem: function (item) {
                return $.ajax({
                    type: "DELETE",
                    url: "/api/reporters/" + item.id
                });
            },
        },

        fields: [
            {title: "Id", name: "id", type: "number", visible: false},
            {title: "First Name", name: "firstName", type: "text", width: 100, validate: "required"},
            {title: "Last Name", name: "lastName", type: "text", width: 100, validate: "required"},
            {title: "Email Address", name: "emailAddress", type: "text", width: 300, validate: "required"},
            {title: "Phone Number", name: "phoneNumber", type: "text", width: 150, validate: "required"},
            {title: "User Name", name: "username", type: "text", width: 100, validate: "required"},
            {title: "Password", name: "password", type: "text", width: 100},
            {title: "Enabled", name: "enabled", type: "checkbox", width: 100},
            {title: "ROLE REPORTER", name: "roleReporter", type: "checkbox", width: 100},
            {title: "ROLE POWER USER", name: "rolePowerUser", type: "checkbox", width: 100},
            {title: "ROLE ADMIN", name: "roleAdmin", type: "checkbox", width: 100},
            {type: "control"}
        ]
    });

})
;
