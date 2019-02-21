<starter>

	<!-- <h2>Starter {cid}</h2>  -->
	
	<div class="container">
		<h3>Available Component Models</h3>
		<table class="table table-bordered" class="modellist">
			<thead>
	    		<tr>
	    			<th>Model name</th>
	    		</tr>
	    	</thead>
			<tbody>
				<tr each="{x in orderBy(models)}">
					<td><a href="{getMethodPrefix()+'&methodname=createComponent&modelname=['+x+']'+'&contenttype=application/json'}">{x}</a></td>
				</tr>
			</tbody>
		</table>
	</div>
	
	<style>
	    #modellist {
			display: block;
			height : 300px;
			overflow-y : scroll;
		}
	</style>
	
	<script>
		//console.log("starter: "+opts);
		
		var self = this;
		self.cid = opts!=null? opts.cid: null;
		self.models = [];
		
		var myservice = "jadex.tools.web.IStarterWebService";
		
		getMethodPrefix()
		{
			return 'webjcc/invokeServiceMethod?cid='+self.cid+'&servicetype='+myservice;
		}
		
		// no args here
		axios.get(self.getMethodPrefix()+'&methodname=getComponentModels', self.transform).then(function(resp)
		{
			//console.log(resp.data);
			
			self.models = resp.data;
			self.update();
		});
			
		self.update();
		
		// todo: order by name
		orderBy(data) 
		{ 
			var order = self.reversed ? -1 : 1;
			
			var res = data.slice().sort(function(a, b) 
			{ 
				return a===b? 0: a > b? order: -order 
			});
			
			res.forEach(function(q) 
			{ 
				console.log(q); 
			}) 
			return res; 
		}
	</script>
</starter>