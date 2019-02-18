<starter>

	<h1>Starter {cid}</h1>
	
	<script>
		console.log("starter: "+opts);
		
		var self = this;
		self.cid = opts!=null? opts.cid: null;
		
		var my_service = "jadex.tools.web.IStarterService";
		
		
		self.update();
	</script>
</starter>