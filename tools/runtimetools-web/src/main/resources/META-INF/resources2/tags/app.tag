<app>
	<nav class="navbar navbar-expand-lg navbar-dark navbar-fixed-top navbar-custom">
		<div class="navbar-brand mr-auto">
 			<img src="images/jadex_logo_ac.png" width="200px"/>
			<a class="navbar-brand pl-2" href="#">WebJCC</a>
		</div>
		<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		</button>
		<div class="collapse navbar-collapse" id="navbarSupportedContent" ref="navcol">
			<ul class="navbar-nav mr-auto">
   				<li class="nav-item">
      			<a class="nav-link" href="">{$t("message.home")}</a>
    		</li>
    		<li class="nav-item">
      			<a class="nav-link" href="#about">{$t("message.about")}</a>
    		</li>
 				</ul>
 				<form class="form-inline my-2 my-lg-0">
 					<!-- <input class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search" v-model="tag" list="mytags2"/>
					<datalist id="mytags2">
						<dynoption v-for="tag in tagNames" v-bind:datalisttag="tag"></dynoption>
					</datalist> -->
				</form>
			</div>
			<!--<div class="d-flex flex-row order-2 order-lg-3">
         <ul class="navbar-nav flex-row">
            <li class="nav-item"><a class="nav-link px-2" href="#"><span class="fa fa-facebook"></span></a></li>
            <li class="nav-item"><a class="nav-link px-2" href="#"><span class="fa fa-twitter"></span></a></li>
            <li class="nav-item"><a class="nav-link px-2" href="#"><span class="fa fa-youtube"></span></a></li>
            <li class="nav-item"><a class="nav-link px-2" href="#"><span class="fa fa-linkedin"></span></a></li>
        </ul>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown">
            <span class="navbar-toggler-icon"></span>
        </button> -->
        
        <form class="form-inline my-2 my-lg-0 ml-2">
			<!-- 
			<input class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search">
			<button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>-->
			<img class="navbar-nav ml-auto" onclick="riot.store.switchLanguage()" src="{lang=='de'? 'images/language_de.png': 'images/language_en.png'}" />
		</form>
	</nav>
	
	<div id="content"></div>
	
	<div if="{message!=null}" class="container-fluid pt-0 pl-3 pr-3 pb-3">
		<div class="row">
			<div class="col">
				<div class="{'alert-danger': message.type=='error', 'alert-info': message.type=='info'}" class="alert m-0 p-3" role="alert">
					{message.text}
					<button type="button" class="btn btn-primary mr-1" align="right" style="width: 100px" onclick="clearMessage()">Close</button>
				</div>
			</div>
		</div>
	</div>
	
	<footer class="container-fluid footer navbar-light bg-light">
        <span class="text-muted">Copyright by <a href="http://www.actoron.com">Actoron GmbH</a> 2017-{new Date().getFullYear()}</span>
    	<div class="pull-right">
    		<a href="#/privacy">{$t("message.privacy")}</a>
    		<a href="#/imprint">{$t("message.imprint")}</a>
    	</div>
    </footer>

    <script>
    	var self = this;
    	this.message = null;
    	
    	//console.log("tag app: "+self.id);
    
	    riot.store.on('message', function(m) {
			//console.log("message: "+m);
			//console.log("lang: "+self.lang);
			self.message = m;
			self.update();
		});
	    
	    clearMessage = function()
	    {
	    	self.message = null;
	    	self.update();
	    }
    </script>
</app>