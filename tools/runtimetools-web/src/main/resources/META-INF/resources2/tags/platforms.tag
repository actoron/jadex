<platforms>

	<div class="actwtable section">
		<div if="{lang=='en'}">
			<h3 id="HConnectedPlatforms">Connected Platforms</h3>
			<p>This page shows a self-updating list of remote platforms known to this Jadex platform.</p>
		</div>
		<div if="{lang=='de'}">
			<h3 id="HConnectedPlatforms">Verbundene Plattformen</h3>
			<p>Diese Seite zeigt eine selbst-aktualisiernde Liste von Jadex-Plattformen.</p>
		</div>
		<table class="{down: serverdown}">
			<tbody>
				<tr>
					<th>Name</th>
					<!-- <th>Connected</th> 
					<th>Protocol</th>-->
				</tr>
				
				<tr each="{x in orderBy(platforms)}"> <!-- class="{connecting: !x.connected}" --> 
					<td><a href="#/platform/{x}">{x}</a></td>
					<!--  <td>{x.connected}</td>
					<td>{x.protocol}</td> -->
				</tr>
			</tbody>
		</table>
	</div>
	
	<script>
		var self = this;
		self.reversed = false;
		self.platforms = [];
		self.serverdown = false;
	
		// todo: order by name
		orderBy(data) 
		{ 
			var order = self.reversed ? -1 : 1;
			
			var res = data.slice().sort(function(a, b) 
			{ 
				return a===b? 0: a > b? order: -order 
			});
			
			/*res.forEach(function(q) 
			{ 
				console.log(q); 
			})*/ 
			return res; 
		}
		
		function getIntermediate(path, handler, error) 
		{
			var	func = function(resp)
			{
				if(resp.status!=202)	// ignore updatetimer commands
					handler(resp);

				var callid = resp.headers["x-jadex-callid"];
				if(callid!=null)
					axios.get(path, {headers: {'x-jadex-callid': callid}}, self.transform).then(func).catch(error); 
				return this.PROMISE_DONE;
			};
			axios.get(path, self.transform).then(func).catch(error);
		}
		
		function updatePlatform(platform, rem)
		{
			console.log("updatePlatform: "+platform+" "+rem);
			
			var	found = false;
			
			var i;
			for(i=0; i<self.platforms.length; i++)
			{
				found = self.platforms[i]===platform;
				if(found)
				{
					if(rem)
						self.platforms.splice(i, 1);
					break;
				}
			}
			
			if(!found && !rem)
				self.platforms.push(platform);
			
			self.update();
		}
		
		getIntermediate('webjcc/subscribeToPlatforms',
			function(resp)
			{
				updatePlatform(resp.data.service.name, resp.data.service.type);
				return this.PROMISE_DONE;
			},
			function(resp)
			{
				console.log("Err: "+JSON.stringify(resp));
				self.serverdown = true;
				self.update();
				return this.PROMISE_DONE;
			});
	</script>

</platforms>


