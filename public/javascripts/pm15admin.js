/* Admin Javascript for PM15. */

/*
 * API
 */

if (!window.location.origin) {
	// IE Fix
	window.location.origin = window.location.protocol + "//" + window.location.host;
}

function onAjaxError(xhr, textStatus, error) {
	console.log(xhr, textStatus, error);
	alert("Ajax Error (" + textStatus +"):\n" + error);
}

function ajaxRequest(method, path, data, onsuccess) {
	var url = window.location.origin + path;
	var request = {
		'method': method,
		'data': data,
		'dataType': 'json',
		'error': onAjaxError,
		'success': onsuccess
	};
	$.ajax(url, request);
}

function postRequest(path, data, onsuccess) {
	ajaxRequest('POST', path, data, onsuccess);
}

function getRequest(path, onsuccess) {
	ajaxRequest('GET', path, undefined, onsuccess);
}

function deleteRequest(path, onsuccess) {
	ajaxRequest('DELETE', path, undefined, onsuccess);
}

function putRequest(path, data, onsuccess) {
	ajaxRequest('PUT', path, data, onsuccess);
}

/*
 * Business
 */

function loadBlogEntries() {
	var blogListTable = $('#bloglistTable');
	if (blogListTable.length) {
		var tBody = blogListTable.find('tbody');
		tBody.html('<tr><td colspan="3" class="row_loading">Wird geladen ...</td></tr>');
		getRequest('/rest/admin/blog/', function onBlogListSuccess(data) {
			tBody.html('');
			if (data.entries && data.entries.length) {
				$.each(data.entries, function() {
					var tr = $('<tr class="row_clickable"></tr>');
					tr.appendTo(tBody);
					var titleCol = $('<td><strong class="title"></strong><div class="url">URL: <code></code></div></td>');
					titleCol.find('.title').text(this.title);
					titleCol.find('.url code').text(this.url);
					titleCol.appendTo(tr);
					var metaCol = $('<td><div class="category"><strong>Kategorie:</strong> <span></span></div><div class="tags"><strong>Tags:</strong> <span></span></div></td>');
					metaCol.find('.category span').text(this.category);
					if (this.tags.length) {
						var tags = this.tags.join(', ');
						metaCol.find('.tags span').text(tags);
					} else {
						metaCol.find('.tags').text('');
					}
					metaCol.appendTo(tr);
					var publishedCol = $('<td></td>');
					if (this.published && this.publishedDate) {
						publishedCol.html('<strong>Veröffentlicht</strong><br>' + this.publishedDate);
					} else {
						publishedCol.html('<strong>Unveröffentlicht</strong>');
					}
					publishedCol.appendTo(tr);
					tr.on('click', (function blogEntryClick(id, event) {
						console.log(id);
						alert("Edit " + id);
					}).bind(this, this.id));
				});
			} else {
				tBody.html('<tr><td colspan="3">Keine Einträge vorhanden</td></tr>');
			}
		});
	}
}

function createNewBlogEntry() {
	alert("Create Blog Entry");
}

$(function initializeBlog() {
	loadBlogEntries();
	
	$('#addBlogButton').on('click', createNewBlogEntry);
});
