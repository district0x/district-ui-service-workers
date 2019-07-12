(ns district.ui.service-workers
  (:require
    [cljs.spec.alpha :as s]
    [clojure.string :as string]
    [district.ui.service-workers.events :as events]
    [mount.core :as mount :refer [defstate]]
    [re-frame.core :refer [dispatch-sync dispatch]]))

(declare start)
(declare stop)
(defstate service-workers
  :start (start (:service-workers (mount/args)))
  :stop (stop))

(s/def ::scope string?)
(s/def ::script-url string?)
(s/def ::worker (s/keys :req-un [::script-url]
                        :opt-un [::scope]))
(s/def ::workers (s/coll-of ::worker))
(s/def ::opts (s/nilable (s/keys :opt-un [::workers])))

(defn start [{:keys [:workers] :as opts}]
  (s/assert ::opts opts)
  (dispatch-sync [::events/start opts])
  opts)

(defn stop []
  (dispatch-sync [::events/stop]))

