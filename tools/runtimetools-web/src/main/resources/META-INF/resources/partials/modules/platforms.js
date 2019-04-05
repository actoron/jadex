//<![CDATA[

var app = angular.module('acplatforms', []);
app.controller('ConnPlats', [ '$scope', '$http',
	function($scope, $http) {
		getIntermediate($http, 'status/subscribeToConnections',
			function(response)
			{
				updatePlatform($scope, response.data);
			},
			function(response)
			{
				$scope.serverDown	= true;
			});
	}
]);

function getIntermediate($http, path, handler, error) 
{
	var	func	= function(response)
	{
		//alert("response: "+JSON.stringify(response));
		if(response.status!=202)	// ignore updatetimer commands
		{
			handler(response);
		}
		var callid = response.headers("x-jadex-callid");
		//alert("callid: "+callid+", "+JSON.stringify(response.data))
		if(callid!=null)
		{
			$http.get(path, {headers: {'x-jadex-callid': callid}})
				.then(func, error);
		}
	};
	$http.get(path).then(func, error);
}

function updatePlatform($scope, platform)
{
	var	found	= false;
	$scope.platforms = $scope.platforms===undefined ? [] : $scope.platforms;
	for(var i=0; i<$scope.platforms.length; i++)
	{
		found = $scope.platforms[i].platform.name==platform.platform.name
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
	}
}

//]]>
