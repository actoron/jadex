<security>
	<div class="container-fluid">
		<div class=" m-2 p-2">
			<div class="row m-1">
				<div class="col-12">
					<h3>General Settings</h3>
				</div>
			</div>
			<div class="row m-1">
				<div class="col-6">
					<input type="checkbox" ref="usesecret" onclick="{useSecret}">Use secret</input>
				</div>
				<div class="col-6">
					<input type="checkbox" ref="printsecret" onclick="{printSecret}">Print secret</input>
				</div>
			</div>
		 </div>
		 <div class=" m-2 p-2">
			<div class="row m-1">
				<div class="col-12">
					<h3>Networks</h3>
				</div>
			</div>
		</div>
		<div class=" m-2 p-2">
			<div class="row m-1">
				<div class="col-12">
					<h3>Roles</h3>
				</div>
			</div>
		</div>
		<div class=" m-2 p-2">
			<div class="row m-1">
				<div class="col-12">
					<h3>Name Authorities</h3>
				</div>
			</div>
		</div>
		<div class=" m-2 p-2">
			<div class="row m-1">
				<div class="col-12">
					<h3>Trusted Platform Names</h3>
				</div>
			</div>
		</div>
	</div>
	
	<script>
		//console.log("security: "+opts);
		
		var self = this;

		self.cid = opts!=null? opts.cid: null;
		var myservice = "jadex.tools.web.security.IJCCSecurityService";
		
		getMethodPrefix()
		{
			return 'webjcc/invokeServiceMethod?cid='+self.cid+'&servicetype='+myservice;
		}
		
		useSecret(e)
		{
			var val = self.refs.usesecret.value;
			axios.get(self.getMethodPrefix()+'&methodname=setUseSecret&args_0='+val+"&argtypes_0=boolean", self.transform).then(function(resp)
			{
				console.log("setUseSecret: "+resp.data);
			});
		}
		
		printSecret(e)
		{
			var val = self.refs.printsecret.value;
			axios.get(self.getMethodPrefix()+'&methodname=setPrintSecret&args_0='+val+"&argtypes_0=boolean", self.transform).then(function(resp)
			{
				console.log("setPrintSecret: "+resp.data);
			});
		}
		
		self.update();
	</script>
</security>