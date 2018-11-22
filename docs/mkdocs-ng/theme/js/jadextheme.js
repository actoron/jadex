   var navbarLoaded2 = function(data) {
      var acpath = "https://www.activecomponents.org/"
      var acindexpage = acpath + "index.html#/"

//      var $topbar = $("#topbar");
      var $topbar = $(data)
      $topbar.find("a[href^=#]").each(function (index, element) {
          element.href = acindexpage + element.attributes.href.value.substring(1);
        }
      )

      $topbar.find("a[href]:not([href^=http])").each(function (index, element) {
        if (element.attributes.href.value == "index.html#/") {
          element.href = acindexpage;
        } else {
          element.href = acindexpage + element.attributes.href.value.substring(1);
        }
      });

      $topbar.find("img[src]:not([src^=http])").each(function (index, element) {
        element.src = acpath + element.attributes.src.value;
      });

      var docsdropdownli = $topbar.find('li:has(> a[id=docs])');
      docsdropdownli.addClass('active');
      var manualsli = docsdropdownli.find('ul > li:has(> a:contains("Manuals"))');
      manualsli.addClass('active');



      return $topbar;
    };


  var xdLoader = new XDLoader('https://www.activecomponents.org/');
  xdLoader.scheduleLoad['topnavbar.html'] = function (data) {
    var element = navbarLoaded2(data);
    $('#topNavBar').html(element);
    $("#topNavBar #innertopbar").append('\
		<div class="version-header auto-padded">\
			<div class="version-header-container auto-scaled">\
				<span class="version-header-jadex  auto-scaled" style="padding-right:0px; padding-left:0px">Jadex </span>\
				<span class="auto-scaled" style="padding-left:0px; padding-right:0px">${jadexversiontitle}</span>\
			</div>\
		</div>')
  };

  xdLoader.scheduleLoad['bottomnavbar.html'] = function (data) {
      var element = navbarLoaded2(data);
      $('#bottomNavBar').html(element);
  };

  	var navMarginBottom = 56;

	var setStickyPositions = function (stickyHeaderTop) {
		  if( $(window).scrollTop() > stickyHeaderTop ) {
				  $('#stickyheader').css({position: 'fixed', top: '0px', bottom: navMarginBottom + 'px'});
		  } else {
		  		var h = $('#topbar').height()
				var visibleHeaderPx = (h - $(window).scrollTop())
			  	$('#stickyheader').css({position: 'absolute', top: '0px', marginLeft: 'unset', bottom: navMarginBottom + visibleHeaderPx + 'px'});
		  }
	}

  xdLoader.finish = function() {

  	var stickyHeaderTop = $('#stickyheader').offset().top;
//  	$('#stickyheader').css({position: 'fixed', top: '0px', bottom: navMarginBottom + 'px'});
	setStickyPositions(stickyHeaderTop);

	$(window).scroll(function () {
		setStickyPositions(stickyHeaderTop);
	});

	$(window).resize(function () {
		setStickyPositions(stickyHeaderTop);
	});
  }

  xdLoader.execute();

  var currentChapter = $("li .current");
  var searchHeader = $(".wy-side-nav-search");
//  console.log(searchHeader.height());
  $("#stickyheader").scrollTop(currentChapter.offset().top-(searchHeader.height()+20));


  $('x-hint').each(function(index, element) {
  	$("<h4>Hint</h4>").insertBefore(element);
  });

  // correct javadoc links
  // var docsHome = $(searchHeader).find('a.icon-home').attr('href');
  var docsHome = $('ul.wy-breadcrumbs > li > a')
//  console.log(docsHome);

  var lengthOf = "$RELJAVADOCPATH".length;
  $('a[href*="$RELJAVADOCPATH"]').each(function(index, element) {
//  	console.log(window.location.pathname);
//  	window.location.pathname.indexof
	var originalHref = element.getAttribute('href')
	var url = docsHome + "/../" + "javadoc" +  originalHref.substring(originalHref.indexOf('$RELJAVADOCPATH') + lengthOf);

	// console.log("replacing with: " + url);
	element.setAttribute('href', url);
  });
