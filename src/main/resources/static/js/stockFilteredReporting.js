$(document).ready(function() {
	$("label").click(function(event) {
		if ($(this).attr("name") != "header") {
			$('#stockReporting').submit();
		}
	});
});
