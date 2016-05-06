/**
 *  Connection to server side puzzle service.
 */ 


/**
 *  Asyncronously invoke the jadex puzzle service.
 */
var newGame	= function(size) {
	jadex.getService(null, "jadex.web.examples.puzzleng.IPuzzleService").then(function(service) {
//		alert("service found: "+service);
		service.newGame(size).then(function()
		{
//			alert("service called");
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
