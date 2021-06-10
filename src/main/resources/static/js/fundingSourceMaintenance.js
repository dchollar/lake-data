$(function () {
    $("#message").text("").hide();

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
                    url: "public/api/fundingSources",
                    data: filter,
                    error: function (xhr) {
                        $("#message").text("There was an issue with your request. " + xhr.responseText).show();
                    }
                });
            },

            insertItem: function (item) {
                $("#message").text("").hide();
                return $.ajax({
                    type: "POST",
                    url: "api/fundingSources",
                    contentType: 'application/json',
                    data: JSON.stringify(item),
                    error: function (xhr) {
                        $("#message").text("There was an issue with your request. " + xhr.responseText).show();
                    }
                });
            },

            updateItem: function (item) {
                $("#message").text("").hide();
                return $.ajax({
                    type: "PUT",
                    url: "api/fundingSources/" + item.id,
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
                    url: "api/fundingSources/" + item.id,
                    error: function (xhr) {
                        $("#message").text("There was an issue with your request. " + xhr.responseText).show();
                    }
                });
            },
        },

        fields: [
            {title: "Id", name: "id", type: "number", visible: false},
            {title: "Name", name: "name", type: "text", width: 100, validate: "required"},
            {title: "Description", name: "description", type: "text", width: 200, validate: "required"},
            {title: "Start Date", name: "startDate", type: "text", width: 100, validate: "required"},
            {title: "End Date", name: "endDate", type: "text", width: 100, validate: "required"},
            {type: "control"}
        ]
    });

})
;
