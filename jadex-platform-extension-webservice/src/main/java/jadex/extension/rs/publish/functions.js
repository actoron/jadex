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
				if(types[i]=="")
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
			}
		}
		
		send(form.action, "post", names, vals, types);
		
		}
		catch(e)
		{
			var e2 = e;
		}
		return false;
	}
		
	function send(url, method, names, vals, types) 
	{
		var http = new XMLHttpRequest();
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
	
		http.send(multipart);
	}
	
</script>