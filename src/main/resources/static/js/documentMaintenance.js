$(function () {

    $("#message").text("").hide();

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
                $("#message").text("").hide();
                return $.ajax({
                    async: false,
                    type: "GET",
                    url: "/api/documents?timezone=" + timezone,
                    data: filter,
                    error: function (xhr) {
                        $("#message").text("There was an issue with your request. " + xhr.responseJSON.errorMessage).show();
                    }
                });
            },

            insertItem: function (item) {
                $("#message").text("").hide();
                let formData = new FormData();
                formData.append("siteId", item.siteId);
                formData.append("path", item.path);
                formData.append("title", item.title);
                formData.append("document", item.file);

                return $.ajax({
                    async: false,
                    method: "post",
                    type: "POST",
                    url: "/api/documents?timezone=" + timezone,
                    data: formData,
                    contentType: false,
                    processData: false,
                    error: function (xhr) {
                        $("#message").text("There was an issue with your request. " + xhr.responseJSON.errorMessage).show();
                    }
                });
            },

            updateItem: function (item) {
                $("#message").text("").hide();
                let formData = new FormData();
                formData.append("id", item.id);
                formData.append("siteId", item.siteId);
                formData.append("path", item.path);
                formData.append("title", item.title);
                if (item.file) {
                    formData.append("document", item.file);
                }

                return $.ajax({
                    async: false,
                    method: "put",
                    type: "PUT",
                    url: "/api/documents/" + item.id + "?timezone=" + timezone,
                    data: formData,
                    contentType: false,
                    processData: false,
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
                    url: "/api/documents/" + item.id,
                    error: function (xhr) {
                        $("#message").text("There was an issue with your request. " + xhr.responseJSON.errorMessage).show();
                    }
                });
            },
        },

        fields: [
            {title: "Id", name: "id", type: "number", visible: false},
            {title: "Site", name: "siteId", width: 100, type: "select", items: sites, valueField: "id", textField: "name", valueType: "number", validate: "required"},
            {title: "Path", name: "path", type: "text", width: 200, validate: "required"},
            {title: "Title", name: "title", type: "text", width: 100, validate: "required"},

            {
                title: "Document",
                name: "file",
                itemTemplate: function (value, item) {
                    return "(" + item.fileSize + " KB)";
                },
                insertTemplate: function () {
                    return this.insertControl = $("<input>").prop("type", "file");
                },
                insertValue: function () {
                    return this.insertControl[0].files[0];
                },
                editTemplate: function () {
                    return this.editControl = $("<input>").prop("type", "file");
                },
                editValue: function () {
                    return this.editControl[0].files[0];
                },
                align: "center",
                width: 150
            },

            {title: "Last Changed", name: "lastUpdated", type: "text", filtering: false, editing: false, inserting: false, width: 75},
            {type: "control"}
        ]
    });

})
;
