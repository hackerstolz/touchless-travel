
console.log('Try connecting to event source...');
var user = 'asdasd';
var source = new EventSource('/webapp/api/connect/' + user);

source.addEventListener('message', function(e) {
  console.log(e.data);
}, false);

source.addEventListener('open', function(e) {
  console.log('Connection to backend established');
}, false);

source.addEventListener('error', function(e) {
  if (e.readyState == EventSource.CLOSED) {
    console.log('Connection to backend closed');
  }
}, false);