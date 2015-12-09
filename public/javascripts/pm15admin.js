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
		'contentType': 'application/json; charset=UTF-8',
		'dataType': 'json',
		'error': onAjaxError,
		'success': onsuccess
	};
	$.ajax(url, request);
}

function postRequest(path, data, onsuccess) {
	ajaxRequest('POST', path, JSON.stringify(data), onsuccess);
}

function getRequest(path, onsuccess) {
	ajaxRequest('GET', path, undefined, onsuccess);
}

function deleteRequest(path, onsuccess) {
	ajaxRequest('DELETE', path, undefined, onsuccess);
}

function putRequest(path, data, onsuccess) {
	ajaxRequest('PUT', path, JSON.stringify(data), onsuccess);
}

/*
 * Business
 */

var BlogModel = {
		'entries': [],
		'categories': [],
		'tags': []
}

function loadBlogEntries() {
	var blogListTable = $('#bloglistTable');
	var tBody = blogListTable.find('tbody');
	tBody.html('<tr><td colspan="3" class="row_loading">Wird geladen ...</td></tr>');
	getRequest('/rest/admin/blog/entries/', function onBlogListSuccess(data) {
		tBody.html('');
		BlogModel.entries = data.entries;
		$('#numBlogEntries').text(BlogModel.entries.length);
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

function loadCategories() {
	var catListTable = $('#catlistTable');
	var tBody = catListTable.find('tbody');
	tBody.html('<tr><td colspan="3" class="row_loading">Wird geladen ...</td></tr>');
	getRequest('/rest/admin/blog/categories/', function onCategoryListSuccess(data) {
		tBody.html('');
		BlogModel.categories = data.entries;
		$('#numCats').text(BlogModel.categories.length);
		var catSelect = $('#createBlogCategory');
		catSelect.find('option[value]').remove();
		if (BlogModel.categories && BlogModel.categories.length) {
			$.each(BlogModel.categories, function() {
				var option = $('<option value=""></option>');
				option.val(this.id);
				option.text(this.title);
				option.appendTo(catSelect);
				
				var tr = $('<tr class="row_clickable"></tr>');
				tr.appendTo(tBody);
				var titleCol = $('<td><strong class="title"></strong></td>');
				titleCol.find('.title').text(this.title);
				titleCol.appendTo(tr);
				var urlCol = $('<td><code class="url"></code></td>');
				urlCol.find('.url').text(this.url);
				urlCol.appendTo(tr);
				var entriesCol = $('<td></td>');
				entriesCol.text(this.blogEntries);
				entriesCol.appendTo(tr);
				tr.on('click', (function categoryClick(id, event) {
					console.log(id);
					alert("Edit Category " + id);
				}).bind(this, this.id));
			});
		} else {
			tBody.html('<tr><td colspan="3">Keine Einträge vorhanden</td></tr>');
		}
	});
}

function loadTags() {
	var tagListTable = $('#taglistTable');
	var tBody = tagListTable.find('tbody');
	tBody.html('<tr><td colspan="3" class="row_loading">Wird geladen ...</td></tr>');
	getRequest('/rest/admin/blog/tags/', function onTagListSuccess(data) {
		tBody.html('');
		BlogModel.tags = data.entries;
		$('#numTags').text(BlogModel.tags.length);
		if (BlogModel.tags && BlogModel.tags.length) {
			$.each(BlogModel.tags, function() {
				var tr = $('<tr class="row_clickable"></tr>');
				tr.appendTo(tBody);
				var titleCol = $('<td><strong class="title"></strong></td>');
				titleCol.find('.title').text(this.title);
				titleCol.appendTo(tr);
				var urlCol = $('<td><code class="url"></code></td>');
				urlCol.find('.url').text(this.url);
				urlCol.appendTo(tr);
				var entriesCol = $('<td></td>');
				entriesCol.text(this.blogEntries);
				entriesCol.appendTo(tr);
				tr.on('click', (function categoryClick(id, event) {
					console.log(id);
					alert("Edit Tag " + id);
				}).bind(this, this.id));
			});
		} else {
			tBody.html('<tr><td colspan="3">Keine Einträge vorhanden</td></tr>');
		}
	});
}

function insertBlogEntry() {
	var showInsertError = function showInsertError(msg) {
		$('#createBlogMessage').html("Error: <span></span>");
		$('#createBlogMessage').find('span').text(msg);
		$('#createBlogMessage').removeClass('hidden');
	}
	var data = {
			'title': $('#createBlogTitle').val(),
			'url': $('#createBlogURL').val(),
			'category': $('#createBlogCategory').val()
	}
	if (data.category) {
		try {
			var categoryId = parseInt(data.category);
			data.category = categoryId;
		} catch (e) {
			showInsertError("Kategorie muss eine Zahl sein");
			return;
		}
	}
	
	if (!data.category) {
		showInsertError("Bitte eine Kategorie auswählen.");
		return;
	}
	if (!data.title) {
		showInsertError("Bitten einen Titel eingeben.");
		return;
	}
	if (!data.url) {
		showInsertError("Bitte eine URL eingeben.");
		return;
	}
	$('#createBlogMessage').addClass('hidden');
	postRequest('/rest/admin/blog/entries/', data, function onBlogInsertSuccess(data) {
		if (data.success) {
			$('#createBlogModal').modal('hide');
			loadBlogEntries();
			alert('Successfully inserted with id=' + data.id);
		} else {
			showInsertError(data.error);
		}
	});
}


$(function initializeBlog() {
	loadBlogEntries();
	loadCategories();
	loadTags();
	$('#createBlogButton').on('click', insertBlogEntry);
});
