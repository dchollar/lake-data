$(function () {

    let units = JSON.parse($('#unitOptions').val());
    let locations = JSON.parse($('#locationOptions').val());
    let sites = JSON.parse($('#siteOptions').val());

    // TODO get the date picker figured out
    // need to style the date picker. need to accept a filter value of from instead of collectionDate.
    var DateField = function (config) {
        jsGrid.Field.call(this, config);
    };

    DateField.prototype = new jsGrid.Field({
        sorter: function (date1, date2) {
            return new Date(date1) - new Date(date2);
        },

        itemTemplate: function (value) {
            return new Date(value).toDateString();
        },

        filterTemplate: function () {
            var now = new Date();
            this._fromPicker = $("<input>").datepicker({defaultDate: now.setFullYear(now.getFullYear() - 1)});
            return $("<div>").append(this._fromPicker);
        },

        insertTemplate: function (value) {
            return this._insertPicker = $("<input>").datepicker({defaultDate: new Date()});
        },

        editTemplate: function (value) {
            return this._editPicker = $("<input>").datepicker().datepicker("setDate", new Date(value));
        },

        insertValue: function () {
            return this._insertPicker.datepicker("getDate").toDateString();
        },

        editValue: function () {
            return this._editPicker.datepicker("getDate").toDateString();
        },

        filterValue: function () {
            return {
                from: this._fromPicker.datepicker("getDate").toDateString(),
            };
        }
    });

    jsGrid.fields.date = DateField;

    $("#jsGrid").jsGrid({

        height: "auto",
        width: "100%",

        filtering: true,
        editing: true,
        inserting: false,
        sorting: true,
        paging: true,
        autoload: true,

        pageSize: 15,
        pageButtonCount: 5,

        controller: {
            loadData: function (filter) {
                return $.ajax({
                    type: "GET",
                    url: "api/measurements",
                    data: filter
                });
            },

            insertItem: $.noop, // this is handled by a different page

            updateItem: function (item) {
                return $.ajax({
                    type: "PUT",
                    url: "api/measurements/" + item.id,
                    contentType: 'application/json',
                    data: JSON.stringify(item)
                });
            },

            deleteItem: function (item) {
                return $.ajax({
                    type: "DELETE",
                    url: "api/measurements/" + item.id + "?unitType=" + item.unitType
                });
            },
        },

        fields: [
            {title: "Id", name: "id", type: "number", visible: false},
            {title: "Type", name: "unitType", type: "text", visible: false},
            {title: "Site", name: "siteId", editing: false, width: 150, type: "select", items: sites, valueField: "id", textField: "name", valueType: "number"},
            {title: "Location", name: "locationId", editing: false, width: 150, type: "select", items: locations, valueField: "id", textField: "name", valueType: "number"},
            {title: "Characteristic", name: "unitId", editing: false, width: 100, type: "select", items: units, valueField: "id", textField: "name", valueType: "number"},
            {title: "Collection Date", name: "collectionDate", validate: "required", type: "text", width: 75}, // type: "date"
            {title: "Depth", name: "depth", filtering: false, type: "number", width: 50},
            {title: "Value", name: "value", filtering: false, type: "number", width: 50},
            {title: "Comment", name: "comment", filtering: false, type: "textarea", width: 150},
            {type: "control"}
        ]
    });
});
