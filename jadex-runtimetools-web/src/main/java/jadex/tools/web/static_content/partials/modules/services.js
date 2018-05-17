//<![CDATA[

// Click to sort: https://scotch.io/tutorials/sort-and-filter-a-table-using-angular 

var cidToString	= function(cid) {
	var	cidparts	= cid.split(/[@\.]+/);	// Split at '@' and '.', cf. https://stackoverflow.com/questions/650022/how-do-i-split-a-string-with-multiple-separators-in-javascript
	return cidparts.length>1 ? cidparts[cidparts.length-1] +" ("+cid+")" : cid;
};
var app = angular.module('acservices', []);
app.controller('Services', [ '$scope', '$http',
	function($scope, $http) {
		$scope.cidOrder	= function(service) {
			var ret	= cidToString(service.providerId.name);
			// alert("order is: "+ret);
			return ret;
		};
		$http.get('status/getServices', 
			{params: {'scope': JSON.stringify(["global","network"])},	// Stringify otherwise angular adds multiple singlevalued parameter occurrences, grrr.
			 data: '',	// Otherwise angular removes content type header required for json unpacking of arg (TODO: Jadex bug, should use something different than contetn type for get?)
			 headers: {'Content-Type': 'application/json'}})
		.then(function(response) {
			$scope.services = response.data;
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
 *  Beatify cid representation for readability and sorting: platform (agent@platform).
 */
app.filter('cid', function() {
	return cidToString;
});

//]]>
