<script type="text/javascript">
	
	function extract(form) 
	{
		var names = [];
		var vals = [];
		var types = [];
		
		for(i=0; i<form.elements.length; i++)
		{
			var a = form.elements[i].tagName;
			if(form.elements[i].tagName.toUpperCase()=="INPUT"
				&& form.elements[i].name!="")
			{
				names[i] = form.elements[i].name;
				vals[i] = form.elements[i].value;
				types[i] = form.elements[i].accept;
				if(types[i]==null || types[i]=="")
				{
					if(form.elements[i].type=="file")
					{
						types[i] = "application/octet-stream";
					}
					else
					{
						types[i] = "text/plain";
					}
				}
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
		}
		
		send(form.action, "post", names, vals, types);
		
		return false;
	}

//	function send(url, method, names, vals, types) 
//	{
//		var xmlHttpReq;
//	
//		var self = this;
//		// Mozilla/Safari
//		if(window.XMLHttpRequest) 
//		{
//		    self.xmlHttpReq = new XMLHttpRequest();
//		}
//		// IE
//		else if (window.ActiveXObject) 
//		{
//		    self.xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
//		}
//	
//		self.xmlHttpReq.open("POST", url, true);
//		self.xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
//		self.xmlHttpReq.setRequestHeader("Content-length", 22);
//		self.xmlHttpReq.send(null);//"?YourQueryString=Value");
//	}
	
	function send(url, method, names, vals, types) 
	{
		this.xmlHttpReq = new XMLHttpRequest();
		
		var http = xmlHttpReq;
//		var http = new XMLHttpRequest();
		var multipart = "";
	
		http.open(method, url, true);
	
		var boundary = Math.random().toString().substr(2);
			
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
		
//		alert(multipart);
		
		http.onreadystatechange = function() 
		{
			if(http.readyState == 4 && http.status == 200) 
			{
//				document.getElementById("content").innerHTML = http.responseText;
//				document.title = response.pageTitle;
//				window.history.pushState({"html":response.html,"pageTitle":response.pageTitle},"", urlPath);
				window.history.pushState("some string", "Test", url);
//				document.open();
//				window.location.replace(url);
				document.write(http.responseText);
//				document.close();
			}
		}
	
		http.send(multipart);
	}
	
</script>