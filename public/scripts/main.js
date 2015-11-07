(function() {
    "use strict";

    // Bootstrap
    window.addEventListener('load', function(e) {
        initHeader();
        initContent();
        addListeners();
        // initWebSocket();
        setTimeout(function() {
            window.scrollTo(0, 0);
        }, 1);

        var user = new Date().getTime() + "AWESOME";
        var source = new EventSource('/webapp/api/connect/' + user);

        source.onmessage = function(e) {
          console.log(e.data);
          raiseEvent(e.data);
        };

        source.onerror = function(e) {
          if (e.readyState == EventSource.CLOSED) {
            console.log('Connection to backend closed');
          }
        };

    }, false);

    // Declaration & Initializing
    var width, height, hideLangageList, largeHeader, language, oContent, aContactItems, oGlassContent, skipper, oHeaderBar, aImgItems, canvas, ctx, circles, target, animateHeader = true,
        isBgrAnimating = false,
        isSkipperAnimating = false,
        iCurrentBgr = 0,
        bgrInterval, noscroll = true,
        isRevealed = false,
        aAnimIn, t,
        map, pointarray, heatmap,
        infoWindows = [], oCounter, cnt = 0,
        u9Marker;

    // Event handling
    function addListeners() {
        window.addEventListener('resize', resize);
        window.addEventListener('scroll', scroll);
        skipper.addEventListener('click', function() {
            toggle(1);
        });
        if (!language) return;
        for (var i = 0; i < language.children.length; i++) {
            language.children[i].addEventListener('click', function(e) {
                if (e.currentTarget.nodeName === 'A') {
                    languageDialog();
                } else {
                    selectLanguage(e);
                }
            });
        }
    }

    function initHeader() {
        width = window.innerWidth;
        height = window.innerHeight;
        target = {
            x: 0,
            y: height
        };

        largeHeader = document.getElementById('large-header');
        largeHeader.style.height = height + 'px';

        canvas = document.getElementById('header-canvas');
        canvas.width = width;
        canvas.height = height;
        ctx = canvas.getContext('2d');

        // create particles
        circles = [];
        var numberCircles = 100; //width * 0.5;
        for (var x = 0; x < numberCircles; x++) {
            var c = new Circle();
            circles.push(c);
        }
        limitLoop(animateHeaderCanvas, 60);

        aImgItems = document.querySelector('ul.image-wrap').children;
        bgrInterval = setInterval(animateBackgroundTransition, 5000);

        aContactItems = document.querySelectorAll('.btn-wrapper');

        var i = 0;
        var timeoutAnimation = function() {
            setTimeout(function() {
                if (aContactItems.length == 0) return;
                aContactItems[i].className = aContactItems[i].className + ' fxFlipInX';
                i++;
                if (i < aContactItems.length) timeoutAnimation();
            }, 100);
        }

        disable_scroll();
        timeoutAnimation();
    }

    // function initWebSocket() {
    //     var socket = io('localhost:3000');

    //     socket.on('newCamera', function(data){
    //         console.log(data);

    //         var image = 'img/mapcamNew.svg';
    //         addCamMarker(data, image, true);
    //     });
    // }

    /*
     * Easing Functions - inspired from http://gizma.com/easing/
     * only considering the t value for the range [0, 1] => [0, 1]
     */
    var EasingFunctions = {
      // no easing, no acceleration
      linear: function (t) { return t },
      // accelerating from zero velocity
      easeInQuad: function (t) { return t*t },
      // decelerating to zero velocity
      easeOutQuad: function (t) { return t*(2-t) },
      // acceleration until halfway, then deceleration
      easeInOutQuad: function (t) { return t<.5 ? 2*t*t : -1+(4-2*t)*t },
      // accelerating from zero velocity 
      easeInCubic: function (t) { return t*t*t },
      // decelerating to zero velocity 
      easeOutCubic: function (t) { return (--t)*t*t+1 },
      // acceleration until halfway, then deceleration 
      easeInOutCubic: function (t) { return t<.5 ? 4*t*t*t : (t-1)*(2*t-2)*(2*t-2)+1 },
      // accelerating from zero velocity 
      easeInQuart: function (t) { return t*t*t*t },
      // decelerating to zero velocity 
      easeOutQuart: function (t) { return 1-(--t)*t*t*t },
      // acceleration until halfway, then deceleration
      easeInOutQuart: function (t) { return t<.5 ? 8*t*t*t*t : 1-8*(--t)*t*t*t },
      // accelerating from zero velocity
      easeInQuint: function (t) { return t*t*t*t*t },
      // decelerating to zero velocity
      easeOutQuint: function (t) { return 1+(--t)*t*t*t*t },
      // acceleration until halfway, then deceleration 
      easeInOutQuint: function (t) { return t<.5 ? 16*t*t*t*t*t : 1+16*(--t)*t*t*t*t }
    }

    function move(u9Marker, latlngs, index, wait, newDestination) {
        u9Marker.setPosition(latlngs[index].pos);
        if(index != latlngs.length - 1) {
          // call the next "frame" of the animation
          setTimeout(function() { 
            move(u9Marker, latlngs, index + 1, wait, newDestination); 
          }, wait * latlngs[index].speed);
        } else {
          // assign new route
          u9Marker.position = u9Marker.destination;
          u9Marker.destination = newDestination;
        }
    }

    function initContent() {
        oContent = document.getElementById('content');
        oHeaderBar = document.querySelector('header');
        oGlassContent = document.querySelector("#glass-content");
        oCounter = document.querySelector(".number");
        skipper = document.querySelector("#header-skipper");
        aAnimIn = document.querySelectorAll('.animation-init');
        language = document.getElementById('language-setting');

        oContent.style.height = window.innerHeight + 'px';
        oContent.height = window.innerHeight + 'px';

        var mapOptions = {
            zoom: 15,
            center: new google.maps.LatLng(48.789881, 9.209300)
        };
        map = new google.maps.Map(document.getElementById('map-canvas'),
            mapOptions);

        // Draw U9 Path
        var u9Stations = [
            [48.759711, 9.254715, 0],       // Hedelfingen
            [48.766543, 9.249094, 1],       // Hedelfinger Strasse
            [48.771634, 9.245510, 1],       // Wangen Marktplatz
            [48.772313, 9.245081, 0.2],     // Turn
            [48.773685, 9.245296, 0.3],     // Turn
            [48.775099, 9.245102, 0.2],     // Turn
            [48.775933, 9.243751, 0.3],     // Wasenstrasse
            [48.776315, 9.240468, 0.5],     // Turn
            [48.775749, 9.237399, 0.5],     // Inselstrasse
            [48.778012, 9.233687, 1],       // Im Degen
            [48.781094, 9.228537, 1],       // Turn
            [48.782423, 9.222915, 1],       // Wangener-/Landhausstraße
            [48.783470, 9.220426, 0.4],     // Turn
            [48.784770, 9.219396, 0.3],     // Turn
            [48.785958, 9.219117, 0.1],     // Turn
            [48.787259, 9.217637, 0.2],     // Schlachthof
            [48.788927, 9.213860, 0.8],     // Turn
            [48.788891, 9.212422, 0.2],     // Raitelsberg (index 17)
            [48.789033, 9.206393, 0.8],     // Bergfriedhof
            [48.789195, 9.202616, 1],       // Karl-Olga-Krankenhaus
            [48.789393, 9.196007, 1],       // Turn
            [48.789054, 9.195310, 0.2],     // Stöckach
            [48.786092, 9.190149, 1.2],     // Neckartor
            [48.782487, 9.187371, 1],       // Staatsgalerie
            [48.781533, 9.186480, 0.2],     // Turn
            [48.782961, 9.181201, 1],       // Hauptbf (A.-Klett-Pl.)
            [48.784077, 9.178991, 0.3],     // Turn
            [48.782621, 9.177403, 0.3],     // Turn
            [48.779977, 9.176717, 0.4],     // Börsenplatz
            [48.779157, 9.176073, 0.2],     // Turn
            [48.778351, 9.168949, 0.8],     // Berliner Platz (Liederhalle)
            [48.776287, 9.162512, 1],       // Schloss-/Johannesstrasse
            [48.775099, 9.159143, 0.5],     // Turn
            [48.774943, 9.155560, 0.5],     // Schwab
            [48.774745, 9.151633, 0.5],     // Arndt-/Spittastraße
            [48.774675, 9.145775, 1],       // Vogelsang
            [48.774632, 9.144509, 0.2],     // Turn
            [48.772285, 9.141719, 0.8]      // Herderplatz
        ].map(function(stop){
            return {lat: stop[0], lng: stop[1], distance: stop[2]};
        });
        var u9Path = new google.maps.Polyline({
            path: u9Stations,
            geodesic: true,
            strokeColor: '#0000FF',
            strokeOpacity: 0.5,
            strokeWeight: 5
        });
        u9Path.setMap(map);

        u9Marker = new google.maps.Marker({
            map: map,
            draggable: false,
            icon: 'styles/img/tram_icon.svg',
            animation: google.maps.Animation.DROP,
            position: {lat: u9Stations[17].lat, lng: u9Stations[17].lng}
        });

        // store a LatLng for each step of the animation
        frames = [];

        u9Stations.slice(17, u9Stations.length - 1).forEach(function(stop, idx, aStops){
            if (aStops.length > idx + 1) {
                var dest = aStops[idx + 1];

                for (var percent = 0; percent < 1; percent += 0.001) {
                    var curLat = stop.lat + percent * (dest.lat - stop.lat);
                    var curLng = stop.lng + percent * (dest.lng - stop.lng);
                    frames.push({pos: new google.maps.LatLng(curLat, curLng), speed: dest.distance});
                }
            } else {
                return;
            }  
        });
    }

    function closeAllInfoWindows() {
        for (var i = 0; i < infoWindows.length; i++) {
            infoWindows[i].close();
        }
    }

    function raiseEvent(data) {
        
        u9Marker.info = new google.maps.InfoWindow({
            content: '<div class="popup-marker"><h2>' + "CHECK IN" + '</h2>' +
                '<div><b>Date:</b><p>' + "07. Nov 2015 13:24" + '</p></div>' +
                '<div><b>Vehicle:</b><p>' + "U9 direction Vogelsang" + '</p></div>' +
                '<div><b>Position:</b><p>' + "Raitelsberg" + '</p></div>' +
                '<div><b>User:</b><p>' + "Norman Weisenburger" + '</p></div>' +
                '</div>'
        });
        infoWindows.push(u9Marker.info);
        closeAllInfoWindows();
        u9Marker.info.open(map, u9Marker);

        window.setTimeout(function(){
            closeAllInfoWindows();
        },5000);

        if("CHECKIN" === "CHECKIN") {
            // begin animation, send back to origin after completion
            move( u9Marker, frames, 0, 50, u9Marker.position );
        }

        google.maps.event.addListener(u9Marker, 'click', function() {
            closeAllInfoWindows();
            u9Marker.info.open(map, u9Marker);
        });
    }

    function addCamMarker(cam, image, animated) {
        var lat, lng;
        if (typeof cam.lat == "string" && typeof cam.lng == "string") {
            // GPS Position are notated as strings and must be transformed
            // debugger
            cam.lat = cam.lat.replace(/\s+/g,"");
            cam.lng = cam.lng.replace(/\s+/g,"");
            var latParts = cam.lat.split(/[^\d\w]+/);
            var lngParts = cam.lng.split(/[^\d\w]+/);
            lat = ConvertDMSToDD(parseInt(latParts[0]), parseInt(latParts[1]), parseInt(latParts[2]), latParts[3]);
            lng = ConvertDMSToDD(parseInt(lngParts[0]), parseInt(lngParts[1]), parseInt(lngParts[2]), lngParts[3]);
        } else {
            lat = cam.lat;
            lng = cam.lng;
        }

        animated = animated ? google.maps.Animation.DROP : null;
        var marker = new google.maps.Marker({
            position: new google.maps.LatLng(lat, lng),
            map: map,
            icon: image,
            title: cam.owner,
            draggable: false,
            animation: animated
        });

        var audio = (cam.audio == "true") ? "Ja" : "Nein";
        var realtime = (cam.realtime == "true") ? "Ja" : "Nein";
        var oR = (cam.objectRecognition == "true") ? "Ja" : "Nein";

        var camImg = "";
        if (cam.img && cam.img != "") {
            camImg = "<div class='cam-img' style='background-image: url("+cam.img+")'></div>"
        }

        var category = '';
        if (cam.category.indexOf("publicSecurity") > -1) category += "<i class='icon-shield'></i><span>Öffentliche Sicherheit </span>";
        if (cam.category.indexOf("traffic") > -1) category += "<i class='icon-road'></i><span>Verkehrsüberwachtung </span>";
        if (cam.category.indexOf("propertySec") > -1) category += "<i class='icon-building'></i><span>Objektschutz </span>";
        if (cam.category.indexOf("other") > -1) category += "<i class='icon-eye2'></i><span>Sonstiges </span>";

        var address = (cam.adress == "") ? cam.owner : cam.adress;
        var pub = cam.countPublic == null ? "Keine" : cam.countPublic;
        debugger
        marker.info = new google.maps.InfoWindow({
            content: '<div class="popup-marker"><h2>' + cam.owner + '</h2>' +
                camImg +
                '<div><b>Adresse:</b><span>' + address + '</span></div>' +
                '<div><b>Anzahl Kameras:</b><span>' + cam.count + '</span></div>' +
                '<div><b>Davon im öff. Raum:</b><span>' + pub + '</span></div>' +
                '<div><b>Kategorie:</b><span>' + category + '</span></div>' +
                '<div class="'+audio+'"><i class="icon-audio"></i><b>Audiofähig:</b><span>' + audio + '</span></div>' +
                '<div class="'+realtime+'"><i class="icon-realtime"></i><b>Echtzeitübertragung:</b><span>' + realtime + '</span></div>' +
                '<div class="'+oR+'"><i class="icon-target"></i><b>Objekterkennung:</b><span>' + oR + '</span></div>' +
                '</div>'
        });
        infoWindows.push(marker.info);
        google.maps.event.addListener(marker, 'click', function() {
            closeAllInfoWindows();
            marker.info.open(map, marker);
        });

        cnt += cam.count;
        oCounter.innerHTML = cnt;
    }

    function ConvertDMSToDD(degrees, minutes, seconds, direction) {
        var dd = degrees + minutes / 60 + seconds / (60 * 60);

        if (direction == "S" || direction == "W") {
            dd = dd * -1;
        } // Don't do anything for N or E
        return dd;
    }

    function colorize() {
        t.options.x_gradient = Trianglify.randomColor();
        t.options.y_gradient = t.options.x_gradient.map(function(c) {
            return d3.rgb(c).brighter(0.5);
        });
    }

    function languageDialog() {
        if (language.className === '') {
            language.className = 'language-select';
            clearTimeout(hideLangageList);
            hideLangageList = setTimeout(languageDialog, 4000);
        } else {
            language.className = '';
        }
    }

    function selectLanguage(e) {
        if (e.currentTarget.classList.contains('language-selected')) {
            e.preventDefault();
        } else {
            e.currentTarget.parentNode.querySelector('.language-selected').className = '';
            e.currentTarget.className = 'language-selected';
            var currentLanguage = e.currentTarget.getAttribute('data-language');
        };
        clearTimeout(hideLangageList);
        hideLangageList = setTimeout(languageDialog, 4000);
    }

    function disable_scroll() {
        var preventDefault = function(e) {
            e = e || window.event;
            if (e.preventDefault)
                e.preventDefault();
            e.returnValue = false;
        };
        document.body.ontouchmove = function(e) {
            preventDefault(e);
        };
    }

    function enable_scroll() {
        window.onmousewheel = document.onmousewheel = document.onkeydown = document.body.ontouchmove = null;
    }

    function scroll() {
        // Update Glass header
        // var iOffset = window.pageYOffset;
        // if (oGlassContent) oGlassContent.style.marginTop = (iOffset * -1) + 'px';

        // Upate Large Header
        var scrollVal = window.pageYOffset || largeHeader.scrollTop;
        if (noscroll) {
            if (scrollVal < 0) return false;
            window.scrollTo(0, 0);
        }
        if (isSkipperAnimating) {
            return false;
        }
        if (scrollVal <= 0 && isRevealed) {
            toggle(0);
        } else if (scrollVal > 0 && !isRevealed) {
            toggle(1);
        }

        // Animate Sections
        for (var i = 0; i < aAnimIn.length; i++) {
            var animationElement = aAnimIn[i];

            var docViewTop = window.pageYOffset;
            var docViewBottom = docViewTop + window.innerHeight;

            var elemTop = animationElement.offsetTop;
            // var elemBottom = elemTop + animationElement.offsetHeight;

            if (elemTop <= docViewBottom - animationElement.offsetHeight / 3) {
                if (animationElement.classList.contains('animate-in') == false) {
                    animationElement.className = animationElement.className + ' animate-in';
                }
            }
        }
    }

    function resize() {
        width = window.innerWidth;
        height = window.innerHeight;
        largeHeader.style.height = height + 'px';
        canvas.width = width;
        canvas.height = height;

        oContent.style.height = height + 'px';
        oContent.height = height + 'px';
    }

    function toggle(reveal) {
        isSkipperAnimating = true;

        if (reveal) {
            document.body.className = 'revealed';
        } else {
            animateHeader = true;
            noscroll = true;
            disable_scroll();
            document.body.className = '';
            // Reset animated content
            for (var i = 0; i < aAnimIn.length; i++) {
                var animationElement = aAnimIn[i];
                animationElement.className = animationElement.className.replace(' animate-in', '');
            }
        }

        // simulating the end of the transition:
        setTimeout(function() {
            isRevealed = !isRevealed;
            isSkipperAnimating = false;
            if (reveal) {
                animateHeader = false;
                noscroll = false;
                enable_scroll();
            }
        }, 1200);
    }

    function animateBackgroundTransition(dir) {
        if (isBgrAnimating) return false;
        isBgrAnimating = true;
        var cntAnims = 0;
        var itemsCount = aImgItems.length;
        dir = dir || 'next';

        var currentItem = aImgItems[iCurrentBgr];
        if (dir === 'next') {
            iCurrentBgr = (iCurrentBgr + 1) % itemsCount;
        } else if (dir === 'prev') {
            iCurrentBgr = (iCurrentBgr - 1) % itemsCount;
        }
        var nextItem = aImgItems[iCurrentBgr];

        var classAnimIn = dir === 'next' ? 'navInNext' : 'navInPrev'
        var classAnimOut = dir === 'next' ? 'navOutNext' : 'navOutPrev';

        var onEndAnimationCurrentItem = function() {
            currentItem.className = '';
            ++cntAnims;
            if (cntAnims === 2) {
                isBgrAnimating = false;
            }
        }

        var onEndAnimationNextItem = function() {
            nextItem.className = 'current';
            ++cntAnims;
            if (cntAnims === 2) {
                isBgrAnimating = false;
            }
        }

        setTimeout(onEndAnimationCurrentItem, 2000);
        setTimeout(onEndAnimationNextItem, 2000);

        currentItem.className = currentItem.className + ' ' + classAnimOut;
        nextItem.className = nextItem.className + classAnimIn;
    }

    var animateHeaderCanvas = function() {
        if (animateHeader) {
            ctx.clearRect(0, 0, width, height);
            for (var i in circles) {
                circles[i].draw();
            }
        }
    }

    var limitLoop = function(fn, fps) {

        // Use var then = Date.now(); if you
        // don't care about targetting < IE9
        var then = new Date().getTime();

        // custom fps, otherwise fallback to 60
        fps = fps || 60;
        var interval = 1000 / fps;

        return (function loop(time) {
            requestAnimationFrame(loop);

            // again, Date.now() if it's available
            var now = new Date().getTime();
            var delta = now - then;

            if (delta > interval) {
                // Update time
                // now - (delta % interval) is an improvement over just 
                // using then = now, which can end up lowering overall fps
                then = now - (delta % interval);

                // call the fn
                fn();
            }
        }(0));
    };

    // Canvas manipulation
    function Circle() {
        var _this = this;

        // constructor
        (function() {
            _this.pos = {};
            init();
        })();

        function init() {
            _this.pos.x = Math.random() * width;
            _this.pos.y = height + Math.random() * 100;
            _this.alpha = 0.1 + Math.random() * 0.3;
            _this.scale = 0.1 + Math.random() * 0.3;
            _this.velocity = Math.random();
        }

        this.draw = function() {
            if (_this.alpha <= 0) {
                init();
            }
            _this.pos.y -= _this.velocity;
            _this.alpha -= 0.0005;
            ctx.beginPath();
            ctx.arc(_this.pos.x, _this.pos.y, _this.scale * 10, 0, 2 * Math.PI, false);
            ctx.fillStyle = 'rgba(255,255,255,' + _this.alpha + ')';
            ctx.fill();
        };
    }
})();