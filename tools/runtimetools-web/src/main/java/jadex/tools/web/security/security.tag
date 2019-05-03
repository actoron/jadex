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
			    						<tr class="d-flex">
			      							<th class="col-4" scope="col">Network Name</th>
			      							<th class="col-8" scope="col">Network Secret</th>
									    </tr>
			  						</thead>
			  						<tbody>
			    						<tr class="d-flex" each="{net in getNetworks()}">
			      							<td class="col-4">{net[0]}</td>
			     							<td class="col-8">{net[1]}</td>
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
			      							<td onclick="{selectRole}">{roles[0]}</td>
			     							<td onclick="{selectRole}">{roles[1]}</td>
									    </tr>
									</tbody>
								</table>
							</div>
						</div>
						<div class="row m-1">
							<div class="col-4">
								<input type="text" placeholder="Entity" ref="entity" onchange="{update}" required>
							</div>
							<div class="col-4">
								<input type="text" placeholder="Role" ref="role" onchange="{update}" required>
							</div>
							<div class="col-4">
								<button type="button" class="btn" onclick="{addRole}" disabled="{isRoleDisabled()}">Add</button>
								<button type="button" class="btn" onclick="{removeRole}" disabled="{isRoleDisabled()}">Remove</button>
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
			      							<td>{na[0]}</td>
			     							<td>{na[1]}</td>
			     							<td>{na[2]}</td>
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
						<div class="row m-1">
							<div class="col-12">
								<table class="table">
									<thead>
			    						<tr>
			      							<th scope="col-5">Trusted Platform Name</th>
									    </tr>
			  						</thead>
			  						<tbody>
			    						<tr each="{na in getTrustedPlatformNames()}">
			      							<td>{na}</td>
									    </tr>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	
		<div class="row m-1">
			<div class="col">
				<button type="button" class="btn btn-success" onclick="{refresh}">Refresh</button>
			</div>
		</div>
	</div>
	
	<style>
		table {
			overflow-wrap: break-word;
			table-layout: fixed
		}
	</style>
	
	<script>
		console.log("security: "+opts);
		
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
		
		getTrustedPlatformNames()
		{
			return self.secstate!=null? self.secstate.trustedPlatformNames: [];
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
		
		refresh()
		{
			axios.get(self.getMethodPrefix()+'&methodname=getSecurityState', self.transform).then(function(resp)
			{
				console.log("ss: "+resp.data);
				self.secstate = resp.data;
				self.update();
			});
		}
		
		addRole(e)
		{
			var en = self.refs.entity.value;
			var ro = self.refs.role.value;
			
			console.log("add role: "+en+" "+ro);
			
			axios.get(self.getMethodPrefix()+'&methodname=addRole&args_0='+en+'&args_1='+ro, self.transform).then(function(resp)
			{
				console.log("added role: "+en+" "+ro);
				self.refresh();
			});
		}
		
		removeRole(e)
		{
			var en = self.refs.entity.value;
			var ro = self.refs.role.value;
			
			console.log("remove role: "+en+" "+ro);
			
			axios.get(self.getMethodPrefix()+'&methodname=removeRole&args_0='+en+'&args_1='+ro, self.transform).then(function(resp)
			{
				console.log("removed role: "+en+" "+ro);
				self.refresh();
			});
		}
		
		selectRole(e)
		{
			//console.log(e);
			self.refs.entity.value = e.item.roles[0];
			self.refs.role.value = e.item.roles[1];
		}
		
		isRoleDisabled()
		{
			var ret = self.refs.entity.value.length==0 || self.refs.role.value.length==0;
			//console.log("isRoleDis: "+ret);
			return ret;
		}
		
		self.refresh();
		//self.update();
	</script>
</security>