(function() {
	let app = {};
	app.lang = {
		listeners: new Set(),
		lang: "en",
		translationtable: {},
		translate: function(text) 
		{
			let ret;
			if(this.lang !== "en")
			{
				let langtl = this.translationtable[this.lang];
				if (langtl)
					ret = langtl[text];
			}
			//console.log('translating: ' + text + ' result ' + ret);
			
			if (!ret)
				ret = text;
			return ret;
		},
		t: function(text) 
		{
			return this.translate(text);
		},
		getLang: function() 
		{
			return this.lang;
		},
		getLanguage: function() 
		{
			// deprecated
			return this.lang=='de'? 0: 1;
		},
		getFlagUrl: function() 
		{
			if (this.lang === 'de')
				return 'images/language_de.png';
			else
				return 'images/language_en.png';
		},
		setLanguage: function(newLang)
		{
			//console.log("setLang called");
			//console.trace();
			let self = app.lang;
			//console.log('listeners ' + self.listeners)
			let table = self.translationtable;
			let ret = new Promise((resolve, reject) => {
				//console.log('updating lang from ' + self.lang + ' to ' + newLang);
				if (newLang == 'en' || newLang in table)
				{
					//console.log('lang available, switch');
					self.lang = newLang;
					resolve();
					for(let lis of self.listeners)
					{
						lis.requestUpdate();
					}
				}
				else
				{
					// Try to get language file
					axios.get('/language/' + newLang + '.txt').then(function(resp) 
					{
						console.log('lang loaded: ' + newLang);
						table[newLang] = {};
						let lines = resp.data.split('\n').forEach(line => 
						{
							let ind = line.indexOf('#');
							if (ind !== 0)
							{
								ind = line.indexOf('|');
								if (ind !== -1)
								{
									let orig = line.substring(0, ind);
									let tl = line.substring(ind + 1);
									table[newLang][orig] = tl;
								}
							}
						});
						self.lang = newLang;
						//console.log("load done, resolving")
						resolve();
						//console.log("load done, listeners")
						for(let lis of self.listeners)
						{
							lis.requestUpdate();
						}
					}).catch(err => {
						//console.log('lang load fail');
						self.setLanguage('en');
						//console.log(err);
						console.log('Error: Language file ' + newLang + '.txt not found.');
						reject(err);
					});
				}
			});
			//return ret;
			//console.log("language is: "+this.lang);
		}
	};
	
	app.login = 
	{
		listeners: new Set(),
		loggedin: false,
		setLogin: function(loggedin)
		{
			if(this.loggedin!=loggedin && loggedin!=null)
			{
				//console.log("login change happened")
				this.loggedin = loggedin;
				
				app.login.listeners.forEach( elem => 
				{
					//console.log("Updating " + elem)
					elem.requestUpdate();
				});
			}
			//console.log("loggedin is: "+this.loggedin);
		},
		isLoggedIn()
		{
			return this.loggedin;
		},
		updateLogin()
		{
			var self = this;
			return new Promise(function(resolve, reject) 
			{
				axios.get('webjcc/isLoggedIn', {headers: {'x-jadex-isloggedin': true}}, self.transform).then(function(resp)
				{
					//console.log("is logged in: "+resp);
					self.setLogin(resp.data);
					resolve(self.loggedin);
				})
				.catch(function(err) 
				{
					console.log("check failed: "+err);	
					reject(err);
				});
			});
		},
		relogin(store)
		{
			return new Promise((resolve, reject) =>
			{
				let pass = localStorage.getItem("platformpassword")
				//console.log("session store pass " + pass);
				if(pass)
				{
					this.checkLoggedIn().then(l =>
					{
						//console.log("check result: "+l);
						if(!l)
						{
							this.login(pass, store).then(l =>
							{
								resolve(l);
							})
							.catch(err =>
							{
								reject(err);
							});
						}
						else
						{
							resolve(true);
						}
					})
					.catch(err =>
					{
						this.login(pass, store).then(l =>
						{
							resolve(l);
						})
						.catch(err =>
						{
							reject(err);
						});
					});
				}
				else
				{
					resolve(false);
				}
			});
		},
		login(pass, store)
		{
			let self = this;
			
			return new Promise((resolve, reject) => 
			{
				axios.get('webjcc/login?pass='+pass, {headers: {'x-jadex-login': pass}}).then(function(resp)
				//axios.get('webjcc/login?pass='+pass, self.transform).then(function(resp)
				{
					//console.log("logged in: "+resp);
					//self.loggedin = true;
					self.setLogin(true);
					
					if(typeof(Storage) !== undefined) 
					{
						if(store) 
						{
							localStorage.setItem("platformpassword", pass);
						}
					}
					
					//window.location.href = "/#/platforms";
					resolve();
				})
				.catch(function(err) 
				{
					//console.log("login failed: "+err);	
					//self.loggedin = false;
					self.setLogin(false);
					reject(err);
				});
			});
		},
		checkLoggedIn()
		{
			let self = this;
			return new Promise((resolve, reject) => 
			{
				axios.get('webjcc/isLoggedIn', {headers: {'x-jadex-isloggedin': true}}).then(function(resp)
				{
					//console.log("is logged in: "+resp);
					//self.loggedin = resp.data;
					self.setLogin(resp.data);
					resolve(self.loggedin);
				})
				.catch(function(err) 
				{
					self.setLogin(false);
					//console.log("check failed: "+err);
					//this.app.login.setLogin(false);	
					resolve(false);
				});
			});
		},
		logout()
		{
			let self = this;
			return new Promise((resolve, reject) => 
			{
				axios.get('webjcc/logout', {headers: {'x-jadex-logout': true}}).then(function(resp)
				{
					//console.log("logged out: "+resp);
					//self.loggedin = false;
					self.setLogin(false);
					resolve();
				})
				.catch(function(err) 
				{
					//console.log("logout failed: "+err);	
					//self.loggedin = false;
					self.setLogin(false);
					reject(err);
				});
				localStorage.removeItem("platformpassword");
			});
		}
	}
	console.log("app loaded/inited");
	if(window.jadexapp==null)
	{
		window.jadexapp = new Promise(function(resolve, reject) 
		{
			resolve(app);	
		});
	}
	else
	{
		window.jadexapp.resolve(app);
	}
	
	return app;
}());
