"use strict";

$(function() {
    function initUI() {
        $('.delete').on('click', function() {
            var id = $(this).attr('data-id');

            if(confirm("Вы действительно хотите удалить постамат?")) {
                $.ajax({
                    method: 'DELETE',
                    url: '/postamat/delete?id=' + id,
                    success: function(resp) {
                            location.reload();
                    },
                    error: function(data) {
                        alert('Ошибка выполнения запроса', data.statusText);
                    }
                });
            }
        });

        $('#generateRand').on('click', function() {
            var x = prompt("Сколько записей создать?\n (целое неотрицательное число;)", "0");
            var count = parseInt(x);

            if(count < 1 || isNaN(count)) {
                alert("Вы ввели неправильное число");
                return false;
            }

            if(confirm("Вы действительно хотите сгенерировать строки в количестве " + count + " шт.?")) {
                $.ajax({
                    method: 'POST',
                    url: '/postamat/rand?count=' + count,
                    success: function(resp) {
                            location.reload();
                    },
                    error: function(data) {
                        alert('Ошибка выполнения запроса', data.statusText);
                    }
                });
            }
        });

        $('#editForm').submit(function (e) {
            // prevent form submission
            e.preventDefault();
            var thisForm = $(e.currentTarget);
            $.ajax({
                // simulate form submission
                type: thisForm.attr('method') || 'POST',
                url: thisForm.attr('action') || window.location.href,
                data:  thisForm.serialize()
            })
            .always(function () {
                // when it is done submitting data to the server, redirect
                window.location.replace("/postamat");
            });
        });

    }
    initUI();
});