
self.addEventListener('install', (event) => {
  console.log("WORKER IS INSTALLED!!!");
  event.waitUntil(self.skipWaiting()); // Activate worker immediately
});

self.addEventListener('activate', (event) => {
  console.log("WORKER IS ACTIVATED!!!");
  event.waitUntil(self.clients.claim()); // Become available to all pages
});

self.addEventListener('message', function(event){
  console.log("SW Received Message: " + JSON.stringify(event.data));
  event.ports[0].postMessage(event.data);
});