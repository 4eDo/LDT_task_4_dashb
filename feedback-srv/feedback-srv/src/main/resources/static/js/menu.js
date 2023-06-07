"use strict";

$(function() {
    function initUI() {
        $('.showstatplease').on('click', function () {
            return confirm('Вы переходите на страницу со статистикой. \nСистема запросит логин и пароль. \nЛогин и пароль написаны на странице, введите их.');
        });
    }
    initUI();

});