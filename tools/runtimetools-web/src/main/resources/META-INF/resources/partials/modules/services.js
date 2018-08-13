//<![CDATA[

// Click to sort: https://scotch.io/tutorials/sort-and-filter-a-table-using-angular 

var cidToString	= function(cid) {
	var	cidparts	= cid.split(/[@\.]+/);	// Split at '@' and '.', cf. https://stackoverflow.com/questions/650022/how-do-i-split-a-string-with-multiple-separators-in-javascript
	return cidparts.length>1 ? cidparts[cidparts.length-1] +" ("+cid+")" : cid;
};
var app = angular.module('acservices', []);
app.controller('Services', [ '$scope', '$http',
	function($scope, $http) {
		getIntermediate($http, 'status/subscribeToServices',
			function(response)
			{
				updateService($scope, response.data);
			},
			function(response)
			{
				$scope.superpeerDown	= true;
			});
	}
]);
app.controller('Queries', [ '$scope', '$http',
	function($scope, $http) {
		$http.get('status/getQueries',
			{params: {'scope': JSON.stringify(["global","network"])},	// Stringify otherwise angular adds multiple singlevalued parameter occurrences, grrr.
			 data: '',	// Otherwise angular removes content type header required for json unpacking of arg (TODO: Jadex bug, should use something different than contetn type for get?)
			 headers: {'Content-Type': 'application/json'}})
		.then(function(response) {
			$scope.queries = response.data;
		});
	}
]);

/**
 *  Beautify cid representation for readability and sorting: platform (agent@platform).
 */
app.filter('cid', function() {
	return cidToString;
});

function	updateService($scope, service)
{
	var	found	= false;
	$scope.services	= $scope.services===undefined ? [] : $scope.services;
	alert("Service: "+JSON.stringify(service));
/*	for(var i=0; i<$scope.platforms.length; i++)
	{
		found	= $scope.platforms[i].platform.name==platform.platform.name
			&& $scope.platforms[i].protocol==platform.protocol;
		if(found)
		{
			if(platform.connected===undefined)
			{
				$scope.platforms.splice(i,1);
			}
			else
			{
				$scope.platforms[i]	= platform;
			}
			break;
		}
	}
	
	if(!found)
	{
		$scope.platforms.push(platform);
	}*/
}
//]]>
