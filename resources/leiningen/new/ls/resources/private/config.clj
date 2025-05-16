;; Replace all "xxxxx" with your configuration
{:db-protocol  "mysql"
 :db-name      "//localhost:3306/xxxxx?characterEncoding=UTF-8&serverTimezone=America/Los_Angeles" ; Change me
 :database-url "mysql://localhost:3306/xxxxx?user=root&password=xxxxx&serverTimezone=America/Los_Angeles" ; Change me
 :db-user      "root"
 :db-pwd       "xxxxx" ; Change me
 :db-class     "com.mysql.cj.jdbc.Driver"
 :email-host   "xxxxx" ; Optional
 :email-user   "xxxxx" ; Optional
 :email-pwd    "xxxxx" ; Optional
 :port         3000
 :tz           "US/Pacific"
 :site-name    "xxxxx" ; Change me
 :company-name "xxxxx" ; Change me
 :uploads      "./uploads/xxxxx/" ; Change me
 :base-url     "http://0.0.0.0:3000/"
 :img-url      "https://0.0.0.0/uploads/"
 :path         "/uploads/"}
