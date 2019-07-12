# district-ui-service-workers

[![Build Status](https://travis-ci.org/district0x/district-ui-service-workers.svg?branch=master)](https://travis-ci.org/district0x/district-ui-service-workers)

Clojurescript [re-mount](https://github.com/district0x/d0x-INFRA/blob/master/re-mount.md) module for handling [Service Workers](https://developers.google.com/web/fundamentals/primers/service-workers/).

## Installation
Latest released version of this library: <br>
[![Clojars Project](https://img.shields.io/clojars/v/district0x/district-ui-service-workers.svg)](https://clojars.org/district0x/district-ui-service-workers)
  
Include `[district.ui.service-workers]` in your CLJS file, where you use `mount/start`

## API Overview

- [district.ui.service-workers](#districtuiservice-workers)
- [district.ui.service-workers.subs](#districtuiservice-workerssubs)
  - [::workers](#workers-sub)
  - [::worker-registration](#worker-registration-sub)
- [district.ui.service-workers.events](#districtuiservice-workersevents)
  - [::register](#register-evt)
  - [::post-message](#post-message-evt)
  - [::worker-registered](#worker-registered-evt)
  - [::worker-register-error](#worker-register-error-evt)
  - [::service-worker-ready](#service-worker-ready-evt)
  - [::service-worker-ready-error](#service-worker-ready-error-evt)
  - [::controller-changed](#controller-changed-evt)
  - [::unregister](#unregister-evt)
- [district.ui.service-workers.effects](#districtuiservice-workerseffects)
  - [::watch-service-worker-ready](#watch-service-worker-ready-fx)
  - [::watch-controller-change](#watch-controller-change-fx)
  - [::register](#register-fx)
  - [::unregister](#unregister-fx)
  - [::post-message](#post-message-fx)
- [district.ui.service-workers.queries](#districtuiservice-workersqueries)
  - [assoc-worker](#assoc-worker)
  - [assoc-workers](#assoc-workers)
  - [assoc-worker-registration](#assoc-worker-registration)
  - [workers](#workers)
  - [worker-registration](#worker-registration)
  

## district.ui.service-workers
This namespace contains service-workers [mount](https://github.com/tolitius/mount) module.

You can pass following args to initiate this module: 
* `:workers` Collection of workers to register at mount start 

```clojure
  (ns my-district.core
    (:require [mount.core :as mount]
              [district.ui.service-workers]))
              
  (-> (mount/with-args
        {:service-workers {:workers [{:script-url "/my-service-worker.js" :scope "/"}]}})
    (mount/start))
```

**Important note:** Don't forget to have your Service Worker file at the same path as you define scope. Therefore, if 
you define root scope "/", you need to have worker file accessible at https://my-server.com/my-service-worker.js. Also note,
`post-message` function can be used only for a worker at root scope "/". 

## district.ui.service-workers.subs
re-frame subscriptions provided by this module:

#### <a name="workers-sub">`::workers`
Returns service workers by scope.

#### <a name="worker-registration-sub">`::worker-registration [scope]`
Given scope, will return ServiceWorkerRegistration object.

## district.ui.service-workers.events
re-frame events provided by this module:

#### <a name="register-evt">`::register`
Will register new service worker. Pass configuration of single worker.

```clojure  
(dispatch [::service-workers-events/register {:script-url "/my-service-worker.js" :scope "/"}])
```

#### <a name="post-message-evt">`::post-message [data & [ports]]`
Will post message to service worker under root scope ("/").

```clojure
(dispatch [::service-workers-events/post-message {:a 1 :b 2}])
```

In case you want to receive response as well, second, optional parameter is for [MessageChannel](https://developer.mozilla.org/en-US/docs/Web/API/MessageChannel) ports,
that will receive response. Follow [tests](https://github.com/district0x/district-ui-service-workers/blob/master/test/tests/all.cljs) for an example. 
 

#### <a name="worker-registered-evt">`::worker-registered`
Event fired when worker has been succesfully registered.

#### <a name="worker-register-error-evt">`::worker-register-error`
Event fired when worker there's been error registering a service worker.

#### <a name="service-worker-ready-evt">`::service-worker-ready`
Event fired when `js/navigator.serviceWorker` is ready.

#### <a name="service-worker-ready-error-evt">`::service-worker-ready-error`
Event fired when `js/navigator.serviceWorker` failed to be ready.

#### <a name="controller-changed-evt">`::controller-changed`
Event fired when `js/navigator.serviceWorker.controller` has changed.

#### <a name="unregister-evt">`::unregister`
Event to unregister a service worker by its scope. 

```clojure
(dispatch [::service-workers-events/unregister {:scope "/"}])
```

## district.ui.service-workers.effects
re-frame effects provided by this module

#### <a name="watch-service-worker-ready-fx">`::watch-service-worker-ready`
Effect to start watching when `js/navigator.serviceWorker` is ready.

#### <a name="watch-controller-change-fx">`::watch-controller-change`
Effect to start watching when `js/navigator.serviceWorker.controller` changes.

#### <a name="register-fx">`::register`
Effect to register new service worker. You should be using event for this unless you have special use case.

#### <a name="unregister-fx">`::unregister`
Effect to unregister a service worker. You should be using event for this unless you have special use case.

#### <a name="post-message-fx">`::post-message`
Effect to post message to a service worker. You should be using event for this unless you have special use case.  

## district.ui.service-workers.queries
DB queries provided by this module:   
*You should use them in your events, instead of trying to get this module's 
data directly with `get-in` into re-frame db.*

#### <a name="assoc-worker">`assoc-worker [db worker]`
Associates new worker and returns new re-frame db.

#### <a name="assoc-workers">`assoc-workers [db]`
Associates multiple new workers and returns new re-frame db.

#### <a name="assoc-worker-registration">`assoc-worker-registration [db worker registration]`
Associates ServiceWorkerRegistration object with existing worker.

#### <a name="workers">`workers [db]`
Returns associated workers.

#### <a name="worker-registration">`worker-registration [db scope]`
Returns ServiceWorkerRegistration object given scope.

## Development
```bash
lein deps
lein doo chrome tests once
```
