$(function () {
    $("#message").text("").hide();

    const baseUri = location.href.substring(0,location.href.indexOf('page/'));

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
                    url: baseUri + "api/reporters",
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
                    url: baseUri + "api/reporters",
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
                    url: baseUri + "api/reporters/" + item.id,
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
                    url: baseUri + "api/reporters/" + item.id,
                    error: function (xhr) {
                        $("#message").text("There was an issue with your request. " + xhr.responseJSON.errorMessage).show();
                    }
                });
            },
        },

        fields: [
            {title: "Id", name: "id", type: "number", visible: false},
            {title: "First Name", name: "firstName", type: "text", width: 100, validate: "required"},
            {title: "Last Name", name: "lastName", type: "text", width: 100, validate: "required"},
            {title: "Email Address", name: "emailAddress", type: "text", width: 300, validate: "required"},
            {title: "User Name", name: "username", type: "text", width: 100, validate: "required"},
            {title: "Password", name: "password", type: "text", width: 100},
            {title: "Enabled", name: "enabled", type: "checkbox", width: 100},
            {title: "ROLE REPORTER", name: "roleReporter", type: "checkbox", width: 100},
            {title: "ROLE DOCUMENT ADMIN", name: "roleDocumentAdmin", type: "checkbox", width: 100},
            {title: "ROLE SYSTEM ADMIN", name: "roleAdmin", type: "checkbox", width: 100},
            {type: "control"}
        ]
    });

})
;
