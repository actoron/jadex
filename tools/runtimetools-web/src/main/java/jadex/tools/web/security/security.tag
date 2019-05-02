<security>
	<div class="container-fluid">
		<div id="accordion">
		
			<div class="card m-2">
				<div class="card-header">
        			<h4 class="card-link" data-toggle="collapse" href="#collapseOne">General Settings</h4>
    			</div>
				<div id="collapseOne" class="collapse show" data-parent="#accordion">
					<div class="card-body">
						<div class="row m-1">
							<div class="col-6">
								<input type="checkbox" ref="usesecret" onclick="{useSecret}" checked="{secstate.useSecret}">Use secret</input>
							</div>
							<div class="col-6">
								<input type="checkbox" ref="printsecret" onclick="{printSecret}" checked="{secstate.printSecret}">Print secret</input>
							</div>
						</div>
      				</div>
    			</div>
  			</div>
			
			<div class="card m-2">
				<div class="card-header">
					<h4 class="collapsed card-link" data-toggle="collapse" href="#collapseTwo">Networks</h4>
				</div>
				<div id="collapseTwo" class="collapse" data-parent="#accordion">
					<div class="card-body">
						<div class="row m-1">
							<div class="col-12">
								<table class="table">
									<thead>
			    						<tr>
			      							<th scope="col">Network Name</th>
			      							<th scope="col">Network Secret</th>
									    </tr>
			  						</thead>
			  						<tbody>
			    						<tr each="{net in getNetworks()}">
			      							<td>{net[0]}</td>
			     							<td>{net[1]}</td>
									    </tr>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="card m-2">
				<div class="card-header">
					<h4 class="collapsed card-link" data-toggle="collapse" href="#collapseThree">Roles</h4>
				</div>
				<div id="collapseThree" class="collapse" data-parent="#accordion">
					<div class="card-body">
						<div class="row m-1">
							<div class="col-12">
								<table class="table">
									<thead>
			    						<tr>
			      							<th scope="col">Entity</th>
			      							<th scope="col">Role</th>
									    </tr>
			  						</thead>
			  						<tbody>
			    						<tr each="{roles in getRoles()}">
			      							<td>{roles[0]}</td>
			     							<td>{roles[1]}</td>
									    </tr>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="card m-2">
				<div class="card-header">
					<h4 class="collapsed card-link" data-toggle="collapse" href="#collapseFour">Name Authorities</h4>
				</div>
				<div id="collapseFour" class="collapse" data-parent="#accordion">
					<div class="card-body">
						<div class="row m-1">
							<div class="col-12">
								<table class="table">
									<thead>
			    						<tr>
			      							<th scope="col-5">Subject Common Name</th>
			      							<th scope="col-5">Subject Distinguished Name</th>
			      							<th scope="col-2">Type</th>
									    </tr>
			  						</thead>
			  						<tbody>
			    						<tr each="{na in getNameAuthorities()}">
			      							<td>{nameAuthorities[0]}</td>
			     							<td>{nameAuthorities[1]}</td>
			     							<td>{nameAuthorities[2]}</td>
									    </tr>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="card m-2">
				<div class="card-header">
					<h4 class="collapsed card-link" data-toggle="collapse" href="#collapseFive">Trusted Platform Names</h4>
				</div>
				<div id="collapseFive" class="collapse" data-parent="#accordion">
					<div class="card-body">
					</div>
				</div>
			</div>
		</div>
	
	</div>
	
	<script>
		//console.log("security: "+opts);
		
		var self = this;

		self.cid = opts!=null? opts.cid: null;
		var myservice = "jadex.tools.web.security.IJCCSecurityService";
		
		self.secstate = {};
		
		getMethodPrefix()
		{
			return 'webjcc/invokeServiceMethod?cid='+self.cid+'&servicetype='+myservice;
		}
		
		getNetworks()
		{
			return self.secstate!=null? self.secstate.networks: [];
		}
		
		getRoles()
		{
			return self.secstate!=null? self.secstate.roles: [];
		}
		
		getNameAuthorities()
		{
			return self.secstate!=null? self.secstate.nameAuthorities: [];
		}
		
		useSecret(e)
		{
			var val = self.refs.usesecret.value;
			if(self.secstate!=null)
				self.secstate.usesecret = val;
			axios.get(self.getMethodPrefix()+'&methodname=setUseSecret&args_0='+val+"&argtypes_0=boolean", self.transform).then(function(resp)
			{
				console.log("setUseSecret: "+resp.data);
			});
		}
		
		printSecret(e)
		{
			var val = self.refs.printsecret.value;
			if(self.secstate!=null)
				self.secstate.usesecret = val;
			axios.get(self.getMethodPrefix()+'&methodname=setPrintSecret&args_0='+val+"&argtypes_0=boolean", self.transform).then(function(resp)
			{
				console.log("setPrintSecret: "+resp.data);
			});
		}
		
		axios.get(self.getMethodPrefix()+'&methodname=getSecurityState', self.transform).then(function(resp)
		{
			console.log("ss: "+resp.data);
			self.secstate = resp.data;
			self.update();
		});
		
		self.update();
	</script>
</security>