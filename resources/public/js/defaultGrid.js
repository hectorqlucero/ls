/**
    * Created by Hector Lucero - kammy on 8/12/16.
    * clj-lhss
    */
/* Default easyui grid handlers
 * NOTE: there are some specific ids that must be used for the default grid solution
 * NOTE: grids must have the id 'datagrid-searchable'
 * NOTE: the search field on a grid must have the id 'datagrid-searchbox'
 * NOTE: the dialog containing the grid must have the id 'dlg'
 * NOTE: the form inside the dialog must have the id 'fm'
 * TODO: make this work with more than one grid/form on a page
 */

/* Variables for the default easyui grid */
var dataGrid = $('.datagrid-searchable');
var dialog = $('.dlg');
var form = $('.fm');
var windowHeight;
var token = $('#__anti-forgery-token').val();

/* Handle creating a new entry in a grid */
function defaultGridNewItem() {
    dataGrid.datagrid('unselectAll');
    // Clear the file container if anything is there from a previous edit
    $('.file-container').empty().removeAttr('style');
    // Open a new dialog, clear the contained form and center it vertically to the middle of the screen
    dialog.dialog('open').dialog('center').dialog('setTitle', 'Nuevo Record');
    form.form('clear');
    windowHeight = $(window).height() - ($(window).height() * 0.2);
    dialog.dialog('resize', {height: windowHeight}).dialog('center');

}

/* Handle editing an existing entry in a grid */
function defaultGridEditItem(params) {
    // Try to load the selected row from the grid
    var row = dataGrid.datagrid('getSelected');
    if (row) {
        try {
            // Open a dialog and center it to the middle of the screen
            dialog.dialog('open').dialog('center').dialog('setTitle', 'Editar Record');
            windowHeight = $(window).height() - ($(window).height() * 0.2);
            dialog.dialog('resize', {height: windowHeight}).dialog('center');
            // Create the request to obtain the form data from the backend code
            var id = params.hasOwnProperty('idField') ? params.idField : row.id;
            $.get(window.location.href + '/json/form/' + id, function (data) {
                // TODO: Add generic handler code to handle more than one image/file field on a form
                var filename = data['file'];
                // Check if there is a container for the file and a folder for uploads is specified
                var fileContainer = $('.file-container');
                var fileInput = fileContainer.parent().find('input[type=file]');
                fileInput.replaceWith(fileInput.clone(true));
                form.form('clear');
                if (params.hasOwnProperty('uploadFolder') && fileContainer && filename) {
                    // Clear the file container every time a form is loaded
                    fileContainer.empty().removeAttr('style');
                    // Obtain the extension and check if it's valid
                    var extension = filename
                        .split('').reverse().join('')
                        .split('.')[0]
                        .split('').reverse().join('');
                    var index = extensions.indexOf(extension);
                    if (index >= 0) {
                        // If the extension is valid find out what type of file display there should be
                        if (allowedFile.indexOf(extensions[index]) !== -1) {
                            // In the case of files create a download link that a user can click on
                            var link = $('<a></a>')
                                .attr('href', '/uploads/' + params['uploadFolder'] + '/' + filename)
                                .attr('download', filename)
                                .html(filename)
                                .css({
                                    'color': '#00BBEE',
                                    'font': '13px Arial',
                                    'display': 'inline-block',
                                    'margin-bottom': '10px',
                                    'line-height': 'normal',
                                    'vertical-align': 'middle'
                                });
                            // Display a small icon next to the download link to show there is a file
                            // TODO: Write script to generate this icon according to file extension
                            var icon = $('<img>')
                                .attr('src', '/images/icon_pdf.gif')
                                .css({
                                    'width': '16px',
                                    'height': '16px',
                                    'margin-right': '8px'
                                });
                            // Update the file container with the link and icon
                            fileContainer
                                .html(icon.add(link))
                                .css({
                                    'height': '20px',
                                    'line-height': '20px'
                                });
                        } else if (allowedImage.indexOf(extensions[index] !== -1)) {
                            // Create an image and update the file container with the created image
                            $('<img>')
                                .attr('src', '/uploads/' + params['uploadFolder'] + '/' + filename)
                                .load(function () {
                                    fileContainer.html(this);
                                    // Resize the image according to aspect ratio to fit in the maximum dimensions
                                    var max = Math.max(maxImageHeight, maxImageWidth);
                                    var ratio = Math.min(
                                        max / $(this).width(),
                                        max / $(this).height()
                                    );
                                    var imageHeight = $(this).height() * ratio;
                                    var imageWidth = $(this).width() * ratio;
                                    $(this).css({
                                        'width': imageWidth,
                                        'height': imageHeight,
                                        'display': 'block',
                                        'border-radius': '4px',
                                        'margin-bottom': '3px'
                                    });
                                });
                        }
                    }
                }
            }, 'json').always(function (data) {
                // If the data contains a file path, unset this before loading the form data
                if (data.file) delete data.file;
                form.form('load', data);
                if (params.callback) params.callback();
            });
        } catch(_) {
            // Display an error when the form is unable to load the data
            $.messager.show({
                title: 'Error',
                msg: 'No se pudieron cargar los datos!'
            });
        }
    }
}

/* Handle submitting a form in a grid */
function defaultGridSaveItem() {
    // Create the request to save the inputted data on the form
    form.form('submit', {
        url: window.location.href + '/save',
        queryParams: {
            '__anti-forgery-token': token
        },
        onSubmit: function () {
            return $(this).form('validate');
        },
        success: function (result) {
            var json = JSON.parse(result);
            // Display errors (if any)
            if (json.error && json.success) {
                // Display error on incomplete submission
                $.messager.show({
                    title: 'Error',
                    msg: json.success + "<br>" + json.error
                });
                dialog.dialog('close');
                dataGrid.datagrid('reload');
            } else if (json.error) {
                // Display error on failed submission
                $.messager.show({
                    title: 'Error',
                    msg: json.error
                });
            } else if(json.success) {
                // Display success message on complete submission
                $.messager.show({
                    title: 'Success',
                    msg: json.success
                });
                dialog.dialog('close');
                dataGrid.datagrid('reload');
            }
        }
    });
}

/* Handle deleting an item from a grid */
function defaultGridDeleteItem() {
    // Attempt to get a selected item from the grid
    var row = dataGrid.datagrid('getSelected');
    if (row) {
        // Display confirmation box to prevent users from deleting on a mis-click
        $.messager.confirm('Confirm', confirmDelete, function(r) {
            if (r) {
                // If a user confirms create the request to delete the item
                $.post(window.location.href + '/delete',
                    {id: row.id, '__anti-forgery-token': token}, function (result) {
                        if (result.success) {
                            // Reload data on successful delete to see the changes
                            $.messager.show({
                                title: 'Success',
                                msg: result.success
                            });
                            dataGrid.datagrid('reload');
                        } else {
                            // Display an error message if data was not deleted
                            $.messager.show({
                                title: 'Error',
                                msg: result.error
                            });
                        }
                    }, 'json');
            }
        });
    }
}

/* Handle closing the grid dialog */
function defaultGridDialogClose() {
    dialog.dialog('close');
}
