/*Начало скрипта для вкладок в шапке*/

$('#h_bt_1').on('click', function() {
	$('#adm_mail').removeClass('hide');
	$('#hi_info').css("display", "none");
	$('#adm_mail').css("display") == "block" ? $('#h_bt_1').addClass('h_bt_active') : $('#h_bt_1').removeClass('h_bt_active');
	$('#h_bt_2').removeClass('h_bt_active') & $('#h_bt_3').removeClass('h_bt_active');
	$('#adm_mail').css("display") == "block" ? $('#adm_postamat').addClass('hide') & $('#adm_partners').addClass('hide') : 	$('#hi_info').css("display", "block");
});

$('#h_bt_2').on('click', function() {
	$('#adm_postamat').removeClass('hide');
	$('#hi_info').css("display", "none");
	$('#adm_postamat').css("display") == "block" ? $('#h_bt_2').addClass('h_bt_active') : $('#h_bt_2').removeClass('h_bt_active');
	$('#h_bt_1').removeClass('h_bt_active') & $('#h_bt_3').removeClass('h_bt_active');
	$('#adm_postamat').css("display") == "block" ? $('#adm_mail').addClass('hide') & $('#adm_partners').addClass('hide') : 	$('#hi_info').css("display", "block");
});

$('#h_bt_3').on('click', function() {
	$('#adm_partners').removeClass('hide');
	$('#hi_info').css("display", "none");
	$('#adm_partners').css("display") == "block" ? $('#h_bt_3').addClass('h_bt_active') : $('#h_bt_3').removeClass('h_bt_active');
	$('#h_bt_1').removeClass('h_bt_active') & $('#h_bt_2').removeClass('h_bt_active');
	$('#adm_partners').css("display") == "block" ? $('#adm_mail').addClass('hide') & $('#adm_postamat').addClass('hide') : 	$('#hi_info').css("display", "block");
});

/*Конец скрипта для вкладок в шапке*/