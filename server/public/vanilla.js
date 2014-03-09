function dosearch (query) {
	$.get('/search?q='+query, render);
}

function all() {
	$.get('/search?size=10000', render);
}

function render(data){
	var results = $('#results');
	var table = document.createElement('table');
	table.className = 'table table-hover';
	for (var i = data.hits.hits.length -1; i >= 0; i--) {
		hit = data.hits.hits[i];
		var tr = table.insertRow();
		var desc = tr.insertCell();
		desc.innerText = hit.fields.description[0];
		var title = tr.insertCell();

		var a = document.createElement('a');
		a.innerText = hit.fields.title[0];
		if (hit.fields.homepage){
			a.setAttribute('href', hit.fields.homepage[0]);
		}

		title.appendChild(a);
	}
	
	$("#results table").remove();
	
	$("#results").append(table);
}

$(document).ready(function(){
	$("#query").keyup(function(){
		var q = $("#query").val();
		if (q.length === 0){
			$("#results table").remove();
		} else {
			dosearch(q);
		}
	});

	$("#all").click(all);
});

