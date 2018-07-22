var counter = new XMLHttpRequest();
counter.onload = function () { console.log("counted"); };
counter.open("get", "http://wasabiflux.org/assets/counter.php", true);
counter.send()
