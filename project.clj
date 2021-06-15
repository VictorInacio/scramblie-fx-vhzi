(defproject scramblie "0.1"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.238"]
  
                 ; SPA
                 [reagent "0.8.1"]
                 [re-frame "0.10.5"]
                 [cljs-ajax "0.8.0"]
                 [day8.re-frame/re-frame-10x "0.3.3"]
                 [day8.re-frame/tracing "0.5.1"]

                 ;SERVER
                 [ring "1.4.0"]
                 [compojure "1.5.0"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.3.2"]
 
                 [yogthos/config "0.8"]

                 [figwheel-sidecar "0.5.15"]
                 [cheshire "5.8.1"]

                 [clojure.java-time "0.3.2"]
                 [day8.re-frame/http-fx "0.1.4"]

                 [camel-snake-kebab "0.4.1"]
                 ]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-sassc "0.10.4"]
            ;[lein-jupyter "0.1.16"]
            ]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj", "src/cljs", "script", "resources"]

  :resource-paths ["resources"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs     ["resources/public/css"]
             :ring-handler scramblie.handler/dev-handler}

  :heroku {:app-name "scramblie-fx-vhzi"}

  :profiles {:dev     {:dependencies [[binaryage/devtools "0.9.10"]
                                      [day8.re-frame/re-frame-10x "0.3.3"]
                                      [day8.re-frame/tracing "0.5.1"]]
                       :plugins      [[lein-figwheel "0.5.16"]]}
             :prod    {:dependencies [[day8.re-frame/tracing-stubs "0.5.1"]]}

             :uberjar {:source-paths ["env/prod/clj"]
                       :dependencies [[day8.re-frame/tracing-stubs "0.5.1"]]
                       :omit-source  true
                       :main         scramblie.server
                       :aot          [scramblie.server]
                       :uberjar-name "scramblie.jar"
                       :prep-tasks   ["compile" ["cljsbuild" "once" "min"]]}}
  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src/cljs"]
                :figwheel     {:on-jsload "scramblie.core/mount-root"}
                :compiler     {:main                 scramblie.core
                               :output-to            "resources/public/js/compiled/app.js"
                               :output-dir           "resources/public/js/compiled/out"
                               :asset-path           "js/compiled/out"
                               :source-map-timestamp true
                               :preloads             [devtools.preload
                                                      day8.re-frame-10x.preload]
                               :closure-defines      {"re_frame.trace.trace_enabled_QMARK_"        true
                                                      "day8.re_frame.tracing.trace_enabled_QMARK_" true}
                               :external-config      {:devtools/config {:features-to-install :all}}}}

               {:id           "min"
                :source-paths ["src/cljs"]
                :jar          true
                :compiler     {:main            scramblie.core
                               :output-to       "resources/public/js/compiled/app.js"
                               :optimizations   :advanced
                               :closure-defines {goog.DEBUG false}
                               :pretty-print    false}}]})
