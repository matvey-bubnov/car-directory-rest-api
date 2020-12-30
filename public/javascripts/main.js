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
                $("#status").text('Refresh error');
            }
        });
    }

    $('#add_car').click(function() {
        var carForm = {
            carNumber:$("#input_number").val(),
            carModel:$("#input_model").val(),
            carColor:$("#input_color").val(),
            carYear: $("#input_year").val()
        }
        $.ajax({
            url: '/car/addNewCar',
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(carForm),
            complete: function() {
                $("#status").text(carForm.carNumber + ' saved');
                $.search();
            },
            failure: function(err) {
                $("#status").text('Saving error');
            }
        });
    });

    function createHeader() {
        var table = '<table border="1" width="100%">'
        table += '<tr><td></td><td><b>Number</b></td><td><b>Model</b></td><td><b>Color</b></td><td><b>Year</b></td></tr>';
        table += '<tr>';
        table += '<td width="5%"><b>Filters:</b></td>';
        table += '<td width="20%"><input type="text" id="filter_number" title="Filter by Number"  style="width: 90%;"></td>';
        table += '<td width="20%"><input type="text" id="filter_model" title="Filter by Model" style="width: 90%;"></td>';
        table += '<td width="20%"><input type="text" id="filter_color" title="Filter by Color" style="width: 90%;"></td>';
        table += '<td width="20%"><input type="number" min="0" id="filter_year" title="Filter by Year" style="width: 90%;"></td>';
        table += '</tr>';
        table += "</table>"
        $('#header_table').append(table);
    };

    function createTable(result) {
        $('#main_table').empty();
        var table = '<table border="1" width="100%">'
        for(i in result) {
            table += '<tr>';
            table += '<td  width="5%" align="center"><button type="button" id="'
                + result[i].id + '" name="' + result[i].number
                + '" class="deletebtn" title="Delete car" style="width: 100%;">X</button></td>';
            table += '<td width="20%">' + result[i].number + '</td>';
            table += '<td width="20%">' + result[i].model + '</td>';
            table += '<td width="20%">' + result[i].color + '</td>';
            table += '<td width="20%">' + result[i].year + '</td>';
            table += '</tr>';
        }
        table += "</table>"
        $('#main_table').append(table);
    };

    $(document).on('click', 'button.deletebtn', function () {
        var id = $(this).attr('id');
        var name = $(this).attr('name');
        $.ajax({
            url: '/car/' + id,
            type: 'DELETE',
            success: function() {
                $("#status").text(name + ' deleted');
                $.search();
            },
            error: function(err) {
                $("#status").text(err.responseText);
            }
        });
    });

    $('#search_car').click(function() {
        $.search();
    });
    $.search = function() {
        var filterNumber = $("#filter_number").val();
        var filterModel = $("#filter_model").val();
        var filterColor = $("#filter_color").val();
        var filterYear = $("#filter_year").val();
        jsRoutes.controllers.HomeController.searchCars(filterNumber, filterModel, filterColor, filterYear).ajax({
            success: function(result) {
                createTable(result);
            },
            failure: function(err) {
                $("#status").text('Refresh error');
            }
        });
    }

    createHeader();
    $.all();

});