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
		
		send(form.action, "post", names, vals, types, form);
		
		return false;
	}

	function send(url, method, names, vals, types, form) 
	{
//		this.xmlHttpReq = new XMLHttpRequest();
		
//		var http = xmlHttpReq;
		var http = new XMLHttpRequest();
		
		http.open(method, url, true);
		
		var multipart = "";
	
		var boundary = Math.random().toString().substr(2);

		var textpost = "post"==method.toLowerCase();
		
		if(types.length>0)
		{
			var accept = "";
			var had = [];
			var num = 0;
			
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
			
			if(num!=0)
			{
				accept += ";q=0.9,*/*;q=0.8";
				alert(accept);
				http.setRequestHeader("Accept", accept);
			}
			// else use default browser accept header
//			http.setRequestHeader("Accept", "text/html,application/json;q=0.9,*/*;q=0.8");
		}
			
		http.onreadystatechange = function() 
		{
			if(http.readyState == 4 && http.status == 200) 
			{
//				document.getElementById("content").innerHTML = http.responseText;
//				document.title = response.pageTitle;
//				window.history.pushState({"html":response.html,"pageTitle":response.pageTitle},"", urlPath);
//				window.history.pushState("some string", "Test", url);
//				window.location.replace(url);
//				window.history.pushState("some string", "Test", url);

				document.open();
				document.write(http.responseText);
				document.close();
			
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
		
		if(textpost)
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