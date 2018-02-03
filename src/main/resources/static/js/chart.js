$( document ).ready(function() {
	setInterval(function(){ 
		var gbpChartUrlId = $("#gbpChartUrlId").attr("value")+"&RANDOM="+Math.random(); 
		$("#gbpChartId").attr("src", gbpChartUrlId);
		var usChartUrlId = $("#usChartUrlId").attr("value")+"&RANDOM="+Math.random(); 
		$("#usChartId").attr("src", usChartUrlId);
		var chartHistory1UrlId = $("#chartHistory1UrlId").attr("value")+"&RANDOM="+Math.random();
		$("#chartHistory1Id").attr("src", chartHistory1UrlId);
		var chartHistory2UrlId = $("#chartHistory2UrlId").attr("value")+"&RANDOM="+Math.random();
		$("#chartHistory2Id").attr("src", chartHistory2UrlId);
		var againstA1UrlId = $("#againstA1UrlId").attr("value")+"&RANDOM="+Math.random();
		$("#againstA1Id").attr("src", againstA1UrlId);
		var againstB1UrlId = $("#againstB1UrlId").attr("value")+"&RANDOM="+Math.random();
		$("#againstB1Id").attr("src", againstB1UrlId);
		}, 25000);
	$("#currencyHistory1Id").change(function () {
        var currency = this.value;
		var chartHistory1UrlId = $("#chartViewUrlId").attr("value")+"?currencies="+currency;
		$("#chartHistory1UrlId").attr("value", chartHistory1UrlId);
		$("#chartHistory1Id").attr("src", chartHistory1UrlId);
    });
	$("#currencyHistory2Id").change(function () {
        var currency = this.value;
		var chartHistory2UrlId = $("#chartViewUrlId").attr("value")+"?currencies="+currency;
		$("#chartHistory2UrlId").attr("value", chartHistory2UrlId);
		$("#chartHistory2Id").attr("src", chartHistory2UrlId);
    });
	$("#currencyA1Id").change(function () {
        var currency1 = this.value;
        var currency2 = $("#currencyA2Id").val();
		var againstA1Url = $("#chartViewUrlId").attr("value")+"?currencies="+currency2+"&inCurrency="+currency1;
		$("#againstA1UrlId").attr("value", againstA1Url);
		$("#againstA1Id").attr("src", againstA1Url);
    });
	$("#currencyA2Id").change(function () {
        var currency2 = this.value;
        var currency1 = $("#currencyA1Id").val();
		var againstA1Url = $("#chartViewUrlId").attr("value")+"?currencies="+currency2+"&inCurrency="+currency1;
		$("#againstA1UrlId").attr("value", againstA1Url);
		$("#againstA1Id").attr("src", againstA1Url);

    });
	
	$("#currencyB1Id").change(function () {
        var currency1 = this.value;
        var currency2 = $("#currencyB2Id").val();
		var againstB1Url = $("#chartViewUrlId").attr("value")+"?currencies="+currency2+"&inCurrency="+currency1;
		$("#againstB1UrlId").attr("value", againstB1Url);
		$("#againstB1Id").attr("src", againstB1Url);
    });
	$("#currencyB2Id").change(function () {
        var currency2 = this.value;
        var currency1 = $("#currencyB1Id").val();
		var againstB1Url = $("#chartViewUrlId").attr("value")+"?currencies="+currency2+"&inCurrency="+currency1;
		$("#againstB1UrlId").attr("value", againstB1Url);
		$("#againstB1Id").attr("src", againstB1Url);

    });



	
});
