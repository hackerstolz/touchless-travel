
document.addEventListener('DOMContentLoaded', function () {

console.log('Try connecting to event source...');
//var user = 'asdasd';
var source = new EventSource('/webapp/api/connect/' + user);

//source.addEventListener('message', function(e) {
//  console.log(e.data);
//  var node = document.createElement("LI");
//  var textnode = document.createTextNode(e.data);
//  node.appendChild(textnode);
//  document.getElementById("events").appendChild(node);
//}, false);
//
//source.addEventListener('open', function(e) {
//  console.log('Connection to backend established');
//  var node = document.createElement("LI");
//    var textnode = document.createTextNode('Connection to backend established');
//    node.appendChild(textnode);
//    document.getElementById("events").appendChild(node);
//}, false);
//
//source.addEventListener('error', function(e) {
//  if (e.readyState == EventSource.CLOSED) {
//    console.log('Connection to backend closed');
//       var textnode = document.createTextNode('Connection to backend closed');
//        node.appendChild(textnode);
//        document.getElementById("events").appendChild(node);
//  }
//}, false);

source.onopen =  function(e) {
  console.log('Connection to backend established');
  var node = document.createElement("LI");
    var textnode = document.createTextNode('Connection to backend established');
    node.appendChild(textnode);
    document.getElementById("events").appendChild(node);
};


source.onmessage = function(e) {
  console.log(e.data);
  var node = document.createElement("LI");
  var textnode = document.createTextNode(e.data);
  node.appendChild(textnode);
  document.getElementById("events").appendChild(node);
};

source.onerror = function(e) {
  if (e.readyState == EventSource.CLOSED) {
    console.log('Connection to backend closed');
       var textnode = document.createTextNode('Connection to backend closed');
        node.appendChild(textnode);
        document.getElementById("events").appendChild(node);
  }
};

});