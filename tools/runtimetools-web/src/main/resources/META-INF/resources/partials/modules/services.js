//<![CDATA[

// Click to sort: https://scotch.io/tutorials/sort-and-filter-a-table-using-angular 

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
	return function(cid) {
		var	cidparts	= cid.split(/[@\.]+/);	// Split at '@' and '.', cf. https://stackoverflow.com/questions/650022/how-do-i-split-a-string-with-multiple-separators-in-javascript
		return cidparts.length>1 ? cidparts[cidparts.length-1] +" ("+cid+")" : cid;
	}
});

/**
 *  Beautify network names representation for readability: exclude global and skip emtpy
 */
app.filter('networks', function() {
	return function(networks) {
		var nets	= null;
		networks.forEach(function(network)
		{
			if("___GLOBAL___".localeCompare(network)!=0)
			{
				if(nets==null)
				{
					nets = network;
				}
				else
				{
					nets += ", "+network
				}
			}
		});
		return nets==null ? "" : nets;
	}
});

function	updateService($scope, event)
{
	var	found	= false;
	$scope.services	= $scope.services===undefined ? [] : $scope.services;
	
//	alert("Service: "+JSON.stringify(service));
	for(var i=0; i<$scope.services.length; i++)
	{
		found	= $scope.services[i].serviceName==event.service.serviceName
			&& $scope.services[i].providerId.name==event.service.providerId.name;
		if(found)
		{
			// 0: added, 1: removed, 2: changed
			if(event.type==1)	// removed
			{
				$scope.services.splice(i,1);
			}
			else	// added / changed
			{
				$scope.services[i]	= event.service;
			}
			break;
		}
	}
	
	if(!found)
	{
		$scope.services.push(event.service);
	}
}
//]]>
