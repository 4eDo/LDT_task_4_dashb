"use strict";

$(function() {
    function initUI() {
        $('input[type=file]').on('change', function() {
            var id = $(this).attr('id');
        console.log(id + " changed!")
            if(!checkFileSize($("#" + id))){
              alert('Ошибка прикрепления файла!', sessionStorage.getItem("errorDescription"));//"Максимально допустимый размер файла: " + MAX_FILE_SIZE);
              document.getElementById(id + "Btn").setAttribute('disabled', 'true');
              document.getElementById(id + "Btn").classList.add("disabled");
              document.getElementById(id + "Btn").setAttribute('title', 'Прикрепите файл');
            } else if(document.getElementById(id + "Btn").hasAttribute('disabled')) {
                document.getElementById(id + "Btn").removeAttribute('disabled');
              document.getElementById(id + "Btn").classList.remove("disabled");
            }
          });

    }
    initUI();
});

