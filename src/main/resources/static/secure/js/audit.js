$(function () {

    const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
    const baseUri = location.href.substring(0, location.href.indexOf('page/'));

    $("#jsGrid").jsGrid({

        height: "auto",
        width: "100%",

        filtering: true,
        editing: false,
        inserting: false,
        sorting: true,
        paging: true,
        autoload: true,

        pageSize: 15,
        pageButtonCount: 5,

        controller: {
            loadData: function (filter) {
                return $.ajax({
                    async: false,
                    type: "GET",
                    url: baseUri + "api/audits?timezone=" + timezone,
                    contentType: 'application/json; charset=UTF-8',
                    data: filter
                });
            },
            insertItem: $.noop,
            updateItem: $.noop,
            deleteItem: $.noop,
        },

        fields: [
            {title: "Id", name: "id", type: "number", visible: false, filtering: false, width: 50},
            {title: "Time", name: "created", type: "text", filtering: false, width: 150},
            {title: "HTTP Method", name: "httpMethod", type: "text", width: 50},
            {title: "End Point", name: "endpoint", type: "text", width: 200},
            {title: "Controller", name: "controller", type: "text", width: 150},
            {title: "Reporter Name", name: "reporterName", type: "text", width: 150},
        ]
    });

})
;
