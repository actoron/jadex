<cms>

	<h1>Starter {cid}</h1>
	
	<script>
		console.log("starter: "+opts);
		
		var self = this;
		self.cid = opts!=null? opts.cid: null;
		
		self.update();
		
		console.log("starter cid is: "+self.cid);
	</script>
</cms>