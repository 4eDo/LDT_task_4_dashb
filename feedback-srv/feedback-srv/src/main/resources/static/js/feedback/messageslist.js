"use strict";

$(function() {
    function initUI() {

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
                window.location.replace("/feedback");
            });
        });

    }
    initUI();
});