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

function ajaxRequest(method, path, data, onsuccess, onerror) {
	var url = window.location.origin + path;
	var request = {
		'method': method,
		'data': data,
		'contentType': 'application/json; charset=UTF-8',
		'dataType': 'json',
		'error': (typeof onerror === "function") ? onerror : onAjaxError,
		'success': onsuccess
	};
	$.ajax(url, request);
}

function postRequest(path, data, onsuccess, onerror) {
	ajaxRequest('POST', path, JSON.stringify(data), onsuccess, onerror);
}

function getRequest(path, onsuccess, onerror) {
	ajaxRequest('GET', path, undefined, onsuccess, onerror);
}

function deleteRequest(path, onsuccess, onerror) {
	ajaxRequest('DELETE', path, undefined, onsuccess, onerror);
}

function putRequest(path, data, onsuccess, onerror) {
	ajaxRequest('PUT', path, JSON.stringify(data), onsuccess, onerror);
}

/*
 * Admin Overview Page
 */

function adminOverviewPage() {
	

	var BlogModel = {
			'entries': [],
			'categories': [],
			'tags': []
	}
	
	function showBlogEditPage(id) {
		location.href = window.location.origin + "/admin/editblog/" + id + "/";
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
						showBlogEditPage(id);
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
					tr.on('click', showEditCategory.bind(this));
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
					tr.on('click', showEditTag.bind(this));
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
				showBlogEditPage(data.id);
			} else {
				showInsertError(data.error);
			}
		});
	}
	
	function insertCategory() {
		var showInsertError = function showInsertError(msg) {
			$('#createCategoryMessage').html("Error: <span></span>");
			$('#createCategoryMessage').find('span').text(msg);
			$('#createCategoryMessage').removeClass('hidden');
		}
		var data = {
				'title': $('#createCategoryTitle').val(),
				'url': $('#createCategoryURL').val()
		}
		if (!data.title) {
			showInsertError("Bitten einen Titel eingeben.");
			return;
		}
		if (!data.url) {
			showInsertError("Bitte eine URL eingeben.");
			return;
		}
		$('#createCategoryMessage').addClass('hidden');
		postRequest('/rest/admin/blog/categories/', data, function onCategoryInsertSuccess(data) {
			if (data.success) {
				$('#createCategoryModal').modal('hide');
				loadCategories();
			} else {
				showInsertError(data.error);
			}
		}, function onCategoryInsertError(xhr, textStatus, error) {
			showInsertError(error);
		});
	}
	
	function insertTag() {
		var showInsertError = function showInsertError(msg) {
			$('#createTagMessage').html("Error: <span></span>");
			$('#createTagMessage').find('span').text(msg);
			$('#createTagMessage').removeClass('hidden');
		}
		var data = {
				'title': $('#createTagTitle').val(),
				'url': $('#createTagURL').val()
		}
		if (!data.title) {
			showInsertError("Bitten einen Titel eingeben.");
			return;
		}
		if (!data.url) {
			showInsertError("Bitte eine URL eingeben.");
			return;
		}
		$('#createTagMessage').addClass('hidden');
		postRequest('/rest/admin/blog/tags/', data, function onTagInsertSuccess(data) {
			if (data.success) {
				$('#createTagModal').modal('hide');
				loadTags();
			} else {
				showInsertError(data.error);
			}
		}, function onTagInsertError(xhr, textStatus, error) {
			showInsertError(error);
		});
	}
	
	function showEditCategory(event) {
		var model = this;
		$('#editCategoryTitle').val(model.title);
		$('#editCategoryURL').val(model.url);
		$('#editCategoryButton').off("click.editcategory");
		$('#editCategoryButton').on("click.editcategory", editCategory.bind(undefined, model.id));
		$('#deleteCategoryButton').off("click.delcategory");
		$('#editCategoryMessage').addClass('hidden');
		if (model.blogEntries !== 0) {
			$('#deleteCategoryButton').addClass("disabled");
			$('#deleteCategoryButton').attr("title", "Löschen nicht möglich, da noch Blog-Einträge zugewiesen sind.");
			$('#deleteCategoryButton').tooltip();
		} else {
			$('#deleteCategoryButton').tooltip("destroy");
			$('#deleteCategoryButton').removeClass("disabled");
			$('#deleteCategoryButton').removeAttr("title");
			$('#deleteCategoryButton').on("click.delcategory", deleteCategory.bind(undefined, model.id));
		}
		$('#editCategoryModal').modal('show');
	}
	
	function editCategory(id) {
		var showEditError = function showEditError(msg) {
			$('#editCategoryMessage').html("Error: <span></span>");
			$('#editCategoryMessage').find('span').text(msg);
			$('#editCategoryMessage').removeClass('hidden');
		}
		var data = {
				'title': $('#editCategoryTitle').val(),
				'url': $('#editCategoryURL').val()
		}
		if (!data.title) {
			showEditError("Bitten einen Titel eingeben.");
			return;
		}
		if (!data.url) {
			showEditError("Bitte eine URL eingeben.");
			return;
		}
		$('#editCategoryMessage').addClass('hidden');
		putRequest('/rest/admin/blog/category/' + id + '/', data, function onCategoryEditSuccess(data) {
			if (data.success) {
				$('#editCategoryModal').modal('hide');
				loadCategories();
			} else {
				showEditError(data.error);
			}
		}, function onCategoryEditError(xhr, textStatus, error) {
			showEditError(error);
		});
	}
	
	function deleteCategory(id) {
		var showEditError = function showEditError(msg) {
			$('#editCategoryMessage').html("Error: <span></span>");
			$('#editCategoryMessage').find('span').text(msg);
			$('#editCategoryMessage').removeClass('hidden');
		}
		$('#editCategoryMessage').addClass('hidden');
		deleteRequest('/rest/admin/blog/category/' + id + '/', function onCategoryDeleteSuccess(data) {
			if (data.success) {
				$('#editCategoryModal').modal('hide');
				loadCategories();
			} else {
				showEditError(data.error);
			}
		}, function onCategoryDeleteError(xhr, textStatus, error) {
			showEditError(error);
		});
	}
	
	function showEditTag(event) {
		var model = this;
		$('#editTagTitle').val(model.title);
		$('#editTagURL').val(model.url);
		$('#editTagButton').off("click.edittag");
		$('#editTagButton').on("click.edittag", editTag.bind(undefined, model.id));
		$('#deleteTagButton').off("click.deltag");
		$('#editTagMessage').addClass('hidden');
		if (model.blogEntries !== 0) {
			$('#deleteTagButton').addClass("disabled");
			$('#deleteTagButton').attr("title", "Löschen nicht möglich, da noch Blog-Einträge zugewiesen sind.");
			$('#deleteTagButton').tooltip();
		} else {
			$('#deleteTagButton').tooltip("destroy");
			$('#deleteTagButton').removeClass("disabled");
			$('#deleteTagButton').removeAttr("title");
			$('#deleteTagButton').on("click.deltag", deleteTag.bind(undefined, model.id));
		}
		$('#editTagModal').modal('show');
	}
	
	function editTag(id) {
		var showEditError = function showEditError(msg) {
			$('#editTagMessage').html("Error: <span></span>");
			$('#editTagMessage').find('span').text(msg);
			$('#editTagMessage').removeClass('hidden');
		}
		var data = {
				'title': $('#editTagTitle').val(),
				'url': $('#editTagURL').val()
		}
		if (!data.title) {
			showEditError("Bitten einen Titel eingeben.");
			return;
		}
		if (!data.url) {
			showEditError("Bitte eine URL eingeben.");
			return;
		}
		$('#editTagMessage').addClass('hidden');
		putRequest('/rest/admin/blog/tag/' + id + '/', data, function onTagEditSuccess(data) {
			if (data.success) {
				$('#editTagModal').modal('hide');
				loadTags();
			} else {
				showEditError(data.error);
			}
		}, function onTagEditError(xhr, textStatus, error) {
			showEditError(error);
		});
	}
	
	function deleteTag(id) {
		var showEditError = function showEditError(msg) {
			$('#editTagMessage').html("Error: <span></span>");
			$('#editTagMessage').find('span').text(msg);
			$('#editTagMessage').removeClass('hidden');
		}
		$('#editTagMessage').addClass('hidden');
		deleteRequest('/rest/admin/blog/tag/' + id + '/', function onTagDeleteSuccess(data) {
			if (data.success) {
				$('#editTagModal').modal('hide');
				loadTags();
			} else {
				showEditError(data.error);
			}
		}, function onTagDeleteError(xhr, textStatus, error) {
			showEditError(error);
		});
	}
	
	function initAdminPage() {
		console.log("Initialize Admin Overview");
		loadBlogEntries();
		loadCategories();
		loadTags();
		$('#createBlogButton').on('click', insertBlogEntry);
		$('#createCategoryButton').on('click', insertCategory);
		$('#createTagButton').on('click', insertTag);
	}
	
	initAdminPage();
	
}

/*
 * Admin Overview Page
 */

function adminEditBlogPage(blogId) {
	
	console.log("Edit Blog Entry " + blogId);
	
	var editor;
	
	function loadBlogEntry() {
		getRequest('/rest/admin/blog/entry/' + blogId + '/', function onBlogLoadSuccess(data) {
			console.log(data);
			if(data.success) {
				refreshMeta(data.blogEntry, data.availableCategories.entries, data.availableTags.entries);
				refreshContent(data.blogEntry);
			} else {
				console.log("ERROR: ", data);
			}
		}, function onBlogLoadError(xhr, status, error) {
			console.log(error);
			
		});
	}
	
	function refreshMeta(data, cats, tags) {
		$('#blogTitle').val(data.title);
		$('#blogURL').val(data.url);
		$('#blogPublished').prop('checked', data.published);
		if (data.publishedDate) {
			$('#blogPublishedDate').val(data.publishedDate);
		}
		updatePublishDateInput();
		updatePublishNow();
		
		var catSelect = $('#blogCategory');
		catSelect.find('option[value]').remove();
		$.each(cats, function() {
			var option = $('<option value=""></option>');
			option.val(this.id);
			option.text(this.title);
			if (this.id === data.category.id) {
				option.prop("selected", true);
			}
			option.appendTo(catSelect);
		});
		
		var tagDropdown = $('#blogAddTagList');
		$.each(tags, function() {
			var addTagItem = $('<li><a href="#"></a></li>');
			var addTagLink = addTagItem.find('a');
			addTagLink.text(this.title);
			addTagLink.attr('data-id', this.id);
			addTagLink.on('click.activatetag', activateTag.bind(undefined, this.id, this.title));
			addTagItem.appendTo(tagDropdown);
		});
		
		$.each(data.tags, function() {
			activateTag(this.id, this.title);
		});
	}
	
	function updatePublishDateInput() {
		$('#blogPublishedDate').prop('disabled', !$('#blogPublished').prop('checked'));
	}
	
	function updatePublishNow() {
		if ($('#blogPublished').prop('checked') === false && !$('#blogPublishedDate').val()) {
			$('#saveMetaButton').addClass('btn-default').removeClass('btn-primary');
			$('#saveAndPublishButton').removeClass('hidden');
		} else {
			$('#saveMetaButton').addClass('btn-primary').removeClass('btn-default');
			$('#saveAndPublishButton').addClass('hidden');
		}
	}
	
	function activateTag(id, title) {
		$('#blogAddTagList a[data-id=' + id + ']').addClass('hidden');
		var tagsContainer = $('#blogTags');
		if (tagsContainer.find('span[data-id=' + id + ']').length === 0) {
			var tagSpan = $('<span class="label label-default"></span>');
			tagSpan.attr('data-id', id);
			tagSpan.text(title);
			tagSpan.on('click', deactivateTag.bind(undefined, id));
			tagSpan.appendTo(tagsContainer);
			tagsContainer.append(" ");
		}
	}
	
	function deactivateTag(id) {
		$('#blogAddTagList a[data-id=' + id + ']').removeClass('hidden');
		$('#blogTags span[data-id=' + id + ']').remove();
	}
	
	function refreshContent(data) {
		editor.setValue(data.content);
		editor.clearSelection();
		$('#blogEntryTitle').text(data.title);
	}
	
	function saveContent() {
		var messageContainer = $('#contentMessage');
		messageContainer.addClass('hidden');
		messageContainer.removeClass('alert-danger');
		messageContainer.removeClass('alert-success');
		$('#saveContentButton').prop('disabled', true);
		
		function showSuccess() {
			messageContainer.addClass('alert-success');
			$('#contentMessageText').text('Speichern erfolgreich');
			messageContainer.removeClass('hidden');
		}
		function showError(error) {
			messageContainer.addClass('alert-danger');
			$('#contentMessageText').text('Error: ' + error);
			messageContainer.removeClass('hidden');
		}
		
		var data = {
			'content': editor.getValue(),
			'preview': false
		}
		putRequest('/rest/admin/blog/entry/' + blogId + '/', data, function onBlogUpdateSuccess(data) {
			$('#saveContentButton').prop('disabled', false);
			if(data.success) {
				showSuccess();
			} else if(data.error) {
				showError(data.error);
			} else {
				showError("Unbekannter Fehler")
			}
		}, function onBlogUpdateError(xhr, status, error) {
			$('#saveContentButton').prop('disabled', false);
			showError(status + " - " + error);
		});
	}
	
	function saveMeta() {
		var showSaveError = function showSaveError(msg) {
			$('#metaMessage').html("Error: <span></span>");
			$('#metaMessage').addClass('alert-error').removeClass('alert-success');
			$('#metaMessage').find('span').text(msg);
			$('#metaMessage').removeClass('hidden');
		}
		var showSaveSuccess = function showSaveSuccess() {
			$('#metaMessage').html("Erfolgreich gespeichert");
			$('#metaMessage').addClass('alert-success').removeClass('alert-error');
			$('#metaMessage').find('span').text(msg);
			$('#metaMessage').removeClass('hidden');
		}
		var data = {
				'title': $('#blogTitle').val(),
				'url': $('#blogURL').val(),
				'category': $('#blogCategory').val(),
				'published': $('#blogPublished').prop('checked'),
				'publishedDate': $('#blogPublishedDate').val(),
				'tags': []
		}
		if (data.category) {
			try {
				var categoryId = parseInt(data.category);
				data.category = categoryId;
			} catch (e) {
				showSaveError("Kategorie muss eine Zahl sein");
				return;
			}
		}
		$.each($('#blogTags span[data-id]'), function() {
			var tag = $(this);
			var id = parseInt(tag.attr('data-id'));
			data.tags.push(id);
		});
		
		if (!data.category) {
			showSaveError("Bitte eine Kategorie auswählen.");
			return;
		}
		if (!data.title) {
			showSaveError("Bitten einen Titel eingeben.");
			return;
		}
		if (!data.url) {
			showSaveError("Bitte eine URL eingeben.");
			return;
		}
		$('#metaMessage').addClass('hidden');
		//$('#saveMetaButton').prop('disabled', true);
		console.log(data);
//		postRequest('/rest/admin/blog/entries/', data, function onBlogInsertSuccess(data) {
//			if (data.success) {
//				$('#createBlogModal').modal('hide');
//				showBlogEditPage(data.id);
//			} else {
//				showInsertError(data.error);
//			}
//		});
	}
	
	function initEditBlogPage() {
		var editorElem = $("#blogContentEditor");
		editorElem.css('position', 'relative');
		editorElem.height(600);
		editorElem.width(editorElem.width());
		editor = ace.edit("blogContentEditor");
		editor.setTheme("ace/theme/xcode");
		editor.getSession().setMode("ace/mode/markdown");
		editor.getSession().setUseWrapMode(true);
		$('#blogPublished').on('click', updatePublishDateInput);
		$('#blogPublished').on('click', updatePublishNow);
		$('#blogPublishedDate').on('change', updatePublishNow);
		$('#saveAndPublishButton').tooltip();
		
		$('#saveMetaButton').on('click', saveMeta);
		$('#saveContentButton').on('click', saveContent);
		loadBlogEntry();
	}
	
	initEditBlogPage();
	
}

/*
 * Initializer
 */

$(function init() {
	if ($('#adminOverviewPage').length) {
		adminOverviewPage();
	}
	if ($('#adminEditBlog').length) {
		adminEditBlogPage(parseInt($('#adminEditBlog').data('id')));
	}
});
