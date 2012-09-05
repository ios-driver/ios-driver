$(document)
		.ready(
				function() {

					// offsetX Y set in the main page as global variable.
					var tree = $("#tree").jstree({
						"core" : {
							"animation" : 0,
							"load_open" : true
						},
						"json_data" : {
							"ajax" : {
								"url" : "tree"
							}
						},
						"themes" : {
							"theme" : "apple",
						// "dots" : false,
						// "icons" : false
						},
						"plugins" : [ "themes", "json_data", "ui" ]
					});

					tree.bind("select_node.jstree", function(e, data) {

					});

					tree.bind("hover_node.jstree", function(e, data) {
						if (!lock) {
							setSelected(data);
						}

					});

					var root;
					tree.bind("loaded.jstree", function(event, data) {
						root = tree.jstree('get_json')[0];
						tree.jstree("open_all");
					});
					tree.bind("refresh.jstree", function(event, data) {
						root = tree.jstree('get_json')[0];
						tree.jstree("open_all");
					});

					setSelected = function(node) {
						var rect;
						var type;
						var ref;
						var name;
						var label;
						var value;
						var l10n;

						if (node.metadata) { // from tree parsing, json node
							rect = node.metadata.rect;
							type = node.metadata.type;
							ref = node.metadata.reference;
							name = node.metadata.name;
							label = node.metadata.label;
							value = node.metadata.value;
							l10n = node.metadata.l10n
						} else { // from listener, jstree node
							rect = node.rslt.obj.data("rect");
							type = node.rslt.obj.data('type');
							ref = node.rslt.obj.data('reference');
							name = node.rslt.obj.data('name');
							label = node.rslt.obj.data('label');
							value = node.rslt.obj.data('value');
							l10n = node.rslt.obj.data('l10n');

						}
						tree.jstree('deselect_all');
						tree.jstree('select_node', '#' + ref);
						var translationFound = (l10n.matches != 0);

						highlight(rect.x, rect.y, rect.h, rect.w,
								translationFound);
						showDetails(type, ref, name, label, value, rect, l10n);
						showActions(type, ref);
					}

					showActions = function(type, ref) {
						// check action per type.
						$('#reference').html(
								"<input type='hidden' name='reference' value='"
										+ ref + "'>");
					}

					highlight = function(x, y, h, w, translationFound) {
						$('#highlight').css('left', (x + realOffsetX) + 'px')
								.css('top', (y + realOffsetY) + 'px').css(
										'height', h + 'px').css('width',
										w + 'px');
						var color;
						if (translationFound) {
							color = "blue";
						} else {
							color = "yellow";
						}
						$('#highlight').css("background-color", color);
					}

					showDetails = function(type, ref, na, label, value, rect,
							l10n) {
						var prettyL10N = "";

						if (l10n) {
							prettyL10N = "\n<b>L10N : </b>";
							var matches = l10n.matches;
							prettyL10N += "\nmatch: " + matches;

							if (matches > 0) {
								prettyL10N += "\nKEY: " + l10n.key;
								var langs = l10n.langs;
								for ( var name in langs) {
									var result = langs[name];
									for ( var a in result) {
										prettyL10N += "\n" + a + " : "
												+ result[a];
									}

								}
							}

						} else {
							prettyL10N = "no l10n for --" + name + "--";
						}

						$('#details').html(
								"<u>Details</u><pre>type:" + type
										+ "\nreference:" + ref + "\nname:" + na
										+ "\nlabel:" + label + "\nvalue:"
										+ value + "\nrect: x=" + rect.x + ",y="
										+ rect.y + ",h=" + rect.h + "w="
										+ rect.w + prettyL10N + "</pre>");

					};

					var root;
					$("#mouseOver")
							.mousemove(
									function(e) {

										if (!lock) {
											var x = e.pageX - realOffsetX;
											var y = e.pageY - realOffsetX;

											var finder = new CandidateFinder(x,
													y, root);
											var node = finder.getNode();
											if (node) {
												setSelected(node);
											} else {
												console
														.log('couldn t find element at '
																+ x
																+ ' , '
																+ y
																+ root);
											}
										}

									});

					$(document).keydown(function(e) {
						if (e.ctrlKey) {
							toggleLock();
						}
					});

					var lock = false;
					toggleLock = function() {
						lock = !lock;
					};

					function CandidateFinder(x, y, rootNode) {
						this.x = x;
						this.y = y;

						this.matchScore = -1;
						this.candidate = null;

						this.rootNode = rootNode;
						// (this.x , this.y) is inside the area covered by
						// this node.
						this._hasCorrectPosition = function(node) {
							var currentX = node.metadata.rect.x;
							var currentY = node.metadata.rect.y;
							var currentH = node.metadata.rect.h;
							var currentW = node.metadata.rect.w;

							if ((currentX <= this.x)
									&& (this.x <= (currentX + currentW))) {
								if ((currentY <= this.y)
										&& (this.y <= (currentY + currentH))) {
									return true;
								}
							}
							return false;

						};
						this._assignIfBetterCandidate = function(newNode) {
							if (this._hasCorrectPosition(newNode)) {
								var surface = (newNode.metadata.rect.h * newNode.metadata.rect.w);
								if (this.candidate) {
									if (surface < this.matchScore) {
										this.matchScore = surface;
										this.candidate = newNode;
									}
								} else {
									this.matchScore = surface;
									this.candidate = newNode;
								}
							}
						};

						this.getNode = function() {
							this._getCandidate(this.rootNode);
							return this.candidate;
						};

						this._getCandidate = function(from) {
							this._assignIfBetterCandidate(from);
							if (from.children) {
								for ( var i = 0; i < from.children.length; i++) {
									var child = from.children[i];
									this._getCandidate(child);
								}
							}
						};

					}

				});
var realOffsetX = 0;
var realOffsetY = 0;
configure = function(device, orientation) {

	realOffsetX = 25 + 42;
	realOffsetY = 25 + 42;

	var scale = 1;
	if (orientation === 'UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT') {
		$('#rotationCenter').css('-moz-transform', 'rotate(90deg)');
		$('#rotationCenter').css('left', (1108 * scale + 25) + 'px');
		$('#rotationCenter').css('top', 25 + 'px');

		$('#mouseOver').css('top', 25 + 42 + 'px');
		$('#mouseOver').css('left', 25 + 42 + 'px');
		$('#mouseOver').css('height', 768 + 'px');
		$('#mouseOver').css('width', 1024 + 'px');

	} else if (orientation === 'UIA_DEVICE_ORIENTATION_LANDSCAPELEFT') {
		$('#rotationCenter').css('-moz-transform', 'rotate(-90deg)');
		$('#rotationCenter').css('left', 25 + 'px');
		$('#rotationCenter').css('top', (852 * scale + 25) + 'px');

		$('#mouseOver').css('top', 25 + 42 + 'px');
		$('#mouseOver').css('left', 25 + 42 + 'px');
		$('#mouseOver').css('height', 768 + 'px');
		$('#mouseOver').css('width', 1024 + 'px');
	} else if (orientation === 'UIA_DEVICE_ORIENTATION_PORTRAIT') {
		$('#rotationCenter').css('-moz-transform', 'rotate(0deg)');
		$('#rotationCenter').css('left', 25 + 'px');
		$('#rotationCenter').css('top', 25 + 'px');

		$('#mouseOver').css('top', 25 + 42 + 'px');
		$('#mouseOver').css('left', 25 + 42 + 'px');
		$('#mouseOver').css('height', 1024 + 'px');
		$('#mouseOver').css('width', 768 + 'px');
	}else if (orientation === 'UIA_DEVICE_ORIENTATION_PORTRAIT_UPSIDEDOWN') {
		$('#rotationCenter').css('-moz-transform', 'rotate(180deg)');
		$('#rotationCenter').css('left', (852*scale)+25 + 'px');
		$('#rotationCenter').css('top', (1108*scale)+25 + 'px');

		$('#mouseOver').css('top', 25 + 42 + 'px');
		$('#mouseOver').css('left', 25 + 42 + 'px');
		$('#mouseOver').css('height', 1024 + 'px');
		$('#mouseOver').css('width', 768 + 'px');
	}
};
