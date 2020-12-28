$(document).ready(function() {

    $('#all').click(function() {
        $.all();
    });
    $.all = function() {
        jsRoutes.controllers.HomeController.getAllCars().ajax({
            success: function(result) {
                createTable(result);
            },
            failure: function(err) {
                $("#status").text('There was an error');
            }
        });
    }

    function createTable(result) {
        $('#main_table').empty();
        var table = "<table border='1'>"
        table += '<tr><td><b>Number</b></td><td><b>Model</b></td><td><b>Color</b></td><td><b>Year</b></td><td><b>Delete</b></td></tr>';
        for(i in result) {
            table += '<tr>';
            table += '<td>' + result[i].number + '</td>';
            table += '<td>' + result[i].model + '</td>';
            table += '<td>' + result[i].color + '</td>';
            table += '<td>' + result[i].year + '</td>';
            table += '<td align="center"><button type="button" id="' + result[i].id + '"class="deletebtn" title="Delete car">X</button></td>';
            table += '</tr>';
        }
        table += "</table>"
        $('#main_table').append(table);
    };

    $(document).on('click', 'button.deletebtn', function () {
        var id = $(this).attr('id');
        $.ajax({
            url: '/car/' + id,
            type: 'DELETE',
            success: function() {
                $("#status").text('id ' + id + ' deleted');
                $.all();
            },
            failure: function(err) {
                $("#status").text('There was an error');
            }
        });
    });

    $.all();

});