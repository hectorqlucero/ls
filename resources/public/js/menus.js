function process_main(user_id, mid, valor) {
    $.post("/users/update_main_menu",{user_id: user_id, mid: mid, valor: valor},
            function(data) {
                location.reload();
            });
}

function process(user_id, mid, subid, valor) {
    $.post("/users/update_menu",{user_id: user_id, mid: mid, sid: subid, valor: valor},
            function(data) {
                location.reload();
            });
}

$(document).ready(function() {
    $("#special").dataTable({
        "iDisplayLength": 50,
        "bJqueryUI": false,
        "bStateSave": true,
        "bAutoWidth": false,
        "aaSorting":[[2, "asc"]],
        "sPaginationType": "full_numbers",
    });
});
