var msgser = "com.actoron.shortmessages.IShortMessageService";
var userser = "com.actoron.shortmessages.IUserService";
var clientser = "com.actoron.shortmessages.IClientService";
var token = null;
var loggedinemail = null;
var loggedinname = null;
var sid = null;

var messages = [];
var followers = [];
var followeds = [];
var users = [];

window.onload = function()
{
	updateUI();
}

function login(email, pass)
{
	// send a message to the followers
	//var user = {email: email, password: pass, __classname:"com.actoron.shortmessages.User"};
	var user = createUser(null, email, pass);
    jadex.getService(userser, Scopes.SCOPE_GLOBAL).then(function(service) 
	{
		service.login(user).then(function(tok)
  		{
			loggedinemail = email;
			token = tok;
			console.log("logged in successfully, token is: "+token);
			
  			//jadex.provideService(clientser, null, token, 
			jadex.provideService(clientser, null, email, 
			{
  				receiveMessage: function(message)
  				{
  					messages.push(message);
  					updateUI();
  				},
  				
  				followersChanged: function(fols)
  				{
					followers = fols;
					updateUI();
  				},
  				
  				//followedsChanged: function(flds)
  				//{
  				//	followeds = flds;
  				//	updateUI();
  				//},
  				
  				onlineStateChanged: function(user)//, online)
  				{
  					var changed = false;
  					for(var i=0; i<followers.length; i++)
  					{
  						if(user.email===followers[i].email)
  						{
  							followers[i].online = user.online;
  							changed = true;
  							break;
  						}
  					}
  					for(var i=0; i<followeds.length; i++)
  					{
  						if(user.email===followeds[i].email)
  						{
  							followeds[i].online = user.online;
  							changed = true;
  							break;
  						}
  					}
  					if(changed)
  						updateUI();
  				},
  				
  				userAdded: function(user)
  				{
  					users.push(user);
  					updateUI();
  				},
  				
  				// Generic invocation method
  				invoke: function(methodname, args)
  				{
	  				console.log("received a client service call: "+methodname, args);
  				}
        	})
        	.then(function(mysid)
        	{
        		sid = mysid;
        		console.log("providing client service: "+token);
        	});
		
			refreshAll();
  		});
	});
}

function logout()
{
	if(token)
	{
		jadex.getService(userser, Scopes.SCOPE_GLOBAL).then(function(service) 
	   	{
	    	service.logout(token).then(function()
			{
	    		console.log("logged out: "+token);
	    		var ftoken = token;
	    		token = null;
	    		
	    		if(sid!=null)
	    		{
		    		jadex.unprovideService(sid).then(function()
		    	    {
		    	    	console.log("unproviding client service: "+ftoken);
		    	    	updateUI();
		    	    });
	    		}
	    		else
	    		{
	    			console.log("could not unprovide service: "+ftoken);
	    	    	updateUI();
	    		}
			});
	   	});
	}
}

function sendMessage(text)
{
	if(token==null)
	{
		alert("Login required to post messages");
	}
	else
	{
		// send a message to the followers
		jadex.getService(msgser, Scopes.SCOPE_GLOBAL).then(function(service) 
		{
   			service.sendMessage(text, token).then(function()
	  		{
	  			console.log("sent message");
	  		});
		});
	}
}

function registerUser(name, email, pass)
{
	if(name && email && pass)
	{
		var user = createUser(name, email, pass);
		
   		jadex.getService(userser, Scopes.SCOPE_GLOBAL).then(function(service) 
		{
   			service.register(user).then(function()
	  		{
   				console.log("New user was registered: "+user.email);	
   				document.getElementById('rresult').innerHTML = "Registered successfully: "+user.email;
	  		})
	  		.catch(function(err)
	  		{
	  			console.log("new user register error: "+err);
	  			document.getElementById('rresult').innerHTML = "Registered error: "+toString(err);
	  		});
		});
	}
	else
	{
		document.getElementById('rresult').innerHTML = "All fields require values";
	}
}

function addFollowed(email)
{
	var followed = getUserByEmail(email);
	
	if(followed!=null)
	{
   		jadex.getService(userser, Scopes.SCOPE_GLOBAL).then(function(service) 
		{
   			service.addFollowed(token, followed).then(function()
	  		{
   				console.log("I follow now: "+followed.name+" "+email);	
   				
   				followeds.push(followed);
   				
   				updateUI();
	  		});
		});
	}
	else
	{
		console.log("Did not find user: "+email);
	}
}

function removeFollowed(email)
{
	var followed = getUserByEmail(email);
	
	if(followed!=null)
	{
   		jadex.getService(userser, Scopes.SCOPE_GLOBAL).then(function(service) 
		{
   			service.removeFollowed(token, followed).then(function()
	  		{
   				console.log("I don't follow now: "+followed.name+" "+email);	
   				
   				SUtil.removeObject(followed, followeds, userEquals);
   				
   				updateUI();
	  		});
		});
	}
	else
	{
		console.log("Did not find user: "+email);
	}
}


function userEquals(user1, user2)
{
	return user1.email===user2.email;
}

function getUserByEmail(email)
{
	var ret = null;
	for(var i=0; i<users.length; i++)
	{
		if(email==users[i].email)
		{
			ret = users[i];
			break;
		}
	}
	return ret;
}

function refreshAll()
{
	if(token!=null)
	{
		jadex.getService(userser, Scopes.SCOPE_GLOBAL).then(function(service) 
   		{
			// Fetch initial followers
			if(loggedinemail!=null)
			{
				service.getUserByEmail(loggedinemail).then(function(user)
		        {
					//console.log("followers received: "+fols);
					loggedinname = user.name;
					updateUI();
		        });
			}
			
    		// Fetch initial followers
			service.getFollowers(token).then(function(fols)
	        {
				//console.log("followers received: "+fols);
				followers = fols;
				updateUI();
	        });
			
			// Fetch people that I follow
			service.getFolloweds(token).then(function(flds)
	        {
				//console.log("followeds received: "+flds);
				followeds = flds;
				updateUI();
	        });
			
			// Fetch initial messages
			jadex.getService(msgser, Scopes.SCOPE_GLOBAL).then(function(mser) 
		   	{
				mser.getMessages(token).then(function(msgs)
   			    {
					//console.log("messages received: "+msgs);
					messages = msgs;
					updateUI();
   			    });
		   	});
			
			// Fetch all users
			jadex.getService(userser, Scopes.SCOPE_GLOBAL).then(function(service) 
	        {
	        	service.getAllUsers(token).then(function(usrs)
	        	{
	        		//console.log("users received: "+usrs);
	        		users = usrs;
	        		updateUI();
	        	});
	        });
		});
	}
}

function createUser(name, email, pass)
{
	//return {name: name, email: email, password: pass, __classname:"com.actoron.shortmessages.User"};
	return {name: name, email: email, password: pass};
}

function toString(err)
{
	return typeof err==="string"? err:  JSON.stringify(err);
}

//-------- GUI methods --------

function updateUI()
{
	var elems = document.getElementsByClassName('loggedin')
	for(var i=0; i<elems.length; i++)
	{
		elems[i].style.display = token? "block": "none";
	}
	elems = document.getElementsByClassName('loggedout')
	for(var i=0; i<elems.length; i++)
	{
		elems[i].style.display = token? "none": "block";
	}
	
	setLoggedIn();
	setMessages();
	setFollowers();
	setFolloweds();
	setAddFollowed();
}

function setLoggedIn()
{
	var elem = document.getElementById("loggedinas");
	elem.innerHTML = loggedinname? "Logged in as "+loggedinname: "";
}

function setFollowers()
{
	var div = document.getElementById('followers');
	
	div.innerHTML = "";
	for(var i=0; i<followers.length; i++)
	{
		var str = followers[i].name;
		if(followers[i].online)
			str = "<b>"+str+"</b>"
		div.innerHTML = div.innerHTML + str;
		if(i+1<followers.length)
			div.innerHTML = div.innerHTML + "</br>";	
	}
}

function setFolloweds()
{
	var div = document.getElementById('followeds');
	
	div.innerHTML = "";
	for(var i=0; i<followeds.length; i++)
	{
		var str = "<a href=\"javascript:removeFollowed('"+followeds[i].email+"');\">"+followeds[i].name+"</a>";
		if(followeds[i].online)
			str = "<b>"+str+"</b>"
		div.innerHTML = div.innerHTML + str;
		if(i+1<followeds.length)
			div.innerHTML = div.innerHTML + "</br>";	
	}
}

function addMessage(message)
{
	var div = document.getElementById('messages');
	div.innerHTML = div.innerHTML + message.sender.name + " ("+formatDate(message.date)+"): "+message.text;
	div.innerHTML = div.innerHTML + "</br>";
}

function setMessages()
{
	var div = document.getElementById('messages');
	div.innerHTML = "";
	for(var i=0; i<messages.length; i++)
	{
		div.innerHTML = div.innerHTML + messages[i].sender.name + " ("+formatDate(messages[i].date)+"): "+messages[i].text;
		if(i+1<messages.length)
			div.innerHTML = div.innerHTML + "</br>";	
	}
}

function setAddFollowed()
{
	var sel = document.getElementById('addtofollow');
	sel.innerHTML = "";		
	var usr = users.slice();
	for(var i=0; i<users.length; i++)
	{
		if(loggedinemail!=users[i].email && !SUtil.containsObject(users[i], followeds, userEquals))
		{
			sel.innerHTML = sel.innerHTML + "<option value=\""+users[i].email+"\">"+users[i].name+"</option>";
		}
	}
}

function formatDate(date) 
{
	var minutes = date.getMinutes();
	minutes = minutes < 10 ? '0'+minutes : minutes;
	return date.getMonth()+1 + "/" + date.getDate() + "/" + date.getFullYear() + "  " + date.getHours() + ':' + minutes;
}
