var app = angular.module('app', []);

var SCOPE_PLATFORM = jadex.Scopes.SCOPE_PLATFORM;
var SCOPE_GLOBAL = jadex.Scopes.SCOPE_GLOBAL;
var SCOPE_SESSION = jadex.Scopes.SCOPE_SESSION;
var jadex = new jadex.Jadex();

var serclazz = "jadex.bridge.service.types.chat.IChatGuiService";
 
var ConnectionState = 
{
	UNCONNECTED : 0, 
	CONNECTING : 1,
	CONNECTED : 2
}

app.controller("ChatController", function($scope)
{
	$scope.messages = [];
	$scope.users = [];
	$scope.selected_users = [];
	$scope.nick = "";
	$scope.avatar = null;
	$scope.connected = ConnectionState.UNCONNECTED;
	$scope.ConnectionState = ConnectionState; // global variables not visible in angualr expressions
	$scope.document = document;
	
	this.postMessage = function(text)
	{
		// each user gets his own chat agent
		jadex.getService(serclazz, SCOPE_SESSION).then(function(service) 
		{
			var recs = [];
			for(var i=0; i<$scope.selected_users.length; i++)
			{
				recs.push($scope.selected_users[i].sid.providerId);	
			}
			
			service.message(text, recs, false).then(function()
	  		{
	  			//alert("posted message");	
	  			console.log("posted message: "+text);
	  		})
	  		.catch(function(err) 
	       	{
				alert(JSON.stringify(err));
	       	});
		})
		.catch(function(err) 
		{
			console.log(err);
	  		//alert('Error', JSON.stringify(err));
		});
	}
	
	$scope.postMessage = this.postMessage; //messageService.postMessage;
	
	function makeDataUrl(filename, data)
	{
		if(filename && data)
		{
			var datab64 = btoa(String.fromCharCode.apply(null, new Uint8Array(data)))
			var ext = filename.split('.').pop().toLowerCase();
			return "data:image/"+ext+";base64,"+datab64;
		}
		else
		{
			return null;
		}
	}
	
	// Get my nickname
	jadex.getService(serclazz, SCOPE_SESSION).then(function(service) 
	{
   		service.getNickName().then(function(nick)
		{
			console.log(nick);
			$scope.$apply($scope.nick = nick);   	
		})
		.catch(function(err) 
       	{
			console.log(err);
       	});
   	});
   	
	// Watches the nick variable to set it on server
   	$scope.$watch("nick", function(newValue, oldValue) 
   	{
    	if($scope.nick.length > 0) 
    	{
			jadex.getService(serclazz, SCOPE_SESSION).then(function(service) 
			{
				service.setNickName($scope.nick).then(function(res)
				{
					console.log("Changed nickname to: "+$scope.nick);
				})
				.catch(function(err) 
		       	{
					console.log(err);
		       	});
		   	});
    	}
  	});
   	
   	// Get my image
	jadex.getService(serclazz, SCOPE_SESSION).then(function(service) 
	{
   		service.getImage().then(function(avatar)
		{
			console.log(avatar);
			
			$scope.$apply($scope.avatar = makeDataUrl("a.jpg", avatar)); // hack, mimetype?  	
		})
		.catch(function(err) 
       	{
			console.log(err);
       	});
   	});
  	
  	// Upload Image
  	$scope.setImage = function()
  	{
  		var f = document.getElementById('avafile').files[0];
		r = new FileReader();
		r.onloadend = function(e)
		{
    		var data = e.target.result;
    		
    		$scope.$apply($scope.avatar = makeDataUrl(f.name, data));   
    		
    		//alert(data);
    		jadex.getService(serclazz, SCOPE_SESSION).then(function(service) 
			{
				service.setImage(data).then(function(res)
				{
					console.log("Changed nickname to: "+$scope.nick);
				})
				.catch(function(err) 
		       	{
					console.log(err);
		       	});
		   	});
    	}
 	 	//r.readAsBinaryString(f);
 	 	r.readAsArrayBuffer(f);
	}
  	
  	// Upload file
  	$scope.sendFile = function()
  	{
		if($scope.selected_users.length>0)
		{
	  		var f = document.getElementById('upfile').files[0];
			r = new FileReader();
			var cid = $scope.selected_users[0].sid.providerId;

			r.onloadend = function(e)
			{
	    		var data = e.target.result;
	    		
	    		//alert(data);
	    		jadex.getService(serclazz, SCOPE_SESSION).then(function(service) 
				{
					service.sendFile(f.name, data, cid).then(function(res)
					{
						console.log("sent: "+f.name);
					})
					.catch(function(err) 
			       	{
						console.log(err);
			       	});
			   	});
	    	}
	 	 	//r.readAsBinaryString(f);
	 	 	r.readAsArrayBuffer(f);
		}
	}
   	
	// Subscribe for chat events (messages, user state changes, etc.) 
  	function connect()
  	{
		jadex.getService(serclazz, SCOPE_SESSION).then(function(service) 
		{
			$scope.$apply($scope.connected = ConnectionState.CONNECTING);   
			var prom = service.subscribeToEvents(function(event)
	   		{
				$scope.$apply($scope.connected = ConnectionState.CONNECTED);   
	   			if(event.type=="message")
	   			{
	   				$scope.$apply($scope.messages.push(event));
					//div.innerHTML = div.innerHTML + '</br><b>' + event.nick + ":</b> " + event.value;	
	   			}
	   			console.log(event);
	   		});
	   	       	
	   		prom.then(function(res)
			{
	   			$scope.$apply($scope.connected = ConnectionState.UNCONNECTED);   	
			  	console.log(res);
			  	window.setTimeout(connect, 3000);
			})
			.catch(function(err) 
	       	{
				$scope.$apply($scope.connected = ConnectionState.UNCONNECTED);   	
				console.log(err);
				window.setTimeout(connect, 3000);
	       	});
		});
  	};
  	connect();
	
  	var cnt = 0;
  	
	// Search the current users 
  	function updateUsers()
  	{
		jadex.getService(serclazz, SCOPE_SESSION).then(function(service) 
		{
			console.log("updateUsers: "+cnt++);
			$scope.users = [];
			var prom = service.findUsers(function(ser)
       		{
       			//users.push(ser);
//       		console.log(ser);
       			ser.getNickName().then(function(res)
           		{
//    				console.log(res);
    				$scope.$apply($scope.users.push({name: res, sid: ser.serviceId}));
					//div.innerHTML = div.innerHTML + '</br>' + res;	
    			})
    			.catch(function(err) 
    	       	{
    				console.log(err);
    	       	});
       		});
   	       	
       		prom.then(function(res)
			{
			  	console.log(res);
//			  	window.setTimeout(updateUsers(), 10000);
			  	// Must be called without () to be invoked in interval!
			  	window.setTimeout(updateUsers, 10000);
			})
			.catch(function(err) 
	       	{
	       		//alert(JSON.stringify(err));
				window.setTimeout(updateUsers, 10000);
	       	});
		});
  	};
  	updateUsers();
});



