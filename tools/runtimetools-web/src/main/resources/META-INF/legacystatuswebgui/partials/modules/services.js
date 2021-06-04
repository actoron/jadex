//<![CDATA[

// Click to sort: https://scotch.io/tutorials/sort-and-filter-a-table-using-angular 

var app = angular.module('acservices', []);
app.controller('Services', [ '$scope', '$http',
	function($scope, $http) {
		getIntermediate($http, 'api/subscribeToServices',
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
		getIntermediate($http, 'api/subscribeToQueries',
			function(response)
			{
				updateQuery($scope, response.data);
			},
			function(response)
			{
				$scope.superpeerDown	= true;
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
		if(networks!=null)
		{
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
		}
		return nets!=null ? nets : "";
	}
});

function	updateService($scope, event)
{
	var	found	= false;
	$scope.services	= $scope.services===undefined ? [] : $scope.services;
	
//	alert("Service: "+JSON.stringify(service));
	for(var i=0; i<$scope.services.length; i++)
	{
		found	= $scope.services[i].name==event.service.name
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

function	updateQuery($scope, event)
{
	var	found	= false;
	$scope.queries	= $scope.queries===undefined ? [] : $scope.queries;
	
//	alert("Query: "+JSON.stringify(event));
	for(var i=0; i<$scope.queries.length; i++)
	{
		found	= $scope.queries[i].id==event.query.id;
		if(found)
		{
			// 0: added, 1: removed, 2: changed
			if(event.type==1)	// removed
			{
				$scope.queries.splice(i,1);
			}
			else	// added / changed
			{
				$scope.queries[i]	= event.query;
			}
			break;
		}
	}
	
	if(!found)
	{
		$scope.queries.push(event.query);
	}
}
//]]>
