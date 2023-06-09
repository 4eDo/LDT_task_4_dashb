function checkFileSize(file) {
  var maxSize = parseInt(MAX_FILE_SIZE.replace(/[^0-9]/g, ''))
  if(MAX_FILE_SIZE.toUpperCase().indexOf("KB") != -1){
    maxSize *= 1024;
  } else if(MAX_FILE_SIZE.toUpperCase().indexOf("MB") != -1){
    maxSize *= 1024 * 1024;
  }

  var currentSize = 0;
  if (file != null && file.length > 0 && file[0].files != null && file[0].files.length > 0) {
    currentSize = file[0].files[0].size;
  }

  let errorDescription;
  if (currentSize < 1) {
    errorDescription = 'Загружаемый файл не может быть нулевого размера';
    $('#errorDescription').text(errorDescription);
    $('#error').show();
    sessionStorage.setItem("errorDescription", errorDescription)
    return false;
  } else if (currentSize > maxSize) {
    errorDescription = 'Максимально допустимый размер файла ' + MAX_FILE_SIZE;
    $('#errorDescription').text(errorDescription);
    $('#error').show();
    sessionStorage.setItem("errorDescription", errorDescription)
    return false;
  }
  return true;
}