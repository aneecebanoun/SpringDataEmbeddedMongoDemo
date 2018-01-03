$(document).ready(function() {
	$("label").click(function(event) {
		if ($(this).attr("name") == "header") {
			$('#sortingOption').attr("value", $(this).text());
			$('#stockReporting').submit();
		} else {
			searchKey = $(this).text();
			searchType = $(this).attr("name");
			$('#searchKey').attr("value", searchKey);
			$('#searchType').attr("value", searchType);
			$('#stockFilteredReporting').submit();
		}
	});
});
