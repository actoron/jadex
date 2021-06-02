<cloudview>
	<div>
		<div id="cloudinfo">
			<h1 id="nodetitle"></h1>
			<p id="nodedescription"></p>
		</div>
		<div id="cloudgraph"></div>
	</div>
	
	<script>
		//console.log("security plugin started: "+opts);
		
		var self = this;
		
		self.cid = opts!=null? opts.cid: null;
		self.myservice = "jadex.tools.web.cloudview.IJCCCloudviewService";
		
		getMethodPrefix()
		{
			return 'webjcc/invokeServiceMethod?cid='+self.cid+'&servicetype='+self.myservice;
		}

		//self.cid = opts!=null? opts.cid: null;
		//var myservice = "jadex.tools.web.security.IJCCSecurityService";
		
		function VisGraph() {
			this.nodes = {};
			this.edges = [];
		
			this.genUid = function() {
				const base64chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'
				let ret = '';
				for (let i = 0; i < 21; ++i)
					ret += base64chars.charAt(Math.floor(Math.random * 64));
				return ret;
			}
		
			this.addNode = function(label, id) {
				id = id || genUid();
				this.nodes[id] = {id:id,label:label};
			}
		
			this.addGroupNode = function(label, group, id) {
				id = id || genUid();
				group = group || 'default';
				this.nodes[id] = {id:id,group:group,label:label};
			}
		
			this.removeNode = function(id) {
				this.nodes.splice(this.nodes.indexOf(id), 1);
			}
		
			this.addEdge = function(from, to) {
				this.edges.push({from: from, to: to});
			}
		
			this.removeEdge = function(from, to) {
				for (let i = 0; i < this.edges.length; ++i) {
					if (this.edges[i].from === from && this.edges[i].to === to)
					{
						this.edges.splice(i, 1);
						return;
					}
				}
			}
		
			this.getVisData = function() {
				let vnodearray = [];
				for (key in this.nodes)
					vnodearray.push(this.nodes[key]);
				let vnodes = new vis.DataSet(vnodearray);
				let vedges = new vis.DataSet(this.edges);
				let data = {
					nodes: vnodes,
					edges: vedges
				};
				return data;
			}
		
			this.defaultOptions = {
			//layout: { hierarchical: {enabled: true} },
				physics: {
					barnesHut: {
						gravitationalConstant: -6000,
						springConstant: 1.2,
						damping: 0.9
					}
				},
				groups: {
					network: {
						level: 0,
						icon: {
							face: '"FontAwesome"',
							code: '\uf6ff',
							color: 'black',
							//size: 200,
							//color: '#2ecc71'
						},
						//shadow: {enabled: true},
						borderWidth: 0,
						//shapeProperties: {borderDashes: true},
						shape: 'dot', mass: 20, size: 50, color: '#81ecec' //color: '#2ecc71'
					},
					platform: {
						level: 1,
						icon: {
							face: '"FontAwesome"',
							code:'\uf233',
							//size: 50,
							color: 'black'
							//color:'#3498db'
						},
						//shadow: {enabled: true},
						borderWidth: 0,
						shape: 'square', size: 30, color: '#74b9ff' //color:'#3498db'
					}
				},
				interaction: {
					navigationButtons: true
				}
			};
		}
		
		function start() {
			/*var bounds = self.cloudgraph.getBoundingClientRect();
			self.cloudgraph.style.height="calc (100vh - " + bounds.top +"px);";*/ 
			let graph = new VisGraph();
			/*graph.addGroupNode('network2', 'network', '0');
			graph.addGroupNode('network1', 'network', '1');
			graph.addGroupNode('jadexplatform1', 'platform', '2');
			graph.addGroupNode('jadexplatform2', 'platform', '3');
			graph.addGroupNode('jadexplatform3', 'platform', '4');
			graph.addGroupNode('jadexplatform4', 'platform', '5');
			graph.addEdge(0,2);
			graph.addEdge(0,3);
			graph.addEdge(1,3);
			graph.addEdge(1,4);
			graph.addEdge(1,5);*/
			
			//alert('2 ' + typeof self.service);
			
			self.service.getPlatformNetworks(self.cid).then(function(resp) {
				let pfs = Object.keys(resp.data);
				
				let knownnw = new Set();
				for (let pf of pfs){
					graph.addGroupNode(pf, 'platform', pf);
					let nws = resp.data[pf];
					for (let nw of nws){
						if (!knownnw.has(nw)){
							knownnw.add(nw);
							graph.addGroupNode(nw, 'network', nw);
						}
						graph.addEdge(pf, nw);
					}
				}
				let data = graph.getVisData();
				self.network = new vis.Network(self.cloudgraph, data, graph.defaultOptions);
				self.network.on('selectNode', function(context) {
					document.getElementById('nodetitle').innerHTML=context.nodes[0];
				});
				self.network.on('doubleClick', function(context) {
					alert('doubleClick');
				});
			}).catch(function (error) {
				alert(error);
			});
		};
		
		
		self.on('mount', function()
		{
			let css1 ="jadex/tools/web/cloudview/vis.min.css";
			let css2 ="jadex/tools/web/cloudview/cloudview.css";
			let js1 = "jadex/tools/web/cloudview/vis.min.js";
			let prefix = self.getMethodPrefix()+'&methodname=loadResource&args_0=';
			self.loadFiles([prefix+css1, prefix+css2], [prefix+js1], function() {
				self.cloudgraph = document.getElementById('cloudgraph');
				self.service=self.createProxy(self.cid, self.myservice);
				self.jccservice=self.createProxy(self.cid, 'IJCCWebService');
				start();
			});
		});
		self.on('unmount', function()
		{
			if (self.network != undefined)
				self.network.destroy()
		});
	</script>
</cloudview>