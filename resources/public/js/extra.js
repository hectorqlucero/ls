/**
**
 * Created by hector on 5/24/16 2:09 PM.
 * clj-lhss
 */

/* Global Constants */
const extensions = ["pdf", "jpg", "jpeg", "png", "gif"];
const allowedImage = ["jpg", "jpeg", "png", "gif"];
const allowedFile = ["pdf"];
const confirmDelete = "Esta seguro que quiere remover este record? Esta accion no se puede revertir.";
const maxImageHeight = 128;
const maxImageWidth = 128;

function menu() {
    var menuIndex = parseInt(sessionStorage.getItem("calls-menu-index")) || 0;
    $("#menu-accordion").accordion({
        selected: menuIndex
    });
}
//noinspection JSUnusedGlobalSymbols
function openPanel() {
    var selected = $("#menu-accordion").accordion("getSelected");
    selected.find(".in-menu").each(function () {
        $(this).linkbutton();
        $(this).fadeIn(this, 100);
    });
    saveStateMenu(selected);
}
function saveStateMenu(option) {
    if (option) {
        var index = $("#menu-accordion").accordion("getPanelIndex", option);
        sessionStorage.setItem("calls-menu-index", index);
    }
}
function parseTime(s) {
    var part = s.match(/(\d+):(\d+)(am|pm)?/i);
    var hh = parseInt(part[1], 10);
    var mm = parseInt(part[2], 10);
    var ap = part[3] ? part[3].toUpperCase() : null;
    if (ap == 'AM') {
        if (hh == 12) {
            hh = 0;
        }
    }
    if (ap == 'PM') {
        if (hh != 12) {
            hh += 12;
        }
    }
    return ((hh < 10 ? '0' : null) + hh) + ':' + mm;
}
//noinspection JSUnusedGlobalSymbols
var token = $('#__anti-forgery-token').val();
$(document).ready(function () {
    /* Preform search on datagrid, the grid must have the id 'datagrid-search' */
    var t = $('#datagrid-searchbox');
    var timeout, searchData;
    var searchableDataGrid = $('.datagrid-searchable');
    try {
        /* Try to find the stored search terms (GLOBAL) */
        searchData = sessionStorage.getItem('searchData');
        if (searchData) {
            var data = JSON.parse(searchData);
            for (var i = 0; i < data.length; i++) {
                // Look for a match in the browser's current URL
                if (data[i]['url'] === window.location.href) {
                    // Set the value of the search box, and re-run the request to obtain the grid data
                    t.textbox('setText', data[i]['val']).textbox('setValue', data[i]['val']);
                    var gridData = {'search': data[i]['val'], '__anti-forgery-token': token};
                    // Add any extra parameters to the grid array
                    //noinspection JSUnresolvedVariable
                    if (typeof extraParams !== 'undefined') {
                        //noinspection JSUnresolvedVariable
                        for (var key in extraParams) {
                            //noinspection JSUnresolvedVariable
                            if (extraParams.hasOwnProperty(key)) {
                                //noinspection JSUnresolvedVariable
                                gridData[key] = extraParams[key];
                            }
                        }
                    }
                    searchableDataGrid.datagrid('load', gridData);
                    break;
                }
            }
        }
        /* Handler for inputs on grids that have the search function */
        t.textbox('textbox').on('input', function () {
            // Clear existing timeouts to add a delay to the search request and typing
            clearTimeout(timeout);
            timeout = setTimeout(function () {
                /* Save state of the search box in sessionStorage */
                var value = t.textbox('getText');
                searchData = {
                    url: window.location.href,
                    val: value
                };
                if (!sessionStorage.getItem('searchData')) {
                    // If we have an empty session create a new JSON string with that search term
                    sessionStorage.setItem('searchData', JSON.stringify([searchData]));
                } else {
                    // If the session is not empty update or append the search term to the JSON string
                    var data = JSON.parse(sessionStorage.getItem('searchData'));
                    var results = [],
                        found = false;
                    for (var i = 0; i < data.length; i++) {
                        if (data[i]['url'] === window.location.href) {
                            results.push({
                                url: data[i]['url'],
                                val: value
                            });
                            found = i;
                        } else {
                            results.push({
                                url: data[i]['url'],
                                val: data[i]['val']
                            });
                        }
                    }
                    if (!found) {
                        results.push({
                            url: window.location.href,
                            val: value
                        });
                    }
                    sessionStorage.setItem('searchData', JSON.stringify(results));
                }
                var gridData = {'search': value, '__anti-forgery-token': token};
                // Add any extra parameters to the grid array
                //noinspection JSUnresolvedVariable
                if (typeof extraParams !== 'undefined') {
                    //noinspection JSUnresolvedVariable
                    for (var key in extraParams) {
                        //noinspection JSUnresolvedVariable
                        if (extraParams.hasOwnProperty(key)) {
                            //noinspection JSUnresolvedVariable
                            gridData[key] = extraParams[key];
                        }
                    }
                }
                // Reload the datagrid with a new request asking for the search term
                searchableDataGrid.datagrid('load', gridData);
            }, 350);
        });
    } catch (e) {
        //noinspection JSAnnotator
        delete t;
    }

    /* Masks for various field types */
    var phoneFields = $('.type-phone');
    phoneFields.each(function () {
        var textBox = $(this).textbox('textbox');
        textBox.mask("(999) 999-9999");
    });
    var pextFields = $('.type-phone-ext');
    pextFields.each(function () {
        var textBox = $(this).textbox('textbox');
        textBox.mask("(999) 999-9999? x99999");
    });
    var ssnFields = $('.type-ssn');
    ssnFields.each(function () {
        var textBox = $(this).textbox('textbox');
        textBox.mask("99-99-9999");
    });
    var n4Fields = $('.type-4n');
    n4Fields.each(function () {
        var textBox = $(this).textbox('textbox');
        textBox.mask("9999");
    });
    var n5Fields = $('.type-5n');
    n5Fields.each(function () {
        var textBox = $(this).textbox('textbox');
        textBox.mask("99999");
    });
    $('.easyui-timespinner').each(function () {
        var textBox = $(this).timespinner('textbox');
        textBox.mask("99:99", {placeholder: "hh:mm"});
    });
    $('.easyui-datebox').each(function () {
        var textBox = $(this).datebox('textbox');
        textBox.mask('99/99/9999', {placeholder: "mm/dd/YYYY"});
    });

    /* Display confirmation on clicking a delete button with the value 'Delete' */
    $('input[type=button][value="Delete"]').each(function () {
        var href = $(this).parent().attr('href');
        $(this).click(function () {
            $.messager.confirm('Confirm Deletion',
                'Esta seguro que quiere remover este record? Esta accion no se puede revertir.',
                function (r) {
                    if (r) {
                        window.location.href = href;
                    }
                }
            );
            return false;
        });
    });

  $(window).load(function() {
    $('.loader').hide();
  });
});
