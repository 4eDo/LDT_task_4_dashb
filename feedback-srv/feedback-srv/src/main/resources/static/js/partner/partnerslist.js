"use strict";

$(function() {
    function initUI() {
        $('.delete').on('click', function() {
            var id = $(this).attr('data-id');

            if(confirm("Вы действительно хотите удалить партнёра?")) {
                $.ajax({
                    method: 'DELETE',
                    url: '/partner/delete?id=' + id,
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
                window.location.replace("/partner");
            });
        });

    }
    initUI();
});