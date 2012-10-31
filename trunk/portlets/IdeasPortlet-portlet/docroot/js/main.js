AUI.add(
	'aoml-ratings',
	function(A) {
		var Lang = A.Lang;

		var EMPTY_FN = Lang.emptyFn;

		var EVENT_INTERACTIONS_RENDER = ['focus', 'mousemove'];

		var TPL_LABEL_SCORE = '{desc} {totalEntries} {voteLabel}';

		var buffer = [];

		var Ratings = A.Component.create(
			{
				ATTRS: {
					averageScore: {},
					className: {},
					classPK: {},
					namespace: {},
					size: {},
					totalEntries: {},
					totalScore: {},
					type: {},
					uri: {},
					yourScore: {}
				},

				EXTENDS: A.Base,

				prototype: {
					initializer: function() {
						var instance = this;

						instance._renderRatings();
					},

					_bindRatings: function() {
						var instance = this;

						instance.ratings.after('itemSelect', instance._itemSelect, instance);
					},

					_convertToIndex: function(score) {
						var instance = this;

						var scoreIndex = -1;

						if (score == 1.0) {
							scoreIndex = 0;
						}
						else if (score == -1.0) {
							scoreIndex = 1;
						}

						return scoreIndex;
					},

					_fixScore: function(score) {
						var instance = this;

						var prefix = '';

						if (score > 0) {
							prefix = '+';
						}

						return prefix + score;
					},

					_getLabel: function(desc, totalEntries, ratingScore) {
						var instance = this;

						var voteLabel = '';

						if (totalEntries == 1) {
							voteLabel = Liferay.Language.get('vote');
						}
						else {
							voteLabel = Liferay.Language.get('votes');
						}

						return Lang.sub(
							TPL_LABEL_SCORE,
							{
								desc: desc,
								totalEntries: totalEntries,
								voteLabel: voteLabel
							}
						);
					},

					_itemSelect: EMPTY_FN,

					_renderRatings: EMPTY_FN,

					_sendVoteRequest: function(url, score, callback) {
						var instance = this;

						A.io.request(
							url,
							{
								data: {
									className: instance.get('className'),
									classPK: instance.get('classPK'),
									p_auth: Liferay.authToken,
									p_l_id: themeDisplay.getPlid(),
									score: score
								},
								dataType: 'json',
								on: {
									success: A.bind(callback, instance)
								}
							}
						);
					},

					_showScoreTooltip: function(event) {
						var instance = this;

						var ratingScore = instance.ratingScore;

						var message = '';

						var stars = ratingScore.get('selectedIndex') + 1;

						if (stars == 1) {
							message = Liferay.Language.get('star');
						}
						else {
							message = Liferay.Language.get('stars');
						}

						Liferay.Portal.ToolTip.show(event.currentTarget, stars + ' ' + message);
					},

					_updateAverageScoreText: function(averageScore) {
						var instance = this;

						var ratingScore = instance.ratingScore;

						var firstImage = ratingScore.get('boundingBox').one('img.aui-rating-element');

						if (firstImage) {
							var averageRatingText = Lang.sub(
								Liferay.Language.get('the-average-rating-is-x-stars-out-of-x'),
								[averageScore, instance.get('size')]
							);

							firstImage.attr('alt', averageRatingText);
						}
					}
				},

				register: function(config) {
					var instance = this;

					var containerId = config.containerId;

					var container = containerId && document.getElementById(config.containerId);

					if (container) {
						buffer.push(
							{
								config: config,
								container: A.one(container)
							}
						);

						instance._registerTask();
					}
					else {
						instance._registerRating(config);
					}
				},

				_registerRating: function(config) {
					var instance = this;

					var ratings = Liferay.Ratings.ThumbRating;

					var ratingInstance = new ratings(config);

					instance._INSTANCES[config.id || config.namespace] = ratingInstance;

					return ratingInstance;
				},

				_registerTask: A.debounce(
					function() {
						A.Array.each(
							buffer,
							function(item, index, collection) {
								var handle = item.container.on(
									EVENT_INTERACTIONS_RENDER,
									function(event) {
										handle.detach();

										Ratings._registerRating(item.config);
									}
								);
							}
						);

						buffer.length = 0;
					},
					100
				),

				_INSTANCES: {},

				_thumbScoreMap: {
					'-1': 0,
					'down': -1,
					'up': 1
				}
			}
		);

		var IdeasRating = A.Component.create(
			{
				EXTENDS: Ratings,

				prototype: {
					_renderRatings: function() {
						var instance = this;

						if (themeDisplay.isSignedIn()) {
							var description = instance._fixScore(instance.get('totalScore'));

							var totalEntries = instance.get('totalEntries');
							var averageScore = instance.get('averageScore');
							var size = instance.get('size');
							var yourScore = instance.get('yourScore');

							var label = instance._getLabel(description, totalEntries, averageScore);

							var yourScoreIndex = instance._convertToIndex(yourScore);

							var namespace = instance.get('namespace');

							instance.ratings = new A.IdeasRating(
								{
									boundingBox: '#' + namespace + 'ratingIdeas',
									label: label,
									srcNode: '#' + namespace + 'ratingIdeasContent'
								}
							).render();

							instance._bindRatings();

							instance.ratings.select(yourScoreIndex);
						}
					},

					_itemSelect: function(event) {
						var instance = this;

						var uri = instance.get('uri');
						var value = instance.ratings.get('value');

						var score = Liferay.Ratings._thumbScoreMap[value];

						instance._sendVoteRequest(uri, score, instance._saveCallback);
					},

					_saveCallback: function(event, id, obj) {
						var instance = this;

						var xhr = event.currentTarget;

						var json = xhr.get('responseData');
						var score = Math.round(json.totalEntries * json.averageScore);

						var description = instance._fixScore(score);
						var label = instance._getLabel(description, json.totalEntries);

						instance.ratings.set('label', label);
					}
				}
			}
		);

		Ratings.IdeasRating = IdeasRating;

		Liferay.Ratings = Ratings;
	},
	'',
	{
		requires: ['aui-io-request', 'aui-rating']
	}
);