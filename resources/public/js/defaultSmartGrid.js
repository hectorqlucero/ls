/**
 * Created by kammy on 8/23/16.
 */

$(document).ready(function () {
    var dataGridOptions = dataGrid.datagrid('options');
    dataGridOptions.view = detailview;
    dataGridOptions.detailFormatter = function () {
        return '<table class="ddv"></table>';
    };
    dataGridOptions.onExpandRow = function (index, row) {
        var ddv = $(this).datagrid('getRowDetail', index).find('table.ddv');
        ddv.datagrid({
            queryParams: {
                '__anti-forgery-token': token,
                'template_id': row.id
            },
            columns: [[
                {field: 'ck', checkbox: true},
                {field: 'description', title: 'Description', width: 50},
                {field: 'datetime', title: 'Date Time', width: 50}
            ]],
            fitColumns: true,
            singleSelect: true,
            data: [
                {description: "Another One", datetime: "2016-02-03 03:44:33"},
                {description: "Yet Another One", datetime: "2016-02-03 03:44:33"}
            ],
            toolbar: [
                {
                    iconCls: 'icon-edit',
                    text: 'testing',
                    handler: function() {
                        alert('edit');
                    }
                },
                {
                    iconCls: 'icon-help',
                    handler: function() {
                        alert('help');
                    }
                }
            ]
        });
        dataGrid.datagrid('fixDetailRowHeight', index);
    };
    dataGrid.datagrid(dataGridOptions);
});
