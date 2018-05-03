//<![CDATA[

var app = angular.module('acplatforms', []);
app.controller('ConnPlats', [ '$scope', '$http',
	function($scope, $http) {
		var path	= 'status/getConnectedPlatforms';
		$http.get('status/getConnectedPlatforms').then(function(response) {
			$scope.platforms = response.data;
		});
	}
]);

//]]>
