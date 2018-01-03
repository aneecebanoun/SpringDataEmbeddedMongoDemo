$( document ).ready(function() {
	$("#dbJmsPerformanceLinkId").hide();
	brokerId = $("#brokerId").val();
	mongodbOverJmsURL = $("#mongodbOverJmsURL").val(); 
	$.post(mongodbOverJmsURL, {id: brokerId}, function(result){
        $("#loaderId").hide();
        $('#insertionTime').text(result.insertionTime);
        $('#queryTime').text(result.queryTime);
        $('#deleteTime').text(result.deleteTime);
        $('#systemInfo').append(result.systemInfo);
        $("#dbJmsPerformanceLinkId").show();
    });
});
