//<![CDATA[

var app = angular.module('acqueries', []);
app.controller('Queries', [ '$scope', '$http',
	function($scope, $http) {
	$http.get('status/getQueries').then(function(response) {
			$scope.queries = response.data;
		});
	}
]);

//]]>
