/**
 *  Connection to server side puzzle service.
 */ 


/**
 *  Asyncronously invoke the jadex puzzle service.
 */
var newGame	= function(size) {
	console.log("new game");
	if(typeof jadex!='undefined') // Allow playing client-side only
	{
		jadex.getService("com.actoron.examples.puzzleng.IPuzzleService", SCOPE_SESSION).then(function(service) {
			console.log("service found: "+JSON.stringify(service));
			service.newGame(size).then(function()
			{
				console.log("service called");
			})
			.catch(function(err) 
	       	{
				console.log(err);
	//			alert(JSON.stringify(err));
	       	});
		})
		.catch(function(err) 
	   	{
			console.log(err);
		});
	}
}
