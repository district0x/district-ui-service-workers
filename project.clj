(defproject district0x/district-ui-service-workers "1.0.0"
  :description "district UI module for handling Service Workers"
  :url "https://github.com/district0x/district-ui-service-workers"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [[district0x/re-frame-spec-interceptors "1.0.1"]
                 [mount "0.1.11"]
                 [org.clojure/clojurescript "1.10.520"]
                 [re-frame "0.10.7"]]

  :doo {:paths {:karma "./node_modules/karma/bin/karma"}
        :karma {:config {"files" [{"pattern" "resources/public/my-service-worker.js"
                                   "watched" false
                                   "included" false
                                   "served" true
                                   "nocache" true}]
                         "proxies" {"/my-service-worker.js" "http://localhost:9876/base/resources/public/my-service-worker.js"}
                         "captureTimeout" 60000
                         "browserDisconnectTolerance" 3
                         "browserDisconnectTimeout" 60000
                         "browserNoActivityTimeout" 60000}}}

  :npm {:devDependencies [[karma "4.1.0"]
                          [karma-chrome-launcher "2.2.0"]
                          [karma-cli "2.0.0"]
                          [karma-cljs-test "0.1.0"]]}

  :profiles {:dev {:dependencies [[com.cemerick/piggieback "0.2.2"]
                                  [day8.re-frame/test "0.1.5"]
                                  [org.clojure/clojure "1.9.0"]
                                  [org.clojure/tools.nrepl "0.2.13"]]
                   :plugins [[lein-cljsbuild "1.1.7"]
                             [lein-doo "0.1.11"]
                             [lein-npm "0.6.2"]]}}

  :cljsbuild {:builds [{:id "tests"
                        :source-paths ["src" "test"]
                        :compiler {:output-to "tests-output/tests.js"
                                   :output-dir "tests-output"
                                   ;:asset-path "resource/public/"
                                   :main "tests.runner"
                                   :optimizations :none}}]})
