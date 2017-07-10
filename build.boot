(set-env!
 :source-paths #{"src"}

 :dependencies '[
                 [org.clojure/clojure "1.9.0-alpha17"]
                 [ring/ring-core "1.6.1"]
                 [clj-http "3.6.1"] 
                 [adzerk/bootlaces "0.1.13" :scope "test"]
                 ])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "0.0.1-SNAPSHOT")

(task-options!
 pom {:project 'couchemar.simple-ring-proxy
      :version +version+
      :description "HTTP proxy ring middleware"
      :scm {:url "https://github.com/couchemar/simple-ring-proxy"}})

(bootlaces! +version+)
