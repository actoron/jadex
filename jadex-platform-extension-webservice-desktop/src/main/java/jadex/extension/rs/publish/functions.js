<script type="text/javascript">
	
	function extract(form) 
	{
		var names = [];
		var vals = [];
		var types = [];
		var type;
		
		for(i=0; i<form.elements.length; i++)
		{
			var a = form.elements[i].tagName;
			if(form.elements[i].tagName.toUpperCase()=="INPUT"
				&& form.elements[i].name!="")
			{
				names[i] = form.elements[i].name;
				vals[i] = form.elements[i].value;
//				types[i] = form.elements[i].accept;
				if(form.elements[i].type=="file")
				{
					types[i] = "application/octet-stream";
				}
				else
				{
					types[i] = null;
				}
				
			}
			else if(form.elements[i].tagName.toUpperCase()=="SELECT")
			{
				type = form.elements[i].value;
			}
		}
		
		if(type==null)
		{
			type = "text/plain";
		}
		
		for(i=0; i<types.length; i++)
		{
			if(types[i]==null)
			{
				types[i] = type;
			}
			
			// replace empty string with null if json or xml
			if(vals[i]=="")
			{
				if(types[i]=="application/json")
				{
					vals[i] = "null";
				}
				else if(types[i]=="application/xml")
				{
					vals[i] = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
						+"<null xmlns=\"typeinfo:\"></null>";
				}
			}
		}
		
		var httpmethod = document.getElementById("httpmethod").innerHTML;
		send(form.action, httpmethod, names, vals, types, form, type);
		
		return false;
	}

	function send(url, method, names, vals, types, form, type) 
	{
		var to = 5000;
		
		var res = document.getElementById("result");
		if(res!=null)
			res.innerHTML = "";
		
		method = method.toLowerCase();
		
		// http://stackoverflow.com/questions/15185499/webkit-equivalent-to-firefoxs-moz-chunked-arraybuffer-xhr-responsetype
		// http://stackoverflow.com/questions/20319727/using-multipart-x-mixed-replace-with-xmlhttprequest
		// http://stackoverflow.com/questions/18472745/how-to-prevent-xmlhttprequest-from-buffering-the-entire-response
		var http = new XMLHttpRequest();
		// Only for firefox, does not work
//		http.responseType = "moz-chunked-text";
		
		// if get and has parameters in form, extract and add to url
		if("get"==method && names!=null && names.length>0)
		{
			url = url+"?";
			for(i=0; i<names.length; i++)
			{
				if(i>0)
					url = url+"&";
				url = url + names[i] + "=" + vals[i];
			}
		}
		
		http.open(method, encodeURI(url), true);
		
		var multipart = "";
	
		var boundary = Math.random().toString().substr(2);

		var textpost = "post"==method.toLowerCase();
		
		var accept = "";
		var had = [];
		
		if("text/plain"!=type)
			textpost = false;
		accept += type;
		had[0] = type;
		
		// determine accept header
		if(types.length>0)
		{
			var num = 1;
			
			for(i=0; i<types.length; i++)
			{
				if("text/plain"!=types[i])
				{
					textpost = false;
				}
				
				if(!contains(had, types[i]) && "multipart/form-data"!=types[i])
				{
					if(accept!="")
						accept += ",";
					accept += types[i];
					had[num++] = types[i];
				}
			}
			
			// else use default browser accept header
//			http.setRequestHeader("Accept", "text/html,application/json;q=0.9,*/*;q=0.8");
		}
		
		accept += ";q=0.9,*/*;q=0.8";
//		alert(accept);
		http.setRequestHeader("Accept", accept);
		http.setRequestHeader("Content-Type", accept); // assumption: sent params are of same type
			
//		http.onprogress = function () 
//		{
//			//readyState: headers received 2, body received 3, done 4
//			if(http.readyState!=2 && http.readyState!=3 && http.readyState!=4)
//				return;
//			if(http.readyState == 3 && http.status!=200)
//				return;
//			
//			alert(http.response);
////			$("#boo").append("<div>"+xhr.response.slice(nextLine) +"</div>");  
////			nextLine = xhr.response.length;
//		}
		
		var reshandler = function() 
		{
//			alert(http.readyState+" "+http.status+" "+http.responseText);
			
//			if(http.responseText!=null)
			if(http.readyState == 4) 
			{
//				alert("received: "+http.status)
				
				if(http.status == 200)
				{
	//				document.getElementById("content").innerHTML = http.responseText;
	//				document.title = response.pageTitle;
	//				window.history.pushState({"html":response.html,"pageTitle":response.pageTitle},"", urlPath);
	//				window.history.pushState("some string", "Test", url);
	//				window.location.replace(url);
	//				window.history.pushState("some string", "Test", url);
					
					var res = document.getElementById("result");
					if(res!=null)
					{
						res.innerHTML += http.responseText;
					}
					else
					{
						document.open();
						document.write(http.responseText);
						document.close();
					}
				}
				
//				if(http.status == 408)
//					alert("received timeout");
				
				var callid = http.getResponseHeader("x-jadex-callid");
				if(callid!=null)
				{
					http = new XMLHttpRequest(); 
					http.open(method, encodeURI(url), true);
					if(accept!=null)
					{
						http.setRequestHeader("Accept", accept);
						http.setRequestHeader("Content-Type", accept); // assumption: sent params are of same type
					}
					http.setRequestHeader("x-jadex-callid", callid);
					http.setRequestHeader("x-jadex-clienttimeout", to);
					http.onreadystatechange = reshandler;
					http.send(null);
				}
				var callid = http.getResponseHeader("x-jadex-callidfin");
				if(callid!=null)
				{
					alert("call finished: "+callid);
				}
				
//				try
//				{
//				var tas = form.getElementsByTagName("textarea");
//				if(tas==null || tas.length==0)
//				{
//					tas = [];
//					tas[0] = document.createElement("textarea");
//					form.appendChild(tas[0]);
//				}
//				tas[0].value = http.responseText;
					
//				alert(tas[0].innerHTML);
				
//				var cdata = document.createCDATASection(http.responseText);
//				tas[0].appendChild(cdata);
				
//				tas[0].innerHTML = "\u003c\u0021\u005bCDATA\u005b"+http.responseText+"\u005d\u005d\u003e";
//					"<![CDATA[ "+http.responseText+" ]]>";
//				var text = http.responseText.replace(/\</g, "&lt;").replace(/\>/g, "&gt;");
//				tas[0].innerHTML = text;
				
//				form.appendChild(tas[0]);
//				}
//				catch(err)
//				{
//					alert(err);
//				}
			}
		}
		http.onreadystatechange = reshandler;
		
		// set client timeout
		http.setRequestHeader("x-jadex-clienttimeout", to);
		if("get"==method)
		{
			http.send(null);
		}
		else if(textpost)
		{
			var fd = new FormData(form);
//			http.setRequestHeader("content-type", "application/x-www-form-urlencoded");
			http.send(fd);
		}
		else
		{
			http.setRequestHeader("content-type",
				"multipart/form-data; charset=utf-8; boundary=" + boundary);
			
			for(i=0; i<names.length; i++)
			{
				multipart += "--" + boundary
					+ "\r\nContent-Disposition: form-data; name=\u0022" + names[i] + "\u0022" 
					+ "\r\nContent-Type: "+ types[i]
					+ "\r\n\r\n"
					+ vals[i] 
					+ "\r\n";
			}
			multipart += "--" + boundary + "--\r\n";
//			alert(multipart);
			http.send(multipart);
		}
		

	}
	
	function contains(a, obj) 
	{
	    for(var i = 0; i < a.length; i++) 
	    {
	        if(a[i] === obj) 
	        {
	            return true;
	        }
	    }
	    return false;
	}

	
</script>